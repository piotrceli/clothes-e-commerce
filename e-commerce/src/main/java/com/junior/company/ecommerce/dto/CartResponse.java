package com.junior.company.ecommerce.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class CartResponse {

    private Long id;
    private Double totalValue;
    private List<CartItemResponse> cartItems;
}
