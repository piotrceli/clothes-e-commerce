package com.junior.company.ecommerce.repository;

import com.junior.company.ecommerce.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldFindRoleByName_givenValidRoleName() {

        // given
        Role role = new Role(1L, "USER");
        roleRepository.save(role);
        String name = "USER";

        // when
        Optional<Role> result = roleRepository.findRoleByName(name);

        // then
        assertThat(result).isPresent();
    }

    @Test
    void shouldNotFindRoleByName_givenInvalidRoleName() {

        // given
        String name = "Invalid";

        // when
        Optional<Role> result = roleRepository.findRoleByName(name);

        // then
        assertThat(result).isEmpty();
    }
}