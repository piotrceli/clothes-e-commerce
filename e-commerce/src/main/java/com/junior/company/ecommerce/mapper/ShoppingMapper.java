package com.junior.company.ecommerce.mapper;

import com.junior.company.ecommerce.dto.CartItemResponse;
import com.junior.company.ecommerce.dto.CartResponse;
import com.junior.company.ecommerce.dto.OrderItemResponse;
import com.junior.company.ecommerce.dto.OrderResponse;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.CartItem;
import com.junior.company.ecommerce.model.Order;
import com.junior.company.ecommerce.model.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

import static com.junior.company.ecommerce.mapper.constant.SharedConstant.EMPTY_ID;

public class ShoppingMapper {

    public static CartResponse mapCartToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .totalValue(cart.getTotalValue())
                .cartItems(mapCartItemsToCartItemResponses(cart.getCartItems()))
                .build();
    }

    public static List<OrderResponse> mapOrdersToOrderResponses(List<Order> orders) {
        return orders.stream().map((order) ->
                        OrderResponse.builder()
                                .id(order.getId())
                                .orderItems(mapOrderItemsToOrderItemResponses(order.getOrderItems()))
                                .totalValue(order.getTotalValue())
                                .dateOfOrder(order.getDateOfOrder())
                                .build())
                .collect(Collectors.toList());
    }

    public static List<OrderItem> mapCartItemsToOrderItems(List<CartItem> cartItems) {
        return cartItems.stream().map((cartItem) ->
                        OrderItem.builder()
                                .id(EMPTY_ID)
                                .item(cartItem.getItem())
                                .amount(cartItem.getAmount())
                                .build())
                .collect(Collectors.toList());

    }

    private static List<OrderItemResponse> mapOrderItemsToOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream().map((orderItem) ->
                        OrderItemResponse.builder()
                                .id(orderItem.getId())
                                .item(ItemMapper.mapItemToItemResponse(orderItem.getItem()))
                                .amount(orderItem.getAmount())
                                .build())
                .collect(Collectors.toList());
    }

    private static List<CartItemResponse> mapCartItemsToCartItemResponses(List<CartItem> cartItems) {
        return cartItems.stream().map((cartItem) ->
                        CartItemResponse.builder()
                                .id(cartItem.getId())
                                .item(ItemMapper.mapItemToItemResponse(cartItem.getItem()))
                                .amount(cartItem.getAmount())
                                .build())
                .collect(Collectors.toList());
    }
}
