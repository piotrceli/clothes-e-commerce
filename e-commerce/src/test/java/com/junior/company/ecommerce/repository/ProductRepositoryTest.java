package com.junior.company.ecommerce.repository;

import com.junior.company.ecommerce.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldFindPageOfProduct() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl(null)
                .description("description")
                .items(null)
                .categories(null)
                .build();
        productRepository.save(product);
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Product> result = productRepository.findAllPagination(pageRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get().collect(Collectors.toList())).usingRecursiveComparison().isEqualTo(List.of(product));
    }
}