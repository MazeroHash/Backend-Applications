package com.project.backend.pathShare.Utility;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.List;
import org.json.JSONObject; // Parses JSON responses



@Service
public class OpenStreetMapService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json";

    public double[] getCoordinates(String location) {
        String url = String.format(NOMINATIM_URL, location);
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);

        if (response != null && !response.isEmpty()) {
            Map<String, Object> locationData = response.get(0);
            double lat = Double.parseDouble(locationData.get("lat").toString());
            double lon = Double.parseDouble(locationData.get("lon").toString());
            return new double[]{lat, lon};
        }

        return new double[]{0, 0}; // Default if location not found
    }
    public String getAddressFromCoordinates(double latitude, double longitude) {
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject json = new JSONObject(response.getBody());
                return json.getJSONObject("address").getString("display_name"); // ✅ Extract full address
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Address Not Found";
    }

}

