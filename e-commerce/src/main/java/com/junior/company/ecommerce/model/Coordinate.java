package com.junior.company.ecommerce.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class Coordinate {

    private Double latitude;
    private Double longitude;
}