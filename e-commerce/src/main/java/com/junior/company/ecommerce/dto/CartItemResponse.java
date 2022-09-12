package com.junior.company.ecommerce.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CartItemResponse {

    private Long id;
    private ItemResponse item;
    private Integer amount;
}
