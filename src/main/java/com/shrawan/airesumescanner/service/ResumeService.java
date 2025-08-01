package com.shrawan.airesumescanner.service;

import com.shrawan.airesumescanner.dto.OptimizationResultDTO;
import com.shrawan.airesumescanner.dto.ResumeRoastResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {

    OptimizationResultDTO optimizeResume(MultipartFile resumeFile, String jobDescription);


    ResumeRoastResponse roastResume(MultipartFile resumeFile, String tone);
}
