package com.junior.company.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.company.ecommerce.dto.ItemRequest;
import com.junior.company.ecommerce.dto.ItemResponse;
import com.junior.company.ecommerce.dto.ProductRequest;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.security.AppUserDetailsService;
import com.junior.company.ecommerce.service.ProductService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.junior.company.ecommerce.mapper.ItemMapper.mapItemRequestToItemCreate;
import static com.junior.company.ecommerce.mapper.ItemMapper.mapItemRequestToItemUpdate;
import static com.junior.company.ecommerce.mapper.ItemMapper.mapItemToItemResponse;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductCreate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponseNoItems;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponses;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

@WebMvcTest(ProductController.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void shouldGetListOfProducts() throws Exception {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        Item itemOne = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .build();
        Product productOne = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .categories(Set.of(category))
                .build();

        Item itemTwo = Item.builder()
                .id(2L)
                .size("L")
                .quantity(30)
                .build();
        Product productTwo = Product.builder()
                .id(2L)
                .name("basic green t-shirt")
                .price(19.99)
                .description("description_two")
                .categories(Set.of(category))
                .build();

        productOne.setItems(List.of(itemOne));
        productTwo.setItems(List.of(itemTwo));
        List<Product> products = new ArrayList<>(List.of(productOne, productTwo));
        PageImpl<Product> page = new PageImpl<>(products);
        List<ProductResponse> productResponses = mapProductsToProductResponses(page);

        given(productService.findProductsPage(anyInt(), anyInt())).willReturn(productResponses);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of products")
                .data(Map.of("products", productService.findProductsPage(0, 10)))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldGetProductById_givenValidId() throws Exception {

        // given
        Long productId = 1L;
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .categories(Set.of(category))
                .build();

        ProductResponse productResponse = mapProductToProductResponseNoItems(product);
        given(productService.findProductById(anyLong())).willReturn(productResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved product by id: %s", productId))
                .data(Map.of("product", productService.findProductById(productId)))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldNotGetProductById_givenInvalidId() throws Exception {

        // given
        Long productId = 0L;

        given(productService.findProductById(anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Product with id: %s not found", productId))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/products/{productId}", productId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateNewProduct_givenValidProductRequest() throws Exception {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        Product product = mapProductRequestToProductCreate(productRequest);
        product.setCategories(Set.of(category));
        ProductResponse productResponse = mapProductToProductResponseNoItems(product);
        given(productService.addProduct(any())).willReturn(productResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Created new product")
                .data(Map.of("product", productService.addProduct(productRequest)))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotCreateNewProduct_givenInvalidProductRequest() throws Exception {

        // given
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("7")
                .price(29.99)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        Map<String, String> errors = new HashMap<>();
        String fieldName = "name";
        String errorMessage = "Min length is 2";
        errors.put(fieldName, errorMessage);
        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("error occurred")
                .data(Map.of("errors", errors))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateProduct_givenValidProductRequest() throws Exception {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        Product product = mapProductRequestToProductCreate(productRequest);
        product.setCategories(Set.of(category));
        ProductResponse productResponse = mapProductToProductResponseNoItems(product);
        given(productService.updateProduct(any())).willReturn(productResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated product")
                .data(Map.of("product", productService.updateProduct(productRequest)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUpdateProduct_givenInvalidProductRequest() throws Exception {

        // given
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("7")
                .price(29.99)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        Map<String, String> errors = new HashMap<>();
        String fieldName = "name";
        String errorMessage = "Min length is 2";
        errors.put(fieldName, errorMessage);
        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("error occurred")
                .data(Map.of("errors", errors))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUpdateProduct_whenProductToUpdateDoesNotExist() throws Exception {

        // given
        Long productId = 0L;
        ProductRequest productRequest = ProductRequest.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        given(productService.updateProduct(any())).willThrow(new ResourceNotFoundException(
                String.format("Product with id: %s not found", productRequest.getId())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Product with id: %s not found", productId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteProductById_givenValidProductId() throws Exception {

        // given
        Long productId = 1L;

        given(productService.deleteProductById(anyLong())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted product with id: %s", productId))
                .data(Map.of("is_deleted", productService.deleteProductById(productId)))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotDeleteProductById_givenInvalidProductId() throws Exception {

        // given
        Long productId = 0L;

        given(productService.deleteProductById(anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Product with id: %s not found", productId))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/products/{productId}", productId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldGetListOfProductsToMatchToWeather() throws Exception {

        // given
        String city = "Warsaw";
        String country = "Poland";
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        Item itemOne = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .build();
        Product productOne = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .categories(Set.of(category))
                .build();

        Item itemTwo = Item.builder()
                .id(2L)
                .size("L")
                .quantity(30)
                .build();
        Product productTwo = Product.builder()
                .id(2L)
                .name("basic green t-shirt")
                .price(19.99)
                .description("description_two")
                .categories(Set.of(category))
                .build();

        productOne.setItems(List.of(itemOne));
        productTwo.setItems(List.of(itemTwo));
        List<Product> products = new ArrayList<>(List.of(productOne, productTwo));
        PageImpl<Product> page = new PageImpl<>(products);
        List<ProductResponse> productResponses = mapProductsToProductResponses(page);
        given(productService.findProductsMatchToWeather(anyString(), anyString(), anyInt(), anyInt()))
                .willReturn(productResponses);
        given(productService.getTemperature(anyString(), anyString())).willReturn("30.00");

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of products matched to actual weather")
                .data(Map.of("temperature", productService.getTemperature(city, country),
                        "products", productService.findProductsMatchToWeather(city, country, 0, 10)))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/products/match-to-weather")
                        .param("city", city)
                        .param("country", country))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateNewItemForProduct_givenValidItemRequestAndProductId() throws Exception {

        // given
        Long productId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("M")
                .quantity(10)
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .categories(Set.of(category))
                .items(new ArrayList<>())
                .build();
        Item item = mapItemRequestToItemCreate(itemRequest);
        item.setProduct(product);
        ItemResponse itemResponse = mapItemToItemResponse(item);
        given(productService.addItem(anyLong(), any())).willReturn(itemResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Added new item")
                .data(Map.of("item", productService.addItem(productId, itemRequest)))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/products/items/{productId}", productId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotCreateNewItemForProduct_givenInvalidProductId() throws Exception {

        // given
        Long productId = 0L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("M")
                .quantity(10)
                .build();
        given(productService.addItem(anyLong(), any())).willThrow(
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Product with id: %s not found", productId))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/products/items/{productId}", productId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotCreateNewItemForProduct_givenInvalidItemRequest() throws Exception {

        // given
        Long productId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("")
                .quantity(10)
                .build();

        Map<String, String> errors = new HashMap<>();
        String fieldName = "size";
        String errorMessage = "Min length is 1";
        errors.put(fieldName, errorMessage);
        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("error occurred")
                .data(Map.of("errors", errors))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/products/items/{productId}", productId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotCreateNewItemForProduct_whenItemSizeIsAlreadyTaken() throws Exception {

        // given
        Long productId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("L")
                .quantity(10)
                .build();

        given(productService.addItem(anyLong(), any())).willThrow(new IllegalStateException(
                String.format("Item with size: %s already exists", itemRequest.getSize())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(String.format("Item with size: %s already exists", itemRequest.getSize()))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/products/items/{productId}", productId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUpdateItemForProduct_givenValidItemRequest() throws Exception {

        // given
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("M")
                .quantity(10)
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .categories(Set.of(category))
                .items(new ArrayList<>())
                .build();
        Item item = mapItemRequestToItemUpdate(itemRequest);
        item.setProduct(product);
        ItemResponse itemResponse = mapItemToItemResponse(item);
        given(productService.updateItem(any())).willReturn(itemResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated item")
                .data(Map.of("item", productService.updateItem(itemRequest)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/items")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUpdateItemForProduct_whenItemToUpdateDoesNotExist() throws Exception {

        // given
        ItemRequest itemRequest = ItemRequest.builder()
                .id(0L)
                .size("M")
                .quantity(10)
                .build();
        given(productService.updateItem(any())).willThrow(
                new ResourceNotFoundException(String.format("Item with id: %s not found", itemRequest.getId())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Item with id: %s not found", itemRequest.getId()))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/items")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldDeleteItemById_givenValidItemId() throws Exception {

        // given
        Long itemId = 1L;

        given(productService.deleteItemById(anyLong())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted item with id: %s", itemId))
                .data(Map.of("is_deleted", productService.deleteItemById(itemId)))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/products/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotDeleteItemById_givenInvalidItemId() throws Exception {

        // given
        Long itemId = 0L;

        given(productService.deleteItemById(anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Item with id: %s not found", itemId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Item with id: %s not found", itemId))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/products/items/{itemId}", itemId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldAssignProductToCategory_givenValidProductIdAndCategoryId() throws Exception {

        // given
        Long productId = 1L;
        Long categoryId = 1L;

        given(productService.assignToCategory(anyLong(), anyLong())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Assigned product with id: %s to category with id: %s", productId, categoryId))
                .data(Map.of("is_assigned", productService.assignToCategory(productId, categoryId)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/assign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotAssignProductToCategory_givenInvalidProductId() throws Exception {

        // given
        Long productId = 0L;
        Long categoryId = 1L;

        given(productService.assignToCategory(anyLong(), anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Product with id: %s not found", productId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/assign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotAssignProductToCategory_givenInvalidCategoryId() throws Exception {

        // given
        Long productId = 1L;
        Long categoryId = 0L;

        given(productService.assignToCategory(anyLong(), anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Category with id: %s not found", categoryId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/assign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotAssignProductToCategory_whenProductIsAlreadyAssignedToCategory() throws Exception {

        // given
        Long productId = 1L;
        Long categoryId = 1L;

        given(productService.assignToCategory(anyLong(), anyLong())).willThrow(
                new IllegalStateException(String.format(
                        "Product with id: %s is already assigned to category with id: %s",
                        productId, categoryId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(String.format(
                        "Product with id: %s is already assigned to category with id: %s",
                        productId, categoryId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/assign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldUnAssignProductFromCategory_givenValidProductIdAndCategoryId() throws Exception {

        // given
        Long productId = 1L;
        Long categoryId = 1L;

        given(productService.unassignFromCategory(anyLong(), anyLong())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Unassigned product with id: %s from category with id: %s", productId, categoryId))
                .data(Map.of("is_unassigned", productService.unassignFromCategory(productId, categoryId)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/unassign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUnAssignProductToCategory_givenInvalidProductId() throws Exception {

        // given
        Long productId = 0L;
        Long categoryId = 1L;

        given(productService.unassignFromCategory(anyLong(), anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Product with id: %s not found", productId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/unassign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUnAssignProductToCategory_givenInvalidCategoryId() throws Exception {

        // given
        Long productId = 1L;
        Long categoryId = 0L;

        given(productService.unassignFromCategory(anyLong(), anyLong())).willThrow(
                new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("Category with id: %s not found", categoryId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/unassign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldNotUnAssignProductToCategory_whenProductIsActuallyNotAssignedToCategory() throws Exception {

        // given
        Long productId = 1L;
        Long categoryId = 1L;

        given(productService.unassignFromCategory(anyLong(), anyLong())).willThrow(
                new IllegalStateException(String.format(
                        "Product with id: %s is not assigned to category with id: %s",
                        productId, categoryId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(String.format(
                        "Product with id: %s is not assigned to category with id: %s",
                        productId, categoryId))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/products/unassign/{productId}/{categoryId}", productId, categoryId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }
}