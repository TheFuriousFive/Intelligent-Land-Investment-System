package com.example.landapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverpassResponseDTO {

    private List<Elements> elements;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Elements{
        private Map<String,String> tags;
    }
}
