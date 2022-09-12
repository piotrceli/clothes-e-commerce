package com.junior.company.ecommerce.dto;

import com.junior.company.ecommerce.model.Address;
import com.junior.company.ecommerce.model.Role;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@Getter
public class AppUserResponse {

    private Long id;
    private String email;
    private List<Role> roles;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dob;
    private Address address;
}
