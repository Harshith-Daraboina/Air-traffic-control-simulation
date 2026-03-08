package com.atc.simulator.controller;

import com.atc.simulator.model.AiInsightResponse;
import com.atc.simulator.service.AiInsightsService;
import com.atc.simulator.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insights")
@CrossOrigin(origins = "http://localhost:5173")
public class InsightsController {

    private final AiInsightsService aiInsightsService;
    private final FlightService flightService;

    @Autowired
    public InsightsController(AiInsightsService aiInsightsService, FlightService flightService) {
        this.aiInsightsService = aiInsightsService;
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<AiInsightResponse> getInsights(@RequestParam double lat, @RequestParam double lon) {
        String weather = aiInsightsService.getWeatherInfo(lat, lon);
        String aviation = aiInsightsService.getAviationInfo();
        int localActiveCount = flightService.getAllFlights().size();
        
        String aiAnalysis = aiInsightsService.generateGeminiInsight(weather, aviation, localActiveCount);
        
        AiInsightResponse response = new AiInsightResponse(weather, aviation, aiAnalysis);
        return ResponseEntity.ok(response);
    }
}
