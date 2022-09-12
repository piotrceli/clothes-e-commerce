package com.junior.company.ecommerce.controller;

import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.service.ShoppingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

import static com.junior.company.ecommerce.swagger.SwaggerConstants.SHOPPING_API_TAG;

@RestController
@RequestMapping("api/v1/shopping")
@RequiredArgsConstructor
@Api(tags = {SHOPPING_API_TAG})
public class ShoppingController {

    private final ShoppingService shoppingService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Get a list of all products in the shopping cart", notes = "Available for USER\n\n" +
            "Allows to view a list of all products in the shopping cart of the current logged user.")
    public ResponseEntity<Response> viewCart() {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved view of cart")
                .data(Map.of("cart", shoppingService.viewCart()))
                .build());
    }

    @PutMapping("{itemId}")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Add an item/increase the amount of the product", notes = "Available for USER\n\n" +
            "Allows to add a new item or increase the amount of the existing item in the shopping cart.")
    public ResponseEntity<Response> addCartItem(@PathVariable Long itemId,
                                            @RequestParam Integer amount) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Added item to cart")
                .data(Map.of("is_added", shoppingService.addCartItem(itemId, amount)))
                .build());
    }

    @DeleteMapping("{itemId}")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Remove an item/reduce the amount of product", notes = "Available for USER\n\n" +
            "Allows to remove an item or reduce the amount of the existing item in the shopping cart.")
    public ResponseEntity<Response> removeCartItem(@PathVariable Long itemId,
                                               @RequestParam Integer amount) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Deleted item from cart")
                .data(Map.of("is_deleted", shoppingService.removeCartItem(itemId, amount)))
                .build());
    }

    @PostMapping("checkout")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Checkout a shopping cart", notes = "Available for USER\n\n" +
            "Allows to checkout the shopping cart of the current logged user. " +
            "The operation is decreasing the quantity in the stock, " +
            "adding an order to the user's order history. " +
            "For the simplicity: checkout does not require payment details.")
    public ResponseEntity<Response> checkout() {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Checkout cart")
                .data(Map.of("is_checkout", shoppingService.checkout()))
                .build());
    }

    @GetMapping("orders")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "View a list of orders", notes = "Available for USER\n\n" +
            "Allows to view a history of orders for the current logged user.")
    public ResponseEntity<Response> viewOrders() {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of orders")
                .data(Map.of("orders", shoppingService.viewOrders()))
                .build());
    }
}
