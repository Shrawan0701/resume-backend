package com.shrawan.airesumescanner.dto;

public class OptimizationResultDTO {
    private String optimizedResume;
    private String suggestions;
    private String optimizedPdf;
    private boolean atsCompatible;
    private String atsFeedback;
    private int atsScore;  // 0 to 100

    // getters and setters

    public String getOptimizedResume() {
        return optimizedResume;
    }

    public void setOptimizedResume(String optimizedResume) {
        this.optimizedResume = optimizedResume;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getOptimizedPdf() {
        return optimizedPdf;
    }

    public void setOptimizedPdf(String optimizedPdf) {
        this.optimizedPdf = optimizedPdf;
    }

    public boolean isAtsCompatible() {
        return atsCompatible;
    }

    public void setAtsCompatible(boolean atsCompatible) {
        this.atsCompatible = atsCompatible;
    }

    public String getAtsFeedback() {
        return atsFeedback;
    }

    public void setAtsFeedback(String atsFeedback) {
        this.atsFeedback = atsFeedback;
    }

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }
}
