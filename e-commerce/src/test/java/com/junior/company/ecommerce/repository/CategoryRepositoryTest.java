package com.junior.company.ecommerce.repository;

import com.junior.company.ecommerce.model.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

import static com.junior.company.ecommerce.model.WeatherSeason.SUMMER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private static Category category;

    @BeforeAll
    static void beforeAll() {
        category = Category.builder()
                .id(1L)
                .name("t-shirt")
                .weatherSeason(SUMMER)
                .products(new ArrayList<>())
                .build();
    }

    @Test
    void shouldFindCategoryByName_givenValidName() {

        // given
        categoryRepository.save(category);
        String name = "t-shirt";

        // when
        Optional<Category> result = categoryRepository.findByName(name);

        // then
        assertThat(result).isPresent();
    }

    @Test
    void shouldNotFindCategoryByName_givenInvalidName() {

        // given
        String name = "invalid_name";

        // when
        Optional<Category> result = categoryRepository.findByName(name);

        // then
        assertThat(result).isEmpty();
    }
}