package com.shrawan.airesumescanner.dto;

public class ResumeRoastRequest {
    private String tone; // Optional: "Friendly", "Honest", or "Brutal"

    public ResumeRoastRequest() {
    }

    public ResumeRoastRequest(String tone) {
        this.tone = tone;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    @Override
    public String toString() {
        return "ResumeRoastRequest{" +
                "tone='" + tone + '\'' +
                '}';
    }
}
