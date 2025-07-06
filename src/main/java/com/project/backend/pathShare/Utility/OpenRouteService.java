package com.project.backend.pathShare.Utility;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouteService {
    @Value("${openroute.api.key}")
    private String apiKey;

    private static final String ORS_URL = "https://api.openrouteservice.org/v2/directions/driving-car";

    public boolean isValidRoute(double originLat, double originLon, double destLat, double destLon,
                                double newPickupLat, double newPickupLon, double newDropLat, double newDropLon) {
        // Create JSON request body
        String requestBody = "{ \"coordinates\": ["
                + "[" + originLon + ", " + originLat + "],"
                + "[" + newPickupLon + ", " + newPickupLat + "],"
                + "[" + newDropLon + ", " + newDropLat + "],"
                + "[" + destLon + ", " + destLat + "]"
                + "]}";

        // Set up RestTemplate and headers
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openrouteservice.org/v2/directions/driving-car/json", entity, Map.class);

        // Log response
        System.out.println("ORS API Response: " + response.getBody());

        // Return true if a valid route exists
        return response.getBody() != null && response.getBody().containsKey("routes");
    }

    public Map<String, Object> getRoute(double pickupLat, double pickupLon, double dropLat, double dropLon) {
        RestTemplate restTemplate = new RestTemplate();

        String url = ORS_URL + "?api_key=" + apiKey +
                "&start=" + pickupLon + "," + pickupLat +
                "&end=" + dropLon + "," + dropLat;

        try {
            return restTemplate.getForObject(url, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>(); // Return empty map on failure
        }
    }

    public double getEstimatedTravelTime(double pickupLat, double pickupLon, double dropLat, double dropLon) {
        Map<String, Object> response = getRoute(pickupLat, pickupLon, dropLat, dropLon);

        if (response.containsKey("routes")) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            if (!routes.isEmpty() && routes.get(0).containsKey("duration")) {
                return (double) routes.get(0).get("duration") / 60; // Convert seconds to minutes
            }
        }
        return 0; // Default if route isn't found
    }


}
