package com.junior.company.ecommerce.mapper;

import com.junior.company.ecommerce.dto.ItemRead;
import com.junior.company.ecommerce.dto.ProductRequest;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

import static com.junior.company.ecommerce.mapper.CategoryMapper.mapCategoriesToCategoryResponses;
import static com.junior.company.ecommerce.mapper.constant.SharedConstant.EMPTY_ID;

public class ProductMapper {

    public static Product mapProductRequestToProductCreate(ProductRequest productRequest) {
        return Product.builder()
                .id(EMPTY_ID)
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .build();
    }

    public static Product mapProductRequestToProductUpdate(ProductRequest productRequest) {
        return Product.builder()
                .id(productRequest.getId())
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .build();
    }

    public static ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .items(mapItemsToItemsRead(product.getItems()))
                .categories(mapCategoriesToCategoryResponses(product.getCategories()))
                .build();
    }

    private static List<ItemRead> mapItemsToItemsRead(List<Item> items) {

        return items.stream().map((item) ->
                        ItemRead.builder()
                                .id(item.getId())
                                .size(item.getSize())
                                .quantity(item.getQuantity())
                                .build())
                .collect(Collectors.toList());
    }

    public static List<ProductResponse> mapProductsToProductResponses(Page<Product> products) {
        return products.stream().map(ProductMapper::mapProductToProductResponseNoItems)
                .collect(Collectors.toList());
    }

    public static List<ProductResponse> mapProductsToProductResponsesList(List<Product> products) {
        return products.stream().map(ProductMapper::mapProductToProductResponseNoItems)
                .collect(Collectors.toList());
    }

    public static ProductResponse mapProductToProductResponseNoItems(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .categories(mapCategoriesToCategoryResponses(product.getCategories()))
                .build();
    }
}
