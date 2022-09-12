package com.junior.company.ecommerce.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ItemRead {

    private Long id;
    private String size;
    private Integer quantity;
}
