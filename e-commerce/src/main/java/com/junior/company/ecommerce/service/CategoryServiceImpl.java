package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.CategoryRequest;
import com.junior.company.ecommerce.dto.CategoryResponse;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.mapper.CategoryMapper;
import com.junior.company.ecommerce.mapper.ProductMapper;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Set<CategoryResponse> findCategories() {
        log.info("Retrieving list of categories");
        Set<Category> categories = new HashSet<>(categoryRepository.findAll());
        return CategoryMapper.mapCategoriesToCategoryResponses(categories);
    }

    @Override
    public CategoryResponse findCategoryByName(String categoryName) {
        log.info("Retrieving category with name: {}", categoryName);
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Category with name: %s not found", categoryName)));
        return CategoryMapper.mapCategoryToCategoryResponse(category);
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        log.info("Creating new category with name: {}", categoryRequest.getName());
        if (categoryRepository.findByName(categoryRequest.getName()).isPresent()) {
            throw new IllegalStateException(
                    String.format("Category with name: %s already exists", categoryRequest.getName()));
        }
        Category category = CategoryMapper.mapCategoryRequestToCategoryCreate(categoryRequest);
        categoryRepository.save(category);
        return CategoryMapper.mapCategoryToCategoryResponseNoProducts(category);
    }

    @Override
    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {
        log.info("Updating category with id: {}", categoryRequest.getId());
        Category existingCategory = categoryRepository.findById(categoryRequest.getId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format("Category with id: %s not found", categoryRequest.getId())));

        if (categoryRepository.findByName(categoryRequest.getName()).isPresent() &&
                !(categoryRepository.findByName(categoryRequest.getName()).get().getId()
                        .equals(categoryRequest.getId()))) {
            throw new IllegalStateException(
                    String.format("Category with name: %s already exists", categoryRequest.getName()));
        }

        Category category = CategoryMapper.mapCategoryRequestToCategoryUpdate(categoryRequest);
        category.setProducts(existingCategory.getProducts());
        categoryRepository.save(category);
        return CategoryMapper.mapCategoryToCategoryResponse(category);
    }

    @Override
    public boolean deleteCategoryById(Long categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));
        if (!category.getProducts().isEmpty()){
            throw new IllegalStateException("Cannot delete category with assigned products");
        }
        categoryRepository.delete(category);
        return true;
    }

    @Override
    public List<ProductResponse> viewProducts(String categoryName) {
        log.info("Retrieving list of products for category with name: {}", categoryName);
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Category with name: %s not found", categoryName)));

        List<Product> products = category.getProducts();
        return ProductMapper.mapProductsToProductResponsesList(products);
    }
}
