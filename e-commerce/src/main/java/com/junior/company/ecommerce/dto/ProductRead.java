package com.junior.company.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRead {

    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
}
