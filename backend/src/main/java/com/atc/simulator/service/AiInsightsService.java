package com.atc.simulator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;

@Service
public class AiInsightsService {

    @Value("${api.aviationstack.key}")
    private String aviationKey;

    @Value("${api.weatherapi.key}")
    private String weatherKey;

    @Value("${api.gemini.key}")
    private String geminiKey;

    private final RestTemplate restTemplate;

    public AiInsightsService() {
        this.restTemplate = new RestTemplate();
    }

    public String getWeatherInfo(double lat, double lon) {
        try {
            String url = "http://api.weatherapi.com/v1/current.json?key=" + weatherKey + "&q=" + lat + "," + lon;

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("current")) {
                Map<String, Object> current = (Map<String, Object>) response.get("current");
                Map<String, Object> conditionMap = (Map<String, Object>) current.get("condition");
                String condition = conditionMap.get("text").toString();
                double tempC = Double.parseDouble(current.get("temp_c").toString());
                double windKph = Double.parseDouble(current.get("wind_kph").toString());
                double visKm = Double.parseDouble(current.get("vis_km").toString());
                
                return String.format("Current weather: %s. Temp: %.1f C. Wind: %.1f kph. Visibility: %.1f km.", 
                                      condition, tempC, windKph, visKm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Weather data unavailable.";
    }

    public String getAviationInfo() {
        try {
            String url = "http://api.aviationstack.com/v1/flights?access_key=" + aviationKey + "&limit=5";

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
                StringBuilder sb = new StringBuilder("Active local flights data snippet: ");
                int count = 0;
                for (Map<String, Object> flightNode : data) {
                    if (count >= 3) break;
                    
                    Map<String, Object> flight = (Map<String, Object>) flightNode.get("flight");
                    Map<String, Object> departure = (Map<String, Object>) flightNode.get("departure");
                    Map<String, Object> arrival = (Map<String, Object>) flightNode.get("arrival");
                    
                    String flightNo = flight != null && flight.get("iata") != null ? flight.get("iata").toString() : "Unknown";
                    String status = flightNode.get("flight_status") != null ? flightNode.get("flight_status").toString() : "Unknown";
                    String dep = departure != null && departure.get("iata") != null ? departure.get("iata").toString() : "Unknown";
                    String arr = arrival != null && arrival.get("iata") != null ? arrival.get("iata").toString() : "Unknown";
                    
                    sb.append(String.format("Flight %s: %s (Dep: %s, Arr: %s). ", flightNo, status, dep, arr));
                    count++;
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Live aviation data unavailable.";
    }

    public String generateGeminiInsight(String weatherContext, String aviationContext, int activeFlightsCount) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiKey;
            
            String promptText = String.format("You are an AI assistant for an Air Traffic Control Simulation System. " +
                    "Analyze the current data and provide a concise, maximum 3-sentence summary highlighting any potential " +
                    "delays, weather risks, or airspace crowding based on this information:\n" +
                    "- Weather: %s\n" +
                    "- Active Flights in Sector: %d\n" +
                    "- General Scheduled Aviation Scope: %s\n" + 
                    "Respond with only the brief ATC advisory summary as if advising a controller.", 
                    weatherContext, activeFlightsCount, aviationContext);

            String requestBody = "{\n" +
                    "  \"contents\": [{\n" +
                    "    \"parts\":[{\"text\": \"" + promptText.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]\n" +
                    "  }]\n" +
                    "}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content= (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        return parts.get(0).get("text").toString();
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to generate AI insight at this time. " + e.getMessage();
        }
        return "AI analysis unavailable.";
    }
}
