package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.model.Coordinate;
import com.junior.company.ecommerce.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;
    private final GeocodingService geocodingService;
    private static final String API_KEY = "9445f36791376ddd9f243c26fe55066d";
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=" + API_KEY;

    @Override
    public Double getTemperature(String city, String country) {
        log.info("WeatherServiceImpl: Getting temperature for city: {} in country: {}", city, country);

        Coordinate coordinate = geocodingService.getCoordinates(city, country);
        Double latitude = coordinate.getLatitude();
        Double longitude = coordinate.getLongitude();

        ResponseEntity<WeatherResponse> response =
                restTemplate.getForEntity(String.format(API_BASE_URL, latitude, longitude), WeatherResponse.class);

        WeatherResponse weatherResponse = response.getBody();
        if (Objects.equals(weatherResponse, null) ||
                (Objects.equals(weatherResponse.getTemperature(), null))) {
            throw new IllegalStateException("No temperature information is available for inserted location");
        }
        return kelvinToCelsius(weatherResponse.getTemperature().getValue());
    }

    private Double kelvinToCelsius(Double kelvinValue) {
        return kelvinValue - 273.15;
    }
}
