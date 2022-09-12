package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.CartResponse;
import com.junior.company.ecommerce.dto.OrderResponse;

import java.util.List;

public interface ShoppingService {

    CartResponse viewCart();

    boolean addCartItem(Long itemId, Integer amount);

    boolean removeCartItem(Long itemId, Integer amount);

    boolean checkout();

    List<OrderResponse> viewOrders();
}
