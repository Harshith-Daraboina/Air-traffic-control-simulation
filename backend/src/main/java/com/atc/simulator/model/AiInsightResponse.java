package com.atc.simulator.model;

public class AiInsightResponse {
    private String weatherSummary;
    private String aviationSummary;
    private String aiAnalysis;

    public AiInsightResponse() {}

    public AiInsightResponse(String weatherSummary, String aviationSummary, String aiAnalysis) {
        this.weatherSummary = weatherSummary;
        this.aviationSummary = aviationSummary;
        this.aiAnalysis = aiAnalysis;
    }

    public String getWeatherSummary() {
        return weatherSummary;
    }

    public void setWeatherSummary(String weatherSummary) {
        this.weatherSummary = weatherSummary;
    }

    public String getAviationSummary() {
        return aviationSummary;
    }

    public void setAviationSummary(String aviationSummary) {
        this.aviationSummary = aviationSummary;
    }

    public String getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(String aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }
}
