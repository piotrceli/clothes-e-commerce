package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.CategoryRequest;
import com.junior.company.ecommerce.dto.CategoryResponse;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoriesToCategoryResponses;
import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoryRequestToCategoryCreate;
import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoryRequestToCategoryUpdate;
import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoryToCategoryResponse;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponsesList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void shouldGetListOfCategories() {

        // given
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(null)
                .build();

        Set<Category> categories = new HashSet<>(Set.of(category));
        given(categoryRepository.findAll()).willReturn(List.of(category));
        Set<CategoryResponse> categoryResponses = mapCategoriesToCategoryResponses(categories);

        // when
        Set<CategoryResponse> result = categoryService.findCategories();

        // then
        verify(categoryRepository, times(1)).findAll();
        assertThat(result).usingRecursiveComparison().isEqualTo(categoryResponses);
    }

    @Test
    void shouldGetCategoryByName_whenCategoryGotProducts_givenValidName() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .build();
        String categoryName = "t-shirt";
        Category category = Category.builder()
                .id(1L)
                .name(categoryName)
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();

        given(categoryRepository.findByName(anyString())).willReturn(Optional.of(category));
        CategoryResponse categoryResponse = mapCategoryToCategoryResponse(category);

        // when
        CategoryResponse result = categoryService.findCategoryByName(categoryName);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(categoryResponse);
    }

    @Test
    void shouldNotGetCategoryByName_givenInvalidName() {

        // given
        String categoryName = "invalid_name";

        given(categoryRepository.findByName(anyString())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> categoryService.findCategoryByName(categoryName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Category with name: %s not found", categoryName));
    }

    @Test
    void shouldAddCategory_givenValidCategoryRequest() {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        given(categoryRepository.findByName(anyString())).willReturn(Optional.empty());
        Category category = mapCategoryRequestToCategoryCreate(categoryRequest);
        category.setProducts(new ArrayList<>());
        CategoryResponse categoryResponse = mapCategoryToCategoryResponse(category);

        // when
        CategoryResponse result = categoryService.addCategory(categoryRequest);

        // then
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryArgumentCaptor.capture());
        Category capturedCategory = categoryArgumentCaptor.getValue();

        assertThat(capturedCategory).usingRecursiveComparison().isEqualTo(category);
        assertThat(result).usingRecursiveComparison().isEqualTo(categoryResponse);
    }

    @Test
    void shouldNotAddCategory_givenCategoryNameIsAlreadyTaken() {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();
        Category existingCategory = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .build();

        given(categoryRepository.findByName(anyString())).willReturn(Optional.of(existingCategory));

        // when then
        assertThatThrownBy(() -> categoryService.addCategory(categoryRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        String.format("Category with name: %s already exists", categoryRequest.getName()));
    }

    @Test
    void shouldUpdateCategory_whenCategoryGotProducts_givenValidCategoryRequest() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .build();
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("coat")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(categoryRepository.findByName(anyString())).willReturn(Optional.empty());

        Category updatedCategory = mapCategoryRequestToCategoryUpdate(categoryRequest);
        updatedCategory.setProducts(category.getProducts());
        CategoryResponse categoryResponse = mapCategoryToCategoryResponse(updatedCategory);

        // when
        CategoryResponse result = categoryService.updateCategory(categoryRequest);

        // then
        ArgumentCaptor<Category> categoryArgumentCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryArgumentCaptor.capture());
        Category capturedCategory = categoryArgumentCaptor.getValue();

        assertThat(capturedCategory).usingRecursiveComparison().isEqualTo(updatedCategory);
        assertThat(result).usingRecursiveComparison().isEqualTo(categoryResponse);
    }

    @Test
    void shouldNotUpdateCategory_givenInvalidCategoryRequestId() {

        // given
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();

        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        String.format("Category with id: %s not found", categoryRequest.getId()));
    }

    @Test
    void shouldUpdateCategory_givenCategoryRequestNameIsAlreadyTaken() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .build();
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason("SUMMER")
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();
        Category anotherCategory = Category.builder()
                .id(99L)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(categoryRepository.findByName(anyString())).willReturn(Optional.of(anotherCategory));

        // when then
        assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        String.format("Category with name: %s already exists", categoryRequest.getName()));
    }

    @Test
    void shouldDeleteCategoryById_givenValidCategoryId() {

        // given
        Long categoryId = 1L;
        Category category = Category.builder()
                .id(categoryId)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(new ArrayList<>())
                .build();
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when
        boolean result = categoryService.deleteCategoryById(categoryId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldDeleteCategoryById_whenProductsAreAssignedToCategory_givenValidCategoryId() {

        // given
        Long categoryId = 1L;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .build();
        Category category = Category.builder()
                .id(categoryId)
                .name("t-shirt")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // when then
        assertThatThrownBy(() -> categoryService.deleteCategoryById(categoryId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete category with assigned products");
    }

    @Test
    void shouldNotDeleteCategoryById_givenInvalidCategoryId() {

        // given
        Long categoryId = 0L;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> categoryService.deleteCategoryById(categoryId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Category with id: %s not found", categoryId));
    }

    @Test
    void shouldRetrieveListOfProducts_givenValidCategoryName() {

        // given
        String categoryName = "t-shirt";
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(null)
                .build();
        Category category = Category.builder()
                .id(1L)
                .name(categoryName)
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();
        product.setCategories(Set.of(category));

        given(categoryRepository.findByName(categoryName)).willReturn(Optional.of(category));
        List<ProductResponse> productResponses = mapProductsToProductResponsesList(category.getProducts());

        // when
        List<ProductResponse> result = categoryService.viewProducts(categoryName);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(productResponses);
    }

    @Test
    void shouldNotRetrieveListOfProducts_givenInvalidCategoryName() {

        // given
        String categoryName = "invalid_name";
        given(categoryRepository.findByName(categoryName)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> categoryService.viewProducts(categoryName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Category with name: %s not found", categoryName));
    }
}































