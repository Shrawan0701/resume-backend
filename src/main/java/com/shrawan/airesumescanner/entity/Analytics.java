package com.shrawan.airesumescanner.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analytics")
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "resume_text", columnDefinition = "TEXT")
    private String resumeText;

    @Column(name = "ats_score")
    private Integer atsScore;

    @Column(name = "ats_compatible")
    private boolean atsCompatible;

    @Column(name = "ats_feedback", columnDefinition = "TEXT")
    private String atsFeedback;

    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;


    public Analytics() {
    }

    public Analytics(LocalDateTime uploadTime, String jobDescription, String resumeText,
                           Integer atsScore, boolean atsCompatible, String atsFeedback, String suggestions) {
        this.uploadTime = uploadTime;
        this.jobDescription = jobDescription;
        this.resumeText = resumeText;
        this.atsScore = atsScore;
        this.atsCompatible = atsCompatible;
        this.atsFeedback = atsFeedback;
        this.suggestions = suggestions;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public Integer getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(Integer atsScore) {
        this.atsScore = atsScore;
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

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }
}
