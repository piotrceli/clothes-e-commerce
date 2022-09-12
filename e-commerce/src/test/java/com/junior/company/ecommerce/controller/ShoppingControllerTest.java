package com.junior.company.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.company.ecommerce.dto.CartResponse;
import com.junior.company.ecommerce.dto.OrderResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.mapper.ShoppingMapper;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.CartItem;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Order;
import com.junior.company.ecommerce.model.OrderItem;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.security.AppUserDetailsService;
import com.junior.company.ecommerce.service.ShoppingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShoppingController.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class ShoppingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShoppingService shoppingService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    private static Category category;
    private static Product product;
    private static Item item;
    private static CartItem cartItem;
    private static Cart cart;

    @BeforeAll
    static void beforeAll() {
        category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        product = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .categories(Set.of(category))
                .build();
        item = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .product(product)
                .build();
        product.setItems(List.of(item));
        cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(10)
                .build();
        cart = Cart.builder()
                .id(1L)
                .totalValue(0.0)
                .cartItems(List.of(cartItem))
                .build();
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldViewCart() throws Exception {

        // given
        CartResponse cartResponse = ShoppingMapper.mapCartToCartResponse(cart);

        given(shoppingService.viewCart()).willReturn(cartResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved view of cart")
                .data(Map.of("cart", shoppingService.viewCart()))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/shopping"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldAddCartItem_givenValidItemIdAndAmount() throws Exception {

        // given
        Long itemId = 1L;
        Integer amount = 1;

        given(shoppingService.addCartItem(anyLong(), anyInt())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Added item to cart")
                .data(Map.of("is_added", shoppingService.addCartItem(itemId, amount)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotAddCartItem_givenInvalidAmount() throws Exception {

        // given
        Long itemId = 1L;
        Integer amount = -10;

        given(shoppingService.addCartItem(anyLong(), anyInt())).willThrow(
                new IllegalStateException("Ordered Amount must be minimum 1"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Ordered Amount must be minimum 1")
                .build();

        // when then
        mockMvc.perform(put("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotAddCartItem_whenOrderedAmountExceedQuantityOfItem() throws Exception {

        // given
        Long itemId = 1L;
        Integer amount = 999999999;

        given(shoppingService.addCartItem(anyLong(), anyInt())).willThrow(
                new IllegalStateException("Ordered amount must not exceed quantity of item"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Ordered amount must not exceed quantity of item")
                .build();

        // when then
        mockMvc.perform(put("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotAddCartItem_givenInvalidItemId() throws Exception {

        // given
        Long itemId = 0L;
        Integer amount = 1;

        given(shoppingService.addCartItem(anyLong(), anyInt())).willThrow(
                new ResourceNotFoundException(String.format("Item with id: %s not found", itemId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Item with id: %s not found", itemId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldRemoveCartItem_givenValidItemIdAndAmount() throws Exception {

        // given
        Long itemId = 1L;
        Integer amount = 1;

        given(shoppingService.removeCartItem(anyLong(), anyInt())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Deleted item from cart")
                .data(Map.of("is_deleted", shoppingService.removeCartItem(itemId, amount)))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotRemoveCartItem_givenInvalidAmount() throws Exception {

        // given
        Long itemId = 1L;
        Integer amount = -10;

        given(shoppingService.removeCartItem(anyLong(), anyInt())).willThrow(
                new IllegalStateException("Ordered Amount must be minimum 1"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Ordered Amount must be minimum 1")
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotRemoveCartItem_whenAmountToDeleteExceedOrderedAmount() throws Exception {

        // given
        Long itemId = 1L;
        Integer amount = 999999999;

        given(shoppingService.removeCartItem(anyLong(), anyInt())).willThrow(
                new IllegalStateException("Amount to delete is higher than actual ordered amount in cart"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Amount to delete is higher than actual ordered amount in cart")
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotRemoveCartItem_givenInvalidItemId() throws Exception {

        // given
        Long itemId = 0L;
        Integer amount = 1;

        given(shoppingService.removeCartItem(anyLong(), anyInt())).willThrow(
                new ResourceNotFoundException(String.format("Item with id: %s not found in cart", itemId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Item with id: %s not found in cart", itemId))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/shopping/{itemId}", itemId)
                        .param("amount", amount.toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldCheckoutCart() throws Exception {

        // given
        given(shoppingService.checkout()).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Checkout cart")
                .data(Map.of("is_checkout", shoppingService.checkout()))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/shopping/checkout"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotCheckoutCart_whenOrderAmountExceedQuantityOfItemInStock() throws Exception {

        // given
        given(shoppingService.checkout()).willThrow(
                new IllegalStateException("Ordered amount must not exceed quantity of item"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Ordered amount must not exceed quantity of item")
                .build();

        // when then
        mockMvc.perform(post("/api/v1/shopping/checkout"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldGetListOfOrders() throws Exception {

        // given
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .item(item)
                .amount(1)
                .build();
        Order order = Order.builder()
                .id(1L)
                .orderItems(List.of(orderItem))
                .totalValue(100.0)
                .dateOfOrder(LocalDateTime.of(2022, 1, 1, 20, 30))
                .build();

        List<Order> orders = new ArrayList<>(List.of(order));
        List<OrderResponse> orderResponses = ShoppingMapper.mapOrdersToOrderResponses(orders);
        given(shoppingService.viewOrders()).willReturn(orderResponses);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of orders")
                .data(Map.of("orders", shoppingService.viewOrders()))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/shopping/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }
}