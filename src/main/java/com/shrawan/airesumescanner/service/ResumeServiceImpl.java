package com.shrawan.airesumescanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shrawan.airesumescanner.dto.OptimizationResultDTO;
import com.shrawan.airesumescanner.dto.ResumeRoastResponse;
import com.shrawan.airesumescanner.entity.Analytics;
import com.shrawan.airesumescanner.repository.AnalyticsRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final AnalyticsRepository analyticsRepository;

    @Autowired
    public ResumeServiceImpl(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Override
    public OptimizationResultDTO optimizeResume(MultipartFile resume, String jobDescription) {
        try {
            Tika tika = new Tika();
            String resumeText = tika.parseToString(resume.getInputStream());

            String promptJobDescription = jobDescription.trim().length() < 20
                    ? "Job Title: " + jobDescription.trim()
                    : jobDescription;

            String prompt = String.format("""
                        You are an AI assistant that helps improve resumes for job applications.

                        --- Job Description ---
                        %s

                        --- Resume ---
                        %s

                        --- Task ---
                        Give clear, structured suggestions in bullet points to improve the resume for this role.
                    """, promptJobDescription, resumeText);

            ensureOllamaReady();
            String response = callOllamaAPI(prompt);
            String suggestions = extractContentFromOllama(response);
            if (suggestions == null || suggestions.isEmpty()) {
                suggestions = "No suggestions provided by AI.";
            }

            int atsScore = calculateAtsScore(resumeText, promptJobDescription);
            boolean atsCompatible = atsScore >= 60;
            String atsFeedback = atsCompatible
                    ? "Resume is ATS compatible."
                    : "Resume is not ATS compatible. Try adding more keywords from the job description.";

            Analytics analytics = new Analytics();
            analytics.setSuggestions(suggestions);
            analytics.setAtsScore(atsScore);
            analytics.setAtsCompatible(atsCompatible);
            analytics.setAtsFeedback(atsFeedback);
            analyticsRepository.save(analytics);

            String encodedPdf = generateSummaryPdf(suggestions, atsScore, atsFeedback);

            OptimizationResultDTO dto = new OptimizationResultDTO();
            dto.setSuggestions(suggestions);
            dto.setAtsScore(atsScore);
            dto.setAtsCompatible(atsCompatible);
            dto.setAtsFeedback(atsFeedback);
            dto.setOptimizedResume("Optimized resume based on the job description.");
            dto.setOptimizedPdf(encodedPdf);

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Resume optimization failed", e);
        }
    }

    @Override
    public ResumeRoastResponse roastResume(MultipartFile resumeFile, String tone) {
        try {
            Tika tika = new Tika();
            String resumeText = tika.parseToString(resumeFile.getInputStream());

            String prompt = generateRoastPrompt(resumeText, tone);
            ensureOllamaReady();

            String response = callOllamaAPI(prompt);
            String roast = extractContentFromOllama(response);

            return new ResumeRoastResponse(roast != null ? roast : "❌ No roast generated.");

        } catch (Exception e) {
            return new ResumeRoastResponse("❌ Failed to generate roast: " + e.getMessage());
        }
    }

    private String generateRoastPrompt(String resumeText, String tone) {
        String style = switch (tone.toLowerCase()) {
            case "friendly" -> "Give a supportive and witty roast of the following resume.";
            case "honest" -> "Give a brutally honest but helpful roast of the resume.";
            case "brutal" -> "Give a sarcastic and savage roast of this resume.";
            default -> "Give a witty and critical review of the following resume.";
        };
        return style + "\n\nResume:\n" + resumeText;
    }

    private void ensureOllamaReady() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:11434/api/tags").openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Ollama is not ready. Please start the service.");
        }
    }

    private String callOllamaAPI(String prompt) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:11434/api/generate").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String escapedPrompt = prompt.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
        String body = String.format("{\"model\":\"llama3\",\"prompt\":\"%s\",\"stream\":false}", escapedPrompt);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            return output.toString();
        }
    }

    private String extractContentFromOllama(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResponse);
        return root.has("response") ? root.get("response").asText().trim() : null;
    }

    private int calculateAtsScore(String resumeText, String jobDescription) {
        String[] jdKeywords = jobDescription.toLowerCase().split("\\W+");
        String resumeLower = resumeText.toLowerCase();

        int matches = 0, total = 0;
        for (String word : jdKeywords) {
            if (word.length() > 2) {
                total++;
                if (resumeLower.contains(word)) matches++;
            }
        }

        if (total == 0) return 30 + (int) (Math.random() * 20);
        double ratio = (double) matches / total;
        return Math.max(30, Math.min(95, (int) Math.round(ratio * 100 + (Math.random() * 10 - 5))));
    }

    private String generateSummaryPdf(String suggestions, int atsScore, String atsFeedback) throws IOException {
        PDDocument doc = new PDDocument();

        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDPageContentStream content = new PDPageContentStream(doc, page);
        content.setFont(PDType1Font.HELVETICA_BOLD, 16);
        content.setLeading(18f);

        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin; // top of page
        float yPosition = yStart;

        content.beginText();
        content.newLineAtOffset(margin, yPosition);
        content.showText("Resume Optimization Summary");
        content.newLine();
        yPosition -= 18;

        content.setFont(PDType1Font.HELVETICA, 12);
        content.showText("ATS Score: " + atsScore);
        content.newLine();
        yPosition -= 18;

        content.showText("ATS Feedback: " + atsFeedback);
        content.newLine();
        yPosition -= 18;

        content.showText("Suggestions:");
        content.newLine();
        yPosition -= 18;

        // For each suggestion line
        for (String line : suggestions.split("\n")) {
            // Check if we need a new page
            if (yPosition <= margin) {
                content.endText();
                content.close();

                // Add new page
                page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                content = new PDPageContentStream(doc, page);
                content.setFont(PDType1Font.HELVETICA, 12);
                content.setLeading(18f);

                yPosition = yStart;

                content.beginText();
                content.newLineAtOffset(margin, yPosition);
            }
            content.showText("- " + line.trim());
            content.newLine();
            yPosition -= 18;
        }

        content.endText();
        content.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.save(out);
        doc.close();

        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}