package com.example.landapp.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;



import java.time.Duration;

@Configuration
public class RestConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://overpass-api.de/api/interpreter")
                .build();
    }
}
