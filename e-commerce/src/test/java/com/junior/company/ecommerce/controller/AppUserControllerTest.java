package com.junior.company.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.company.ecommerce.dto.AddressRequest;
import com.junior.company.ecommerce.dto.AppUserRequest;
import com.junior.company.ecommerce.dto.AppUserResponse;
import com.junior.company.ecommerce.exception.PermissionDeniedException;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.mapper.AppUserMapper;
import com.junior.company.ecommerce.model.*;
import com.junior.company.ecommerce.security.AppUserDetailsService;
import com.junior.company.ecommerce.service.AppUserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppUserController.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppUserService appUserService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetListOfUsers() throws Exception {

        // given
        Role role = new Role(1L, "USER");
        Address addressOne = Address.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        Cart cartOne = new Cart();
        AppUser appUserOne = AppUser.builder()
                .id(1L)
                .email("one@email.com")
                .password("password_one")
                .roles(List.of(role))
                .enabled(true)
                .firstName("firstname_one")
                .lastName("lastname_one")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressOne)
                .cart(cartOne)
                .build();

        Address addressTwo = Address.builder()
                .id(2L)
                .apartmentNumber(200)
                .street("street_two")
                .city("city_two")
                .country("country_two")
                .build();
        Cart cartTwo = new Cart();
        AppUser appUserTwo = AppUser.builder()
                .id(1L)
                .email("one@email.com")
                .password("password_two")
                .roles(List.of(role))
                .enabled(true)
                .firstName("firstname_two")
                .lastName("lastname_two")
                .phoneNumber("123456789")
                .dob(LocalDate.of(2002, 2, 2))
                .address(addressTwo)
                .cart(cartTwo)
                .build();

        List<AppUser> appUsers = List.of(appUserOne, appUserTwo);

        given(appUserService.findUsers()).willReturn(
                AppUserMapper.mapAppUsersToAppUserResponses(appUsers));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of users")
                .data(Map.of("users", appUserService.findUsers()))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldGetUserById_givenValidUserId() throws Exception {

        // given
        Role role = new Role(1L, "USER");
        Address addressOne = Address.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        Cart cartOne = new Cart();
        Long userId = 1L;
        AppUser appUser = AppUser.builder()
                .id(userId)
                .email("one@email.com")
                .password("password_one")
                .roles(List.of(role))
                .enabled(true)
                .firstName("firstname_one")
                .lastName("lastname_one")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressOne)
                .cart(cartOne)
                .build();

        AppUserResponse appUserResponse = AppUserMapper.mapAppUserToAppUserResponse(appUser);

        given(appUserService.findUserById(userId)).willReturn(appUserResponse);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved user by id: %s", userId))
                .data(Map.of("user", appUserService.findUserById(userId)))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldNotGetUserById_whenUserGotNoPermission() throws Exception {

        // given
        Long userId = 1L;

        given(appUserService.findUserById(userId)).willThrow(
                new PermissionDeniedException("Permission denied"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Permission denied")
                .build();

        // when then
        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldNotGetUserById_givenInvalidUserId() throws Exception {

        // given
        Long userId = 0L;

        given(appUserService.findUserById(userId)).willThrow(
                new ResourceNotFoundException(String.format("User with id: %s not found", userId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("User with id: %s not found", userId))
                .build();

        // when then
        mockMvc.perform(get("/api/v1/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldRegisterUser_givenValidAppUserRequest() throws Exception {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(1L)
                .email("one@email.com")
                .password("password_one")
                .matchingPassword("password_one")
                .firstName("firstname_one")
                .lastName("lastname_one")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();

        given(appUserService.registerUser(any())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Registered new user")
                .data(Map.of("is_registered", appUserService.registerUser(appUserRequest)))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldNotRegisterUser_givenInvalidAppUserRequestEmail() throws Exception {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(1L)
                .email("email.com")
                .password("password_one")
                .matchingPassword("password_one")
                .firstName("firstname_one")
                .lastName("lastname_one")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();

        Map<String, String> errors = new HashMap<>();
        String fieldName = "email";
        String errorMessage = "Invalid email";
        errors.put(fieldName, errorMessage);
        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("error occurred")
                .data(Map.of("errors", errors))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void shouldNotRegisterUser_givenAlreadyTakenEmail() throws Exception {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(1L)
                .email("one@email.com")
                .password("password_one")
                .matchingPassword("password_one")
                .firstName("firstname_one")
                .lastName("lastname_one")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();

        given(appUserService.registerUser(any())).willThrow(
                new IllegalStateException(String.format("Email %s is already taken", appUserRequest.getEmail())));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(String.format("Email %s is already taken", appUserRequest.getEmail()))
                .build();

        // when then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldUpdateUser_givenValidAppUserRequest() throws Exception {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(1L)
                .email("one@email.com")
                .password("password_one")
                .matchingPassword("password_one")
                .firstName("updated_firstname")
                .lastName("updated_lastname")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();

        given(appUserService.updateUser(any())).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated user")
                .data(Map.of("is_updated", appUserService.updateUser(appUserRequest)))
                .build();

        // when then
        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotUpdateUser_givenUserGotNoPermission() throws Exception {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(1L)
                .apartmentNumber(100)
                .street("street_one")
                .city("city_one")
                .country("country_one")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(1L)
                .email("one@email.com")
                .password("password_one")
                .matchingPassword("password_one")
                .firstName("updated_firstname")
                .lastName("updated_lastname")
                .phoneNumber("987654321")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();

        given(appUserService.updateUser(any())).willThrow(
                new PermissionDeniedException("Permission denied"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Permission denied")
                .build();

        // when then
        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldDeleteUserById_givenValidUserId() throws Exception {

        // given
        Long userId = 1L;

        given(appUserService.deleteUserById(userId)).willReturn(true);

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted user with id: %s", userId))
                .data(Map.of("is_deleted", appUserService.deleteUserById(userId)))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldNotDeleteUserById_givenUserGotNoPermission() throws Exception {

        // given
        Long userId = 1L;

        given(appUserService.deleteUserById(userId)).willThrow(
                new PermissionDeniedException("Permission denied"));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Permission denied")
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void shouldNotDeleteUserById_givenInvalidUserId() throws Exception {

        // given
        Long userId = 0L;

        given(appUserService.deleteUserById(userId)).willThrow(
                new ResourceNotFoundException(String.format("User with id: %s not found", userId)));

        Response expectedResponseBody = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(String.format("User with id: %s not found", userId))
                .build();

        // when then
        mockMvc.perform(delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }
}