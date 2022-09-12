package com.junior.company.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.junior.company.ecommerce.model.WeatherSeason;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private Long id;
    private String name;
    private WeatherSeason weatherSeason;
    private List<ProductRead> products;
}
