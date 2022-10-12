package com.junior.company.ecommerce.mapper;

import com.junior.company.ecommerce.dto.CategoryRequest;
import com.junior.company.ecommerce.dto.CategoryResponse;
import com.junior.company.ecommerce.dto.ProductRead;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.WeatherSeason;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.junior.company.ecommerce.mapper.constant.SharedConstant.EMPTY_ID;

public class CategoryMapper {

    public static Category mapCategoryRequestToCategoryCreate(CategoryRequest categoryRequest) {
        return Category.builder()
                .id(EMPTY_ID)
                .name(categoryRequest.getName())
                .weatherSeason(WeatherSeason.valueOf(categoryRequest.getWeatherSeason()))
                .build();
    }

    public static Category mapCategoryRequestToCategoryUpdate(CategoryRequest categoryRequest) {
        return Category.builder()
                .id(categoryRequest.getId())
                .name(categoryRequest.getName())
                .weatherSeason(WeatherSeason.valueOf(categoryRequest.getWeatherSeason()))
                .build();
    }

    public static CategoryResponse mapCategoryToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .weatherSeason(category.getWeatherSeason())
                .products(mapProductsToProductsRead(category.getProducts()))
                .build();
    }

    private static List<ProductRead> mapProductsToProductsRead(List<Product> products) {
        return products.stream().map((product) ->
                        ProductRead.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .imageUrl(product.getImageUrl())
                                .build())
                .collect(Collectors.toList());
    }

    public static Set<CategoryResponse> mapCategoriesToCategoryResponses(Set<Category> categories) {
        return categories.stream().map((category) ->
                        CategoryResponse.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .weatherSeason(category.getWeatherSeason())
                                .build())
                .collect(Collectors.toSet());
    }
}
