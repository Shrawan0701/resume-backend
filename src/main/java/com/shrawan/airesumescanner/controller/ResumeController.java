package com.shrawan.airesumescanner.controller;

import com.shrawan.airesumescanner.dto.OptimizationResultDTO;
import com.shrawan.airesumescanner.dto.ResumeRoastRequest;
import com.shrawan.airesumescanner.dto.ResumeRoastResponse;
import com.shrawan.airesumescanner.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResultDTO> optimizeResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription) {

        OptimizationResultDTO result = resumeService.optimizeResume(resume, jobDescription);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/roast")
    public ResponseEntity<ResumeRoastResponse> roastResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam(value = "tone", required = false, defaultValue = "Honest") String tone) {

        ResumeRoastResponse response = resumeService.roastResume(resume, tone);
        return ResponseEntity.ok(response);
    }
}
