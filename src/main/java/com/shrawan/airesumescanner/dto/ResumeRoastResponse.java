package com.shrawan.airesumescanner.dto;

public class ResumeRoastResponse {
    private String feedback;

    public ResumeRoastResponse() {
    }

    public ResumeRoastResponse(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "ResumeRoastResponse{" +
                "feedback='" + feedback + '\'' +
                '}';
    }
}
