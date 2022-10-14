package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.ItemRequest;
import com.junior.company.ecommerce.dto.ItemResponse;
import com.junior.company.ecommerce.dto.ProductRequest;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.repository.CategoryRepository;
import com.junior.company.ecommerce.repository.ItemRepository;
import com.junior.company.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.junior.company.ecommerce.mapper.ItemMapper.mapItemRequestToItemCreate;
import static com.junior.company.ecommerce.mapper.ItemMapper.mapItemRequestToItemUpdate;
import static com.junior.company.ecommerce.mapper.ItemMapper.mapItemToItemResponse;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductCreate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductUpdate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponse;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponseNoItems;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponses;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponsesList;
import static com.junior.company.ecommerce.service.ProductServiceImpl.DIRECTORY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private WeatherService weatherService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void shouldFindProductsPage() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product productOne = Product.builder()
                .id(1L)
                .name("one")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .categories(Set.of(category))
                .build();
        Product productTwo = Product.builder()
                .id(2L)
                .name("two")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .categories(Set.of(category))
                .build();
        category.getProducts().addAll(List.of(productOne, productTwo));
        List<Product> products = List.of(productOne, productTwo);
        Page<Product> productPage = new PageImpl<>(products);
        given(productRepository.findAllPagination(any())).willReturn(productPage);
        List<ProductResponse> productResponses = mapProductsToProductResponses(productPage);

        // when
        List<ProductResponse> result = productService.findProductsPage(0, 10);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(productResponses);
    }

    @Test
    void shouldFindProductById_whenProductGotAssignedItems_givenValidProductId() {

        // given
        Long productId = 1L;
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("product")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .categories(Set.of(category))
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        ProductResponse productResponse = mapProductToProductResponse(product);

        // when
        ProductResponse result = productService.findProductById(productId);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(productResponse);
    }

    @Test
    void shouldNotFindProductById_givenInvalidProductId() {

        // given
        Long productId = 0L;

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.findProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldAddProduct_givenValidProductRequest() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        ProductRequest productRequest = ProductRequest.builder()
                .name("product")
                .price(10.0)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        Product product = mapProductRequestToProductCreate(productRequest);
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        product.setCategories(Set.of(category));
        ProductResponse productResponse = mapProductToProductResponseNoItems(product);

        // when
        ProductResponse result = productService.addProduct(productRequest);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(capturedProduct).usingRecursiveComparison().isEqualTo(product);
        assertThat(result).usingRecursiveComparison().isEqualTo(productResponse);
    }

    @Test
    void shouldUpdateProduct_whenProductGotItems_givenValidProductRequest() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("product")
                .price(10.0)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("product")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(List.of(item))
                .categories(Set.of(category))
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        Product updatedProduct = mapProductRequestToProductUpdate(productRequest);
        updatedProduct.setItems(product.getItems());
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        updatedProduct.setCategories(product.getCategories());
        updatedProduct.setImageUrl(product.getImageUrl());
        ProductResponse productResponse = mapProductToProductResponse(updatedProduct);

        // when
        ProductResponse result = productService.updateProduct(productRequest);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(capturedProduct).usingRecursiveComparison().isEqualTo(updatedProduct);
        assertThat(result).usingRecursiveComparison().isEqualTo(productResponse);
    }

    @Test
    void shouldNotUpdateProduct_givenInvalidProductRequestId() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("product")
                .price(10.0)
                .description("description")
                .categoriesIds(Set.of(1L))
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.updateProduct(productRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productRequest.getId()));
    }

    @Test
    void shouldNotUpdateProduct_givenInvalidProductRequestCategoriesIds() {

        // given
        ProductRequest productRequest = ProductRequest.builder()
                .id(1L)
                .name("product")
                .price(10.0)
                .description("description")
                .categoriesIds(Set.of(99L))
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("product")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .categories(Set.of(category))
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.updateProduct(productRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Category with id: %s not found", 99L));
    }

    @Test
    void shouldDeleteProduct_givenValidProductId() {

        // given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("product")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        boolean result = productService.deleteProductById(productId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldDeleteProduct_givenInvalidProductId() {

        // given
        Long productId = 0L;

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.deleteProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldGetListOfProductsMatchedToWeather() {

        // given
        String city = "city";
        String country = "country";
        int page = 0;
        int size = 10;
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product productOne = Product.builder()
                .id(1L)
                .name("one")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .categories(Set.of(category))
                .build();
        Product productTwo = Product.builder()
                .id(2L)
                .name("two")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .categories(Set.of(category))
                .build();
        List<Product> products = List.of(productOne, productTwo);

        given(weatherService.getTemperature(anyString(), anyString())).willReturn(25.0);
        given(productRepository.findAll()).willReturn(products);

        List<ProductResponse> productResponses = mapProductsToProductResponsesList(products);

        // when
        List<ProductResponse> result = productService.findProductsMatchToWeather(city, country, page, size);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(productResponses);
    }

    @Test
    void shouldGetTemperature() {

        // given
        String city = "city";
        String country = "country";

        given(weatherService.getTemperature(anyString(), anyString())).willReturn(25.0);

        // when
        String result = productService.getTemperature(city, country);

        // then
        assertThat(result).isEqualTo("25,00");
    }

    @Test
    void shouldAddItemToProduct_givenValidProductIdAndItemRequest() {

        // given
        Long productId = 1L;
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .categories(Set.of(category))
                .build();
        category.getProducts().add(product);
        ItemRequest itemRequest = ItemRequest.builder()
                .size("S")
                .quantity(50)
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        Item item = mapItemRequestToItemCreate(itemRequest);
        item.setProduct(product);
        ItemResponse itemResponse = mapItemToItemResponse(item);

        // when
        ItemResponse result = productService.addItem(productId, itemRequest);

        // then
        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();

        assertThat(capturedItem).usingRecursiveComparison().isEqualTo(item);
        assertThat(result).usingRecursiveComparison().isEqualTo(itemResponse);
    }

    @Test
    void shouldNotAddItemToProduct_givenInvalidProductId() {

        // given
        Long productId = 0L;
        ItemRequest itemRequest = ItemRequest.builder()
                .size("S")
                .quantity(50)
                .build();

        // when then
        assertThatThrownBy(() -> productService.addItem(productId, itemRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldNotAddItemToProduct_whenProductAlreadyGotSpecificSize() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .categories(Set.of(category))
                .build();
        category.getProducts().add(product);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("M")
                .quantity(50)
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .product(product)
                .build();
        Item otherItem = Item.builder()
                .id(2L)
                .size("M")
                .quantity(50)
                .product(product)
                .build();
        product.getItems().add(item);
        product.getItems().add(otherItem);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when then
        assertThatThrownBy(() -> productService.addItem(product.getId(), itemRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Item with size: %s already exists", itemRequest.getSize());
    }

    @Test
    void shouldUpdateItemToProduct_givenValidItemRequest() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .categories(Set.of(category))
                .build();
        category.getProducts().add(product);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("M")
                .quantity(50)
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .product(product)
                .build();
        product.getItems().add(item);

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));
        Item updatedItem = mapItemRequestToItemUpdate(itemRequest);
        updatedItem.setProduct(item.getProduct());
        ItemResponse itemResponse = mapItemToItemResponse(updatedItem);

        // when
        ItemResponse result = productService.updateItem(itemRequest);

        // then
        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();

        assertThat(capturedItem).usingRecursiveComparison().isEqualTo(updatedItem);
        assertThat(result).usingRecursiveComparison().isEqualTo(itemResponse);
    }

    @Test
    void shouldNotUpdateItem_givenInvalidItemRequestId() {

        // given
        ItemRequest itemRequest = ItemRequest.builder()
                .id(0L)
                .size("S")
                .quantity(50)
                .build();

        // when then
        assertThatThrownBy(() -> productService.updateItem(itemRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Item with id: %s not found", itemRequest.getId()));
    }

    @Test
    void shouldNotUpdateItemToProduct_whenProductAlreadyGotSpecificSize() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(1L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .categories(Set.of(category))
                .build();
        category.getProducts().add(product);
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .size("M")
                .quantity(50)
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("S")
                .quantity(50)
                .product(product)
                .build();
        Item otherItem = Item.builder()
                .id(2L)
                .size("M")
                .quantity(50)
                .product(product)
                .build();
        product.getItems().add(item);
        product.getItems().add(otherItem);

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));

        // when then
        assertThatThrownBy(() -> productService.updateItem(itemRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Item with size: %s already exists", itemRequest.getSize());
    }

    @Test
    void shouldDeleteItemById_givenValidItemId() {

        // given
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .size("S")
                .quantity(50)
                .build();

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));

        // when
        boolean result = productService.deleteItemById(itemId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotDeleteItemById_givenInvalidItemId() {

        // given
        Long itemId = 0L;

        given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.deleteItemById(itemId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Item with id: %s not found", itemId));
    }

    @Test
    void shouldAssignProductToCategory_givenValidProductIdAndCategoryId() {

        // given
        Long productId = 1L;
        Long categoryId = 1L;
        Category category = Category.builder()
                .id(categoryId)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when
        boolean result = productService.assignToCategory(productId, categoryId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotAssignProductToCategory_whenProductIsAlreadyAssignedToCategory() {

        // given
        Long productId = 1L;
        Long categoryId = 1L;
        Category category = Category.builder()
                .id(categoryId)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();
        product.setCategories(Set.of(category));
        category.setProducts(List.of(product));

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when then
        assertThatThrownBy(() -> productService.assignToCategory(productId, categoryId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        String.format("Product with id: %s is already assigned to category with id: %s",
                                productId, categoryId));
    }

    @Test
    void shouldNotAssignProductToCategory_givenInvalidProductId() {

        // given
        Long productId = 0L;
        Long categoryId = 1L;

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.assignToCategory(productId, categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldNotAssignProductToCategory_givenInvalidCategoryId() {

        // given
        Long productId = 1L;
        Long categoryId = 0L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.assignToCategory(productId, categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Category with id: %s not found", categoryId));
    }

    @Test
    void shouldUnassignProductFromCategory_givenValidProductIdAndCategoryId() {

        // given
        Long productId = 1L;
        Long categoryId = 1L;
        Category category = Category.builder()
                .id(categoryId)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .categories(new HashSet<>())
                .build();
        product.getCategories().add(category);
        category.getProducts().add(product);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when
        boolean result = productService.unassignFromCategory(productId, categoryId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotUnassignProductFromCategory_whenProductIsNotAssignedToCategory() {

        // given
        Long productId = 1L;
        Long categoryId = 1L;
        Product anotherProduct = Product.builder()
                .id(2L)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();
        Category category = Category.builder()
                .id(categoryId)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(anotherProduct))
                .build();
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();


        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when then
        assertThatThrownBy(() -> productService.unassignFromCategory(productId, categoryId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Product with id: %s is not assigned to category with id: %s",
                        productId, categoryId));
    }

    @Test
    void shouldNotUnassignProductFromCategory_givenInvalidProductId() {

        // given
        Long productId = 0L;
        Long categoryId = 1L;

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.unassignFromCategory(productId, categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldNotUnassignProductFromCategory_givenInvalidCategoryId() {

        // given
        Long productId = 1L;
        Long categoryId = 0L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.unassignFromCategory(productId, categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Category with id: %s not found", categoryId));
    }

    @Test
    void shouldUploadProductImage_givenValidProductId() {

        // given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();
        multipartFile = new MockMultipartFile("name", "".getBytes());

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        boolean result = productService.uploadProductImage(multipartFile, productId);

        // then
        assertThat(result).isTrue();
        assertThat(product.getImageUrl()).isEqualTo(productId + ".png");
    }

    @Test
    void shouldNotUploadProductImage_whenIOExceptionOccurs() throws IOException {

        // given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();
        given(multipartFile.getInputStream()).willThrow(new IOException());

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        // when
        boolean result = productService.uploadProductImage(multipartFile, productId);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldNotUploadProductImage_givenInvalidProductId() {

        // given
        Long productId = 0L;
        multipartFile = new MockMultipartFile("name", "".getBytes());

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.uploadProductImage(multipartFile, productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldDeleteProductImageByProductId_givenValidProductId() throws Exception{

        // given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .imageUrl("test_image_9012")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        File tempImage = new File(DIRECTORY + product.getImageUrl());
        tempImage.createNewFile();

        // when
        boolean result = productService.deleteProductImageByProductId(productId);

        // then
        assertThat(result).isTrue();
        assertThat(product.getImageUrl()).isNull();
    }

    @Test
    void shouldNotDeleteProductImageByProductId_whenProductImageDoesNotExist() {

        // given
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        boolean result = productService.deleteProductImageByProductId(productId);

        // then
        assertThat(result).isFalse();
        assertThat(product.getImageUrl()).isNull();
    }

    @Test
    void shouldNotDeleteProductImageByProductId_givenInvalidProductId() {

        // given
        Long productId = 0L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.deleteProductImageByProductId(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }

    @Test
    void shouldGetProductImage_givenValidProductId() throws Exception{

        // given
        Long productId =  1L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .imageUrl("test_image_9012")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        File tempImage = new File(DIRECTORY + product.getImageUrl());
        tempImage.createNewFile();

        // when
        byte[] result = productService.getProductImage(productId);

        // then
        assertThat(result).isNotNull();
        tempImage.delete();
    }

    @Test
    void shouldNotGetProductImage_whenProductImageDoesNotExist(){

        // given
        Long productId =  1L;
        Product product = Product.builder()
                .id(productId)
                .name("basic black t-shirt")
                .price(29.99)
                .description("description_one")
                .imageUrl("test_image_9012")
                .items(new ArrayList<>())
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when then
        assertThatThrownBy(() -> productService.getProductImage(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Image of product with id: %s not found", productId));
    }

    @Test
    void shouldNotGetProductImage_givenInvalidProductId() {

        // given
        Long productId =  0L;
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> productService.getProductImage(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Product with id: %s not found", productId));
    }
}