package com.junior.company.ecommerce.repository;

import com.junior.company.ecommerce.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT u FROM AppUser u " +
            "LEFT JOIN FETCH u.address")
    List<AppUser> findUsersAndAddresses();

    @Query("SELECT u FROM AppUser u " +
            "LEFT JOIN FETCH u.address " +
            "WHERE u.id = ?1")
    Optional<AppUser> findUserAndAddressByUserId(Long userId);

    @Query("SELECT u FROM AppUser u " +
            "LEFT JOIN FETCH u.address " +
            "LEFT JOIN FETCH u.cart " +
            "WHERE u.email = ?1")
    Optional<AppUser> findByEmail(String email);
}
