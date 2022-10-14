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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductCreate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductRequestToProductUpdate;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponse;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductToProductResponseNoItems;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponses;
import static com.junior.company.ecommerce.mapper.ProductMapper.mapProductsToProductResponsesList;
import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final WeatherService weatherService;
    public static final String DIRECTORY = "./src/main/resources/static/images/";

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
        deleteProductImageByProductId(productId);
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

    @Override
    public boolean uploadProductImage(MultipartFile multipartFile, Long productId) {
        log.info("Uploading image of product with id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        String fileName = productId + ".png";
        Path fileStorage = get(DIRECTORY, fileName);
        try {
            copy(multipartFile.getInputStream(), fileStorage, REPLACE_EXISTING);
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
        product.setImageUrl(fileName);
        return true;
    }

    @Override
    public boolean deleteProductImageByProductId(Long productId) {
        log.info("Deleting image of product with id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        File image = new File(DIRECTORY + product.getImageUrl());
        product.setImageUrl(null);
        return image.delete();
    }

    @Override
    public byte[] getProductImage(Long productId) {
        log.info("Retrieving image of product with id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("Product with id: %s not found", productId)));
        String imageName = product.getImageUrl();
        try {
            return Files.readAllBytes(Paths.get(DIRECTORY + imageName));
        } catch (IOException e) {
            throw new ResourceNotFoundException(String.format("Image of product with id: %s not found", productId));
        }
    }
}
