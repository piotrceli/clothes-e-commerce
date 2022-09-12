package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.CartResponse;
import com.junior.company.ecommerce.dto.OrderResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.CartItem;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Order;
import com.junior.company.ecommerce.model.OrderItem;
import com.junior.company.ecommerce.repository.CartItemRepository;
import com.junior.company.ecommerce.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.junior.company.ecommerce.mapper.ShoppingMapper.mapCartItemsToOrderItems;
import static com.junior.company.ecommerce.mapper.ShoppingMapper.mapCartToCartResponse;
import static com.junior.company.ecommerce.mapper.ShoppingMapper.mapOrdersToOrderResponses;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShoppingServiceImpl implements ShoppingService {

    private final AppUserService appUserService;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartResponse viewCart() {
        log.info("Retrieving view of cart for user: {}", appUserService.getCurrentUser().getEmail());
        Cart cart = appUserService.getCurrentUser().getCart();
        return mapCartToCartResponse(cart);
    }

    @Override
    public boolean addCartItem(Long itemId, Integer amount) {
        log.info("Adding item with id: {} to cart of user: {}", itemId, appUserService.getCurrentUser().getEmail());

        if (amount < 1) {
            throw new IllegalStateException("Ordered amount must be minimum 1");
        }

        Item existingItem = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Item with id: %s not found", itemId)));

        if (existingItem.getQuantity() < amount) {
            throw new IllegalStateException("Ordered amount must not exceed quantity of item");
        }

        Cart cart = appUserService.getCurrentUser().getCart();

        boolean existed = false;
        for (CartItem cartItem : cart.getCartItems()) {
            if (Objects.equals(cartItem.getItem().getId(), existingItem.getId())) {
                existed = true;
                if (cartItem.getAmount() + amount > existingItem.getQuantity()) {
                    throw new IllegalStateException("Ordered amount must not exceed stock quantity of item");
                }
                cartItem.setAmount(cartItem.getAmount() + amount);
            }
        }
        if (!existed) {
            CartItem newCartItem = CartItem.builder()
                    .item(existingItem)
                    .amount(amount)
                    .build();
            cartItemRepository.save(newCartItem);
            cart.getCartItems().add(newCartItem);
        }
        cart.setTotalValue(cart.getTotalValue() + existingItem.getProduct().getPrice() * amount);
        return true;
    }

    @Override
    public boolean removeCartItem(Long itemId, Integer amount) {
        log.info("Removing item with id: {} from cart of user: {}",
                itemId, appUserService.getCurrentUser().getEmail());

        if (amount < 1) {
            throw new IllegalStateException("Amount to delete must be minimum 1");
        }

        Cart cart = appUserService.getCurrentUser().getCart();

        for (CartItem cartItem : cart.getCartItems()) {
            if (Objects.equals(cartItem.getItem().getId(), itemId)) {
                if (amount > cartItem.getAmount()) {
                    throw new IllegalStateException("Amount to delete is higher than actual ordered amount in cart");
                }
                cartItem.setAmount(cartItem.getAmount() - amount);
                if (Objects.equals(cartItem.getAmount(), 0)) {
                    cartItemRepository.delete(cartItem);
                }
                cart.setTotalValue(cart.getTotalValue() - cartItem.getItem().getProduct().getPrice() * amount);
                return true;
            }
        }
        throw new IllegalStateException(String.format("Item with id: %s not found in cart", itemId));
    }

    @Override
    public boolean checkout() {
        log.info("Shopping cart checkout for user: {}", appUserService.getCurrentUser().getEmail());
        Cart cart = appUserService.getCurrentUser().getCart();

        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getAmount() > cartItem.getItem().getQuantity()) {
                throw new IllegalStateException("Ordered amount must not exceed quantity of item");
            }
        }

        Order order = Order.builder()
                .orderItems(new ArrayList<>())
                .totalValue(cart.getTotalValue())
                .dateOfOrder(LocalDateTime.now())
                .build();

        List<OrderItem> orderItems = mapCartItemsToOrderItems(cart.getCartItems());
        for (OrderItem orderItem : orderItems) {
            order.getOrderItems().add(orderItem);
            orderItem.getItem().setQuantity(orderItem.getItem().getQuantity() - orderItem.getAmount());
        }
        appUserService.getCurrentUser().getOrders().add(order);

        cart.setTotalValue(0.0);
        cart.getCartItems().forEach(cartItemRepository::delete);
        return true;
    }

    @Override
    public List<OrderResponse> viewOrders() {
        log.info("Retrieving list of orders for user: {}", appUserService.getCurrentUser().getEmail());
        List<Order> orders = appUserService.getCurrentUser().getOrders();
        return mapOrdersToOrderResponses(orders);
    }
}
















