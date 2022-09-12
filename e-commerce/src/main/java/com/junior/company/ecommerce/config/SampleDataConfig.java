package com.junior.company.ecommerce.config;

import com.junior.company.ecommerce.model.Address;
import com.junior.company.ecommerce.model.AppUser;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.repository.AppUserRepository;
import com.junior.company.ecommerce.repository.CategoryRepository;
import com.junior.company.ecommerce.repository.ItemRepository;
import com.junior.company.ecommerce.repository.ProductRepository;
import com.junior.company.ecommerce.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static com.junior.company.ecommerce.controller.constant.ImagesConstant.HOODIE_URL;
import static com.junior.company.ecommerce.controller.constant.ImagesConstant.TROUSERS_URL;
import static com.junior.company.ecommerce.controller.constant.ImagesConstant.T_SHIRT_URL;
import static com.junior.company.ecommerce.model.WeatherSeason.AUTUMN;
import static com.junior.company.ecommerce.model.WeatherSeason.SPRING;
import static com.junior.company.ecommerce.model.WeatherSeason.SUMMER;
import static com.junior.company.ecommerce.model.WeatherSeason.WINTER;

@Configuration
public class SampleDataConfig {

    @Bean
    public CommandLineRunner commandLineRunner(ProductRepository productRepository,
                                               ItemRepository itemRepository,
                                               CategoryRepository categoryRepository,
                                               PasswordEncoder passwordEncoder,
                                               AppUserRepository appUserRepository,
                                               RoleRepository roleRepository) {
        return args -> {

            Item product1Item1 = new Item("S", 5);
            Item product1Item2 = new Item("M", 3);
            Item product1Item3 = new Item("L", 7);

            Item product2Item1 = new Item("S", 4);
            Item product2Item2 = new Item("M", 11);
            Item product2Item3 = new Item("L", 0);

            Item product3Item1 = new Item("L", 17);
            Item product3Item2 = new Item("M", 3);

            Item product4Item1 = new Item("L", 5);
            Item product4Item2 = new Item("M", 2);

            Item product5Item1 = new Item("S", 5);
            Item product5Item2 = new Item("M", 21);
            Item product5Item3 = new Item("L", 2);
            Item product5Item4 = new Item("XL", 7);

            Item product6Item1 = new Item("S", 2);
            Item product6Item2 = new Item("M", 3);
            Item product6Item3 = new Item("L", 11);
            Item product6Item4 = new Item("XL", 4);

            Product product1 = Product.builder()
                    .name("white basic")
                    .price(29.99)
                    .description("description1")
                    .imageUrl(T_SHIRT_URL)
                    .build();

            Product product2 = Product.builder()
                    .name("black basic")
                    .price(29.99)
                    .description("description2")
                    .imageUrl(T_SHIRT_URL)
                    .build();

            Product product3 = Product.builder()
                    .name("blue jeans")
                    .price(129.99)
                    .description("description3")
                    .imageUrl(TROUSERS_URL)
                    .build();

            Product product4 = Product.builder()
                    .name("black jeans")
                    .price(139.99)
                    .description("description4")
                    .imageUrl(TROUSERS_URL)
                    .build();

            Product product5 = Product.builder()
                    .name("yellow hoodie")
                    .price(79.99)
                    .description("description5")
                    .imageUrl(HOODIE_URL)
                    .build();

            Product product6 = Product.builder()
                    .name("red hoodie")
                    .price(79.99)
                    .description("description6")
                    .imageUrl(HOODIE_URL)
                    .build();

            Category category1 = Category.builder()
                    .name("t-shirt")
                    .weatherSeason(SUMMER)
                    .products(List.of(product1, product2))
                    .build();

            Category category2 = Category.builder()
                    .name("trousers")
                    .weatherSeason(SPRING)
                    .products(List.of(product3, product4))
                    .build();
            Category category3 = Category.builder()
                    .name("hoodie")
                    .weatherSeason(AUTUMN)
                    .products(List.of(product5, product6))
                    .build();
            Category category4 = Category.builder()
                    .name("coat")
                    .weatherSeason(WINTER)
                    .build();

            product1Item1.setProduct(product1);
            product1Item2.setProduct(product1);
            product1Item3.setProduct(product1);
            product2Item1.setProduct(product2);
            product2Item2.setProduct(product2);
            product2Item3.setProduct(product2);
            product3Item1.setProduct(product3);
            product3Item2.setProduct(product3);
            product4Item1.setProduct(product4);
            product4Item2.setProduct(product4);
            product5Item1.setProduct(product5);
            product5Item2.setProduct(product5);
            product5Item3.setProduct(product5);
            product5Item4.setProduct(product5);
            product6Item1.setProduct(product6);
            product6Item2.setProduct(product6);
            product6Item3.setProduct(product6);
            product6Item4.setProduct(product6);

            productRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6));
            itemRepository.saveAll(List.of(product1Item1, product1Item2, product1Item3,
                    product2Item1, product2Item2, product2Item3,
                    product3Item1, product3Item2,
                    product4Item1, product4Item2,
                    product5Item1, product5Item2, product5Item3, product5Item4,
                    product6Item1, product6Item2, product6Item3, product6Item4));
            categoryRepository.saveAll(List.of(category1, category2, category3, category4));

            Address address1 = Address.builder()
                    .apartmentNumber(101)
                    .street("street_name_1")
                    .city("city_name_1")
                    .country("country_name_1")
                    .build();

            Address address2 = Address.builder()
                    .apartmentNumber(202)
                    .street("street_name_2")
                    .city("city_name_2")
                    .country("country_name_2")
                    .build();

            Cart cart1 = Cart.builder()
                    .totalValue(0.0)
                    .build();
            Cart cart2 = Cart.builder()
                    .totalValue(0.0)
                    .build();

            AppUser appUser1 = AppUser.builder()
                    .email("admin@email.com")
                    .password(passwordEncoder.encode("password"))
                    .roles(List.of(roleRepository.findRoleByName("ADMIN").get()))
                    .enabled(true)
                    .firstName("admin")
                    .lastName("admin")
                    .phoneNumber("100100100")
                    .dob(LocalDate.of(1980, 1, 1))
                    .address(address1)
                    .cart(cart1)
                    .build();

            AppUser appUser2 = AppUser.builder()
                    .email("user@email.com")
                    .password(passwordEncoder.encode("password"))
                    .roles(List.of(roleRepository.findRoleByName("USER").get()))
                    .enabled(true)
                    .firstName("user")
                    .lastName("user")
                    .phoneNumber("200200200")
                    .dob(LocalDate.of(1990, 1, 1))
                    .address(address2)
                    .cart(cart2)
                    .build();

            appUserRepository.saveAll(List.of(appUser1, appUser2));
        };
    }
}
