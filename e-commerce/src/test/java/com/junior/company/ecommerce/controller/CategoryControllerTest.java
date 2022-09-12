package com.junior.company.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.company.ecommerce.dto.CategoryRequest;
import com.junior.company.ecommerce.dto.CategoryResponse;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.security.AppUserDetailsService;
import com.junior.company.ecommerce.service.CategoryService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoriesToCategoryResponses;
import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoryRequestToCategoryCreate;
import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoryRequestToCategoryUpdate;
import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoryToCategoryResponseNoProducts;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void shouldGetListOfCategories() throws Exception {

        // given
        Category categoryOne = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();

        Category categoryTwo = Category.builder()
                .id(2L)
                .name("coat")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();

        Set<Category> categories = new HashSet<>(List.of(categoryOne, categoryTwo));
        Set<CategoryResponse> categoryResponses = mapCategoriesToCategoryResponses(categories);
        given(categoryService.findCategories()).willReturn(categoryResponses);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of categories")
                .data(Map.of("categories", categoryService.findCategories()))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldGetCategoryByName_givenValidName() throws Exception {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();

        CategoryResponse categoryResponse = mapCategoryToCategoryResponseNoProducts(category);
        given(categoryService.findCategoryByName(anyString())).willReturn(categoryResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved category with name: %s", category.getName()))
                .data(Map.of("category", categoryService.findCategoryByName(category.getName())))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/categories/{categoryName}", category.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldNotGetCategoryByName_givenInvalidName() throws Exception {

        // given
        String categoryName = "Invalid name";
        given(categoryService.findCategoryByName(anyString())).willThrow(new ResourceNotFoundException(
                String.format("Category with name: %s not found", categoryName)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Category with name: %s not found", categoryName))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/categories/{categoryName}", categoryName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateNewCategory_givenValidCategoryRequest() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        Category category = mapCategoryRequestToCategoryCreate(categoryRequest);
        CategoryResponse categoryResponse = mapCategoryToCategoryResponseNoProducts(category);
        given(categoryService.addCategory(any())).willReturn(categoryResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Created new category")
                .data(Map.of("category", categoryService.addCategory(categoryRequest)))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotCreateNewCategory_givenInvalidCategoryRequest() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("JANUARY")
                .build();

        Map<String, String> errors = new HashMap<>();
        String fieldName = "weatherSeason";
        String errorMessage = "Given wrong Weather Season. Pick: SPRING / SUMMER / AUTUMN / WINTER / NONE";
        errors.put(fieldName, errorMessage);
        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("error occurred")
                .data(Map.of("errors", errors))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotCreateNewCategory_whenCategoryNameIsAlreadyTaken() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        given(categoryService.addCategory(any())).willThrow(new IllegalStateException(
                String.format("Category with name: %s already exists", categoryRequest.getName())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(String.format("Category with name: %s already exists", categoryRequest.getName()))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateCategory_givenValidCategoryRequest() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        Category category = mapCategoryRequestToCategoryUpdate(categoryRequest);
        CategoryResponse categoryResponse = mapCategoryToCategoryResponseNoProducts(category);
        given(categoryService.updateCategory(any())).willReturn(categoryResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated category")
                .data(Map.of("category", categoryService.updateCategory(categoryRequest)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUpdateCategory_givenInvalidCategoryRequest() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("JANUARY")
                .build();


        Map<String, String> errors = new HashMap<>();
        String fieldName = "weatherSeason";
        String errorMessage = "Given wrong Weather Season. Pick: SPRING / SUMMER / AUTUMN / WINTER / NONE";
        errors.put(fieldName, errorMessage);
        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("error occurred")
                .data(Map.of("errors", errors))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUpdateCategory_whenCategoryToUpdateDoesNotExist() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(0L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        given(categoryService.updateCategory(any())).willThrow(new ResourceNotFoundException(
                String.format("Category with id: %s not found", categoryRequest.getId())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Category with id: %s not found", categoryRequest.getId()))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUpdateCategory_whenCategoryNameIsAlreadyTaken() throws Exception {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        given(categoryService.updateCategory(any())).willThrow(new IllegalStateException(
                String.format("Category with name: %s already exists", categoryRequest.getName())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(String.format("Category with name: %s already exists", categoryRequest.getName()))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteCategoryById_givenValidCategoryId() throws Exception {

        // given
        Long categoryId = 1L;

        given(categoryService.deleteCategoryById(anyLong())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted category with id: %s", categoryId))
                .data(Map.of("is_deleted", categoryService.deleteCategoryById(categoryId)))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotDeleteCategoryById_givenInvalidCategoryId() throws Exception {

        // given
        Long categoryId = 0L;

        given(categoryService.deleteCategoryById(anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Category with id: %s not found", categoryId))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/categories/{categoryId}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldGetProductsForGivenCategory_givenValidCategoryName() throws Exception {

        // given
        String categoryName = "t-shirt";
        ProductResponse productResponse = ProductResponse.builder()
                .id(1L)
                .name("basic t-shirt")
                .price(10.99)
                .description("description")
                .build();

        given(categoryService.viewProducts(anyString())).willReturn(List.of(productResponse));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved list of products for category: %s", categoryName))
                .data(Map.of("products", categoryService.viewProducts(categoryName)))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/categories/products/{categoryName}", categoryName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldNotGetProductsForGivenCategory_givenInvalidCategoryName() throws Exception {

        // given
        String categoryName = "Invalid name";

        given(categoryService.viewProducts(anyString())).willThrow(
                new ResourceNotFoundException(String.format("Category with name: %s not found", categoryName)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Category with name: %s not found", categoryName))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/categories/products/{categoryName}", categoryName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }
}