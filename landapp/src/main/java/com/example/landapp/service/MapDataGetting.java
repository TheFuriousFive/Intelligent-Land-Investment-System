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


/*
RestClient acts as the middleman between our website and OpenStreetMap
*/

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
            // THE "TRANSLATION": RestClient automatically translates (deserializes)
            // complex raw JSON text into our structured Java DTO (OverpassResponseDTO).
            OverpassResponseDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/interpreter") // Ensure Config baseUrl is ONLY https://overpass-api.de
                            .queryParam("data", rawQuery)
                            .build())
                    .retrieve()
                    .body(OverpassResponseDTO.class);

            //  Using RestClient ensures we handle timeouts,
            // SSL security, and connection management so the website doesn't freeze.
            if (response == null || response.getElements() == null || response.getElements().isEmpty()) {
                return new MapDataResult("No Map Data Found", "None", "Unknown");
            }

            MapDataResult result = new MapDataResult();

            //Data mapping check
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
            System.err.println("Overpass API Failure: " + e.getMessage());
            return new MapDataResult("Error fetching data", "", "");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    // Helper class to hold the extracted data
    public static class MapDataResult {
        private String suggestedLandType;
        private String amenities;
        private String accessRoad;
    }
}
