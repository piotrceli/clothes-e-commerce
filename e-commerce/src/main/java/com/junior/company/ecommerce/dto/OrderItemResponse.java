package com.junior.company.ecommerce.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class OrderItemResponse {

    private Long id;
    private ItemResponse item;
    private Integer amount;
}
