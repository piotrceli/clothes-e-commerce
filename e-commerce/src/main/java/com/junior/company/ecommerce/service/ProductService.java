package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.ItemRequest;
import com.junior.company.ecommerce.dto.ItemResponse;
import com.junior.company.ecommerce.dto.ProductRequest;
import com.junior.company.ecommerce.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductResponse> findProductsPage(int page, int size);

    ProductResponse findProductById(Long productId);

    ProductResponse addProduct(ProductRequest productRequest);

    ProductResponse updateProduct(ProductRequest productRequest);

    boolean deleteProductById(Long productId);

    List<ProductResponse> findProductsMatchToWeather(String city, String country, int page, int size);

    String getTemperature(String city, String country);

    ItemResponse addItem(Long productId, ItemRequest itemRequest);

    ItemResponse updateItem(ItemRequest itemRequest);

    boolean deleteItemById(Long itemId);

    boolean assignToCategory(Long productId, Long categoryId);

    boolean unassignFromCategory(Long productId, Long categoryId);

    boolean uploadProductImage(MultipartFile multipartFile, Long productId) throws IOException;

    boolean deleteProductImageByProductId(Long productId);

    byte[] getProductImage(Long productId) throws IOException;
}
