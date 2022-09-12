package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.model.Coordinate;
import com.junior.company.ecommerce.model.GeocodingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingServiceImpl implements GeocodingService {

    private final RestTemplate restTemplate;
    private static final String API_KEY = "0b94ead55571ca7f9f0226212058fcee";
    private static final String API_BASE_URL = String.format("http://api.positionstack.com/v1/forward?access_key=%s&query=", API_KEY);

    @Override
    public Coordinate getCoordinates(String city, String country) {
        log.info("GeocodingServiceImpl: Getting coordinates for city: {} in country: {}", city, country);
        ResponseEntity<GeocodingResponse> response =
                restTemplate.getForEntity(String.format(API_BASE_URL + "%s %s", city, country), GeocodingResponse.class);

        GeocodingResponse geocodingResponse = response.getBody();
        if ((Objects.equals(geocodingResponse, null)) ||
                (Objects.equals(geocodingResponse.getCoordinates(), null)) ||
                geocodingResponse.getCoordinates().length == 0) {
            throw new IllegalStateException(String.format("Localization for city: %s in country: %s not found", city, country));
        }
        return geocodingResponse.getCoordinates()[0];
    }
}
