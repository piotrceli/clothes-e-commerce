package com.junior.company.ecommerce.controller;

import com.junior.company.ecommerce.dto.CategoryRequest;
import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.service.CategoryService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.junior.company.ecommerce.swagger.SwaggerConstants.CATEGORIES_API_TAG;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@Api(tags = {CATEGORIES_API_TAG})
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ApiOperation(value = "Get a list of all categories", notes = "Available for EVERYONE\n\n" +
            "Allows to view a list of all categories of products.")
    public ResponseEntity<Response> findCategories() {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of categories")
                .data(Map.of("categories", categoryService.findCategories()))
                .build());
    }

    @GetMapping("{categoryName}")
    @ApiOperation(value = "Get a category by name", notes = "Available for EVERYONE\n\n" +
            "Allows to view a category by name.")
    public ResponseEntity<Response> findCategoryByName(@PathVariable String categoryName) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved category with name: %s", categoryName))
                .data(Map.of("category", categoryService.findCategoryByName(categoryName)))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Create a new category", notes = "Available for ADMIN\n\n" +
            "Allows to create a new category of products.")
    public ResponseEntity<Response> addCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/categories").toUriString());
        return ResponseEntity.created(uri).body(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Created new category")
                .data(Map.of("category", categoryService.addCategory(categoryRequest)))
                .build());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Update an existing category", notes = "Available for ADMIN\n\n" +
            "Allows to update an existing category.")
    public ResponseEntity<Response> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated category")
                .data(Map.of("category", categoryService.updateCategory(categoryRequest)))
                .build());
    }

    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Delete an existing category by id", notes = "Available for ADMIN\n\n" +
            "Allows to delete an existing category by id.")
    public ResponseEntity<Response> deleteCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted category with id: %s", categoryId))
                .data(Map.of("is_deleted", categoryService.deleteCategoryById(categoryId)))
                .build());
    }

    @GetMapping("products/{categoryName}")
    @ApiOperation(value = "Get a list of all products of the chosen category", notes = "Available for EVERYONE\n\n" +
            "Allows to view a list of all products of the chosen category by name.")
    public ResponseEntity<Response> viewProducts(@PathVariable String categoryName) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved list of products for category: %s", categoryName))
                .data(Map.of("products", categoryService.viewProducts(categoryName)))
                .build());
    }
}
