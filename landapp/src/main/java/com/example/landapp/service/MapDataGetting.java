package com.example.landapp.service;

import com.example.landapp.dto.OverpassResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MapDataGetting {

    private final RestClient restClient;

    public MapDataGetting(RestClient restClient) {
        this.restClient = restClient;
    }

    public MapDataResult getLandContext(Double lat, Double lon) {

        String rawQuery = String.format(
                "[out:json];(nwr(around:150, %f, %f););out tags center;",
                lat, lon
        );

        try {
            OverpassResponseDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/interpreter") // Ensure Config baseUrl is ONLY https://overpass-api.de
                            .queryParam("data", rawQuery)
                            .build())
                    .retrieve()
                    .body(OverpassResponseDTO.class);

            // Correct Check: Ensure response and internal list exist
            if (response == null || response.getElements() == null || response.getElements().isEmpty()) {
                return new MapDataResult("No Map Data Found", "None", "Unknown");
            }

            MapDataResult result = new MapDataResult();

            // DATA MAPPING CHECK: Correct use of Java Streams
            result.setAmenities(response.getElements().stream()
                    .map(e -> e.getTags() != null ? e.getTags().get("amenity") : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.joining(", ")));

            result.setSuggestedLandType(response.getElements().stream()
                    .map(e -> e.getTags() != null ?
                            (e.getTags().get("landuse") != null ? e.getTags().get("landuse") : e.getTags().get("natural"))
                            : null)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("Unknown"));

            result.setAccessRoad(response.getElements().stream()
                    .map(e -> e.getTags() != null ? e.getTags().get("highway") : null)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("Access Road Not Mapped"));

            return result;
        } catch (Exception e) {
            // Log the actual error so you can see it in your console
            System.err.println("Overpass API Failure: " + e.getMessage());
            return new MapDataResult("Error fetching data", "", "");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapDataResult {
        private String suggestedLandType;
        private String amenities;
        private String accessRoad;
    }
}
/*public class MapDataGetting {

    private final RestClient restClient;

    // Use a constructor to let Spring "Inject" the bean
    public MapDataGetting(RestClient restClient) {
        this.restClient= restClient;
    }

    private final String INTERPRETER_URL = "https://overpass-api.de/api/interpreter";

    public MapDataResult getLandContext(Double lat,Double lon) {
        //Query looks for features within 150 meters
        String rawQuery = String.format(
                "[out:json];(nwr(around:150, %f, %f););out tags center;",
                lat,lon
        );

        try {
            OverpassResponseDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/interpreter")
                            .queryParam("data", rawQuery)
                            .build())
                    .retrieve()
                    .body(OverpassResponseDTO.class);

            if (response == null || response.getElements() == null) return new MapDataResult();

            MapDataResult result = new MapDataResult();

            // 1. Get Amenities (Schools, Parks, etc.)
            result.setAmenities(response.getElements().stream()
                    .map(e -> e.getTags().get("amenity"))
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.joining(", ")));

            // 2. Get Land Use (Residential, Commercial, Forest)
            result.setSuggestedLandType(response.getElements().stream()
                    .map(e -> e.getTags().get("landuse") != null ? e.getTags().get("landuse") : e.getTags().get("natural"))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("Unknown"));

            // 3. Get Road Type (Residential road, Primary, etc.)
            result.setAccessRoad(response.getElements().stream()
                    .map(e -> e.getTags().get("highway"))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("Access Road Not Mapped"));

            return result;
        } catch (Exception e) {
            // Return empty result to allow the website to continue without map data
            return new MapDataResult("Error fetching data", "", "");
        }
    }

    // Static inner class for the result
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapDataResult {
        private String suggestedLandType;
        private String amenities;
        private String accessRoad;
    }

}
*/
