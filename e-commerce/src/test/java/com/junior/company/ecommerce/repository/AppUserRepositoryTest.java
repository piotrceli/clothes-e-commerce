package com.junior.company.ecommerce.repository;

import com.junior.company.ecommerce.model.Address;
import com.junior.company.ecommerce.model.AppUser;
import com.junior.company.ecommerce.model.Cart;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    private static Cart cart;
    private static Address address;
    private static AppUser appUser;

    @BeforeAll
    static void beforeAll() {
        cart = Cart.builder()
                .id(1L)
                .totalValue(0.0)
                .cartItems(new ArrayList<>())
                .build();
        address = Address.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street")
                .city("city")
                .country("country")
                .build();
        appUser = AppUser.builder()
                .id(1L)
                .email("email@email.com")
                .password("password")
                .roles(null)
                .enabled(true)
                .firstName("first_name")
                .lastName("last_name")
                .phoneNumber("987654321")
                .dob(LocalDate.of(1990, 2, 2))
                .address(address)
                .cart(cart)
                .orders(Collections.emptyList())
                .build();
    }

    @Test
    void shouldFindUsersAndAddresses() {

        // given
        appUserRepository.save(appUser);

        // when
        List<AppUser> result = appUserRepository.findUsersAndAddresses();

        // then
        assertThat(result).usingRecursiveComparison().ignoringFields("id", "address.id", "cart.id").isEqualTo(List.of(appUser));
    }

    @Test
    void shouldFindUserAndAddressById_givenValidUserId() {

        // given
        appUserRepository.save(appUser);
        Long userId = 1L;

        // when
        Optional<AppUser> result = appUserRepository.findUserAndAddressByUserId(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getAddress()).usingRecursiveComparison().ignoringFields("id").isEqualTo(address);
    }

    @Test
    void shouldNotFindUserAndAddressById_givenInvalidUserId() {

        // given
        Long userId = 0L;

        // when
        Optional<AppUser> result = appUserRepository.findUserAndAddressByUserId(userId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindUserAndAddressAndCartByUserEmail_givenValidUserEmail() {

        // given
        appUserRepository.save(appUser);
        String email = "email@email.com";

        // when
        Optional<AppUser> result = appUserRepository.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getAddress()).usingRecursiveComparison().ignoringFields("id").isEqualTo(address);
        assertThat(result.get().getCart()).usingRecursiveComparison().ignoringFields("id").isEqualTo(cart);
    }

    @Test
    void shouldNotFindUserAndAddressAndCartByUserEmail_givenInvalidUserEmail() {

        // given
        String email = "invalid@email.com";

        // when
        Optional<AppUser> result = appUserRepository.findByEmail(email);

        // then
        assertThat(result).isEmpty();
    }
}