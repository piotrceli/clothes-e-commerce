package com.junior.company.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class WeatherResponse {

    @JsonProperty("main")
    private Temperature temperature;
}
