package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.ItemRequest;
import com.junior.company.ecommerce.dto.ItemResponse;
import com.junior.company.ecommerce.dto.ProductRequest;
import com.junior.company.ecommerce.dto.ProductResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.mapper.ItemMapper;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.repository.CategoryRepository;
import com.junior.company.ecommerce.repository.ItemRepository;
import com.junior.company.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.junior.company.ecommerce.controller.constant.ImagesConstant.COAT_URL;
import static com.junior.company.ecommerce.controller.constant.ImagesConstant.HOODIE_URL;
import static com.junior.company.ecommerce.controller.constant.ImagesConstant.OTHER_URL;
import static com.junior.company.ecommerce.controller.constant.ImagesConstant.TROUSERS_URL;
import static com.junior.company.ecommerce.controller.constant.ImagesConstant.T_SHIRT_URL;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductCreate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductUpdate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponse;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponseNoItems;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponses;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponsesList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final WeatherService weatherService;

    @Override
    public List<ProductResponse> findProductsPage(int page, int size) {
        log.info("Retrieving list of products");
        return mapProductsToProductResponses(productRepository.findAllPagination(PageRequest.of(page, size)));
    }

    @Override
    public ProductResponse findProductById(Long productId) {
        log.info("Retrieving product by id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        return mapProductToProductResponse(product);
    }

    @Override
    public ProductResponse addProduct(ProductRequest productRequest) {
        log.info("Creating new product");
        Product product = mapProductRequestToProductCreate(productRequest);
        product.setCategories(identifyCategories(productRequest));
        product.setImageUrl(assignImageUrl(product));
        productRepository.save(product);
        return mapProductToProductResponseNoItems(product);
    }

    @Override
    public ProductResponse updateProduct(ProductRequest productRequest) {
        log.info("Updating product with id: {}", productRequest.getId());
        Product existingProduct = productRepository.findById(productRequest.getId()).orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format("Product with id: %s not found", productRequest.getId())));

        Product product = mapProductRequestToProductUpdate(productRequest);
        product.setItems(existingProduct.getItems());
        product.setCategories(identifyCategories(productRequest));
        product.setImageUrl(existingProduct.getImageUrl());
        productRepository.save(product);
        return mapProductToProductResponse(product);
    }

    @Override
    public boolean deleteProductById(Long productId) {
        log.info("Deleting product with id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        productRepository.delete(product);
        return true;
    }

    private Set<Category> identifyCategories(ProductRequest productRequest) {
        Set<Category> categories = new HashSet<>();
        productRequest.getCategoriesIds().forEach((categoryId) -> {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                    new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));
            categories.add(category);
        });
        return categories;
    }

    private String assignImageUrl(Product product) {

        Map<String, String> images = new HashMap<>();
        images.put("t-shirt", T_SHIRT_URL);
        images.put("trousers", TROUSERS_URL);
        images.put("hoodie", HOODIE_URL);
        images.put("coat", COAT_URL);
        images.put("other", OTHER_URL);

        for (Category category : product.getCategories()) {
            if (category.getName().contains("t-shirt")) {
                return images.get("t-shirt");
            }
            if (category.getName().contains("trousers")) {
                return images.get("trousers");
            }
            if (category.getName().contains("hoodie")) {
                return images.get("hoodie");
            }
            if (category.getName().contains("coat")) {
                return images.get("coat");
            }
        }
        return images.get("other");
    }

    @Override
    public List<ProductResponse> findProductsMatchToWeather(String city, String country, int page, int size) {
        log.info("Retrieving list of products matched to actual weather in: {}, {}", city, country);

        Double temperature = weatherService.getTemperature(city, country);
        String weatherSeasonName = temperature <= 5.0 ? "WINTER" : temperature <= 15.0 ? "AUTUMN" :
                temperature <= 23 ? "SPRING" : "SUMMER";

        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach((product) ->
                product.getCategories().forEach((category) -> {
                    if (category.getWeatherSeason() == WeatherSeason.valueOf(weatherSeasonName)) {
                        products.add(product);
                    }
                }));

        PagedListHolder<Product> pagedListHolder = new PagedListHolder<>(products);
        pagedListHolder.setPage(page);
        pagedListHolder.setPageSize(size);
        List<Product> pagedProducts = pagedListHolder.getPageList();
        return mapProductsToProductResponsesList(pagedProducts);
    }

    @Override
    public String getTemperature(String city, String country) {
        log.info("Retrieving temperature in: {}, {}", city, country);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        return decimalFormat.format(weatherService.getTemperature(city, country));
    }

    @Override
    public ItemResponse addItem(Long productId, ItemRequest itemRequest) {
        log.info("Creating new item for product with id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));

        if (checkExistenceOfItemSize(product, itemRequest.getSize())) {
            throw new IllegalStateException(String.format("Item with size: %s already exists", itemRequest.getSize()));
        }

        Item item = ItemMapper.mapItemRequestToItemCreate(itemRequest);
        item.setProduct(product);
        itemRepository.save(item);
        return ItemMapper.mapItemToItemResponse(item);
    }

    private boolean checkExistenceOfItemSize(Product product, String size) {
        for (Item item : product.getItems()) {
            if (Objects.equals(item.getSize(), size)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemResponse updateItem(ItemRequest itemRequest) {
        log.info("Updating quantity of item with size: {}", itemRequest.getSize());
        Item existingItem = itemRepository.findById(itemRequest.getId()).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Item with id: %s not found", itemRequest.getId())));

        if (checkExistenceOfItemSize(existingItem.getProduct(), itemRequest.getSize()) &&
                !Objects.equals(itemRequest.getSize(), existingItem.getSize())) {
            throw new IllegalStateException(String.format("Item with size: %s already exists", itemRequest.getSize()));
        }

        Item item = ItemMapper.mapItemRequestToItemUpdate(itemRequest);
        item.setProduct(existingItem.getProduct());
        itemRepository.save(item);
        return ItemMapper.mapItemToItemResponse(item);
    }

    @Override
    public boolean deleteItemById(Long itemId) {
        log.info("Deleting item with id: {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Item with id: %s not found", itemId)));
        itemRepository.delete(item);
        return true;
    }

    @Override
    public boolean assignToCategory(Long productId, Long categoryId) {
        log.info("Assigning product with id: {} to category with id: {}", productId, categoryId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));

        category.getProducts().forEach((existingProduct) -> {
            if (existingProduct.getId().equals(product.getId())) {
                throw new IllegalStateException(
                        String.format("Product with id: %s is already assigned to category with id: %s",
                                productId, categoryId));
            }
        });
        return category.getProducts().add(product);
    }

    @Override
    public boolean unassignFromCategory(Long productId, Long categoryId) {
        log.info("Unassigning product with id: {} to category with id: {}", productId, categoryId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Category with id: %s not found", categoryId)));

        for (Product existingProduct : category.getProducts()) {
            if (existingProduct.getId().equals(product.getId())) {
                return category.getProducts().remove(product);
            }
        }
        throw new IllegalStateException(
                String.format("Product with id: %s is not assigned to category with id: %s",
                        productId, categoryId));
    }
}
