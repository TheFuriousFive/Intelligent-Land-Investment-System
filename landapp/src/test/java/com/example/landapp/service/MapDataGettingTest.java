package com.example.landapp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MapDataGettingTest {
    @Autowired
    private MapDataGetting mapDataGetting;

    @Test
    void testGetLandContext() {
        // Test coordinates (e.g., a park in London or a known location)
        Double testLat = 7.0897;
        Double testLon = 79.9925;

        var result = mapDataGetting.getLandContext(testLat, testLon);

        // Check if results are not null
        assertNotNull(result);
        System.out.println("Suggested Land: " + result.getSuggestedLandType());
        System.out.println("Amenities: " + result.getAmenities());
    }
}