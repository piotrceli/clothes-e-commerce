package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.model.Coordinate;
import com.junior.company.ecommerce.model.Temperature;
import com.junior.company.ecommerce.model.WeatherResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class WeatherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @Test
    void shouldGetTemperature() {

        // given
        String city = "city";
        String country = "country";
        Coordinate coordinate = Coordinate.builder()
                .latitude(50.0)
                .longitude(50.0)
                .build();

        given(geocodingService.getCoordinates(anyString(), anyString())).willReturn(coordinate);

        Temperature temperature = Temperature.builder()
                .value(293.15)
                .build();
        WeatherResponse weatherResponse = WeatherResponse.builder()
                .temperature(temperature)
                .build();

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(weatherResponse, HttpStatus.OK));

        Double tempInCelsius = 293.15 - 273.15;

        // when
        Double result = weatherService.getTemperature(city, country);

        // then
        assertThat(result).isEqualTo(tempInCelsius);
    }

    @Test
    void shouldThrowIllegalStateException_whenResponseIsNull() {

        // given
        String city = "city";
        String country = "country";
        Coordinate coordinate = Coordinate.builder()
                .latitude(50.0)
                .longitude(50.0)
                .build();

        given(geocodingService.getCoordinates(anyString(), anyString())).willReturn(coordinate);

        WeatherResponse weatherResponse = null;

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(weatherResponse, HttpStatus.OK));

        // when then
        assertThatThrownBy(() -> weatherService.getTemperature(city, country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No temperature information is available for inserted location");
    }

    @Test
    void shouldThrowIllegalStateException_whenTemperatureIsNull() {

        // given
        String city = "city";
        String country = "country";
        Coordinate coordinate = Coordinate.builder()
                .latitude(50.0)
                .longitude(50.0)
                .build();

        given(geocodingService.getCoordinates(anyString(), anyString())).willReturn(coordinate);

        WeatherResponse weatherResponse = WeatherResponse.builder()
                .temperature(null)
                .build();

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(weatherResponse, HttpStatus.OK));

        // when then
        assertThatThrownBy(() -> weatherService.getTemperature(city, country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No temperature information is available for inserted location");
    }
}