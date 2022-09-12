package com.junior.company.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private String description;
    private List<ItemRead> items;
    private Set<CategoryResponse> categories;
}
