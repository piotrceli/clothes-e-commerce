package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.model.Coordinate;
import com.junior.company.ecommerce.model.GeocodingResponse;
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
class GeocodingServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeocodingServiceImpl geocodingService;

    @Test
    void shouldGetCoordinates() {

        // given
        String city = "city";
        String country = "country";

        Coordinate coordinate = Coordinate.builder()
                .latitude(50.0)
                .longitude(50.0)
                .build();

        Coordinate[] coordinates = {coordinate};
        GeocodingResponse geocodingResponse = GeocodingResponse
                .builder()
                .coordinates(coordinates)
                .build();

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(geocodingResponse, HttpStatus.OK));

        // when
        Coordinate result = geocodingService.getCoordinates(city, country);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(coordinate);
    }

    @Test
    void shouldThrowIllegalStateException_whenResponseCoordinatesIsNull() {

        // given
        String city = "city";
        String country = "country";

        GeocodingResponse geocodingResponse = GeocodingResponse
                .builder()
                .coordinates(null)
                .build();

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(geocodingResponse, HttpStatus.OK));

        // when then
        assertThatThrownBy(() -> geocodingService.getCoordinates(city, country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(
                        "Localization for city: %s in country: %s not found", city, country));
    }

    @Test
    void shouldThrowIllegalStateException_whenResponseIsNull() {

        // given
        String city = "city";
        String country = "country";

        GeocodingResponse geocodingResponse = null;

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(geocodingResponse, HttpStatus.OK));

        // when then
        assertThatThrownBy(() -> geocodingService.getCoordinates(city, country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(
                        "Localization for city: %s in country: %s not found", city, country));
    }

    @Test
    void shouldThrowIllegalStateException_whenResponseCoordinatesIsEmpty() {

        // given
        String city = "city";
        String country = "country";

        GeocodingResponse geocodingResponse = GeocodingResponse
                .builder()
                .coordinates(new Coordinate[0])
                .build();

        given(restTemplate.getForEntity(anyString(), any()))
                .willReturn(new ResponseEntity<>(geocodingResponse, HttpStatus.OK));

        // when then
        assertThatThrownBy(() -> geocodingService.getCoordinates(city, country))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(
                        "Localization for city: %s in country: %s not found", city, country));
    }
}