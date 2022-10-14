package com.junior.company.ecommerce.controller;

import com.junior.company.ecommerce.dto.ItemRequest;
import com.junior.company.ecommerce.dto.ProductRequest;
import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.junior.company.ecommerce.swagger.SwaggerConstants.PRODUCTS_API_TAG;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
@Api(tags = {PRODUCTS_API_TAG})
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ApiOperation(value = "Get a list of all products", notes = "Available for EVERYONE\n\n" +
            "Allows to view a list of all products registered in the system.")
    public ResponseEntity<Response> findProducts(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                 @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of products")
                .data(Map.of("products", productService.findProductsPage(page, size)))
                .build());
    }

    @GetMapping("{productId}")
    @ApiOperation(value = "Get a product by id", notes = "Available for EVERYONE\n\n" +
            "Allows to view a product by id.")
    public ResponseEntity<Response> findProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved product by id: %s", productId))
                .data(Map.of("product", productService.findProductById(productId)))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Create a new product", notes = "Available for ADMIN\n\n" +
            "Allows to register a new product to the system.")
    public ResponseEntity<Response> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/products").toUriString());
        return ResponseEntity.created(uri).body(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Created new product")
                .data(Map.of("product", productService.addProduct(productRequest)))
                .build());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Update an existing product", notes = "Available for ADMIN\n\n" +
            "Allows to update an existing product.")
    public ResponseEntity<Response> updateProduct(@Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated product")
                .data(Map.of("product", productService.updateProduct(productRequest)))
                .build());
    }

    @DeleteMapping("{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete an existing product by id", notes = "Available for ADMIN\n\n" +
            "Allows to delete an existing product by id from the system.")
    public ResponseEntity<Response> deleteProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted product with id: %s", productId))
                .data(Map.of("is_deleted", productService.deleteProductById(productId)))
                .build());
    }

    @GetMapping("match-to-weather")
    @ApiOperation(value = "Get a list of all products matched to actual weather",
            notes = "Available for EVERYONE\n\n" +
            "Allows to view a list of products registered in the system that matches to actual weather.")
    public ResponseEntity<Response> findProductsMatchToWeather(@RequestParam String city,
                                                               @RequestParam String country,
                                                               @RequestParam(required = false, defaultValue = "0") Integer page,
                                                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of products matched to actual weather")
                .data(Map.of("temperature", productService.getTemperature(city, country),
                        "products", productService.findProductsMatchToWeather(city, country, page, size)))
                .build());
    }

    @PostMapping("items/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Create a new item", notes = "Available for ADMIN\n\n" +
            "Allows to register a new item of the specific product in the system.")
    public ResponseEntity<Response> addItem(@PathVariable Long productId,
                                            @Valid @RequestBody ItemRequest itemRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path(String.format("/api/v1/products/%s", productId)).toUriString());
        return ResponseEntity.created(uri).body(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Added new item")
                .data(Map.of("item", productService.addItem(productId, itemRequest)))
                .build());
    }

    @PutMapping("items")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Update an existing item of product", notes = "Available for ADMIN\n\n" +
            "Allows to update an existing item of the specific product.")
    public ResponseEntity<Response> updateItem(@Valid @RequestBody ItemRequest itemRequest) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated item")
                .data(Map.of("item", productService.updateItem(itemRequest)))
                .build());
    }

    @DeleteMapping("items/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete an existing item by id", notes = "Available for ADMIN\n\n" +
            "Allows to delete an existing item of the specific product by id from the system.")
    public ResponseEntity<Response> deleteItemById(@PathVariable Long itemId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted item with id: %s", itemId))
                .data(Map.of("is_deleted", productService.deleteItemById(itemId)))
                .build());
    }

    @PutMapping("assign/{productId}/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Assign a product to a category", notes = "Available for ADMIN\n\n" +
            "Allows to assign a product to a category if the product is not assigned yet.")
    public ResponseEntity<Response> assignToCategory(@PathVariable Long productId,
                                                     @PathVariable Long categoryId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Assigned product with id: %s to category with id: %s", productId, categoryId))
                .data(Map.of("is_assigned", productService.assignToCategory(productId, categoryId)))
                .build());
    }

    @PutMapping("unassign/{productId}/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Unassign a product from category", notes = "Available for ADMIN\n\n" +
            "Allows to unassign a product from a category if the product is already assigned.")
    public ResponseEntity<Response> unassignFromCategory(@PathVariable Long productId,
                                                         @PathVariable Long categoryId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Unassigned product with id: %s from category with id: %s", productId, categoryId))
                .data(Map.of("is_unassigned", productService.unassignFromCategory(productId, categoryId)))
                .build());
    }

    @PostMapping("upload/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Upload a new image of product", notes = "Available for ADMIN\n\n" +
            "Allows to upload an image of the existing product.")
    public ResponseEntity<Response> uploadProductImage(@RequestPart(value = "image")MultipartFile multipartFile,
                                                       @PathVariable Long productId) throws IOException {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Uploaded image for product with id: %s", productId))
                .data(Map.of("is_uploaded", productService.uploadProductImage(multipartFile, productId)))
                .build());
    }

    @DeleteMapping("delete/image/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete an existing image of product", notes = "Available for ADMIN\n\n" +
            "Allows to delete an image of the existing product.")
    public ResponseEntity<Response> deleteProductImageByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted image for product with id: %s", productId))
                .data(Map.of("is_deleted", productService.deleteProductImageByProductId(productId)))
                .build());
    }

    @GetMapping(path = "/image/{productId}", produces = IMAGE_PNG_VALUE)
    @ApiOperation(value = "Get product's image", notes = "Available for EVERYONE\n\n" +
            "Allows to get an image of the existing product.")
    public byte[] getProductImage(@PathVariable Long productId) throws IOException {
        return productService.getProductImage(productId);
    }
}
