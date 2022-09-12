package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.AddressRequest;
import com.junior.company.ecommerce.dto.AppUserRequest;
import com.junior.company.ecommerce.dto.AppUserResponse;
import com.junior.company.ecommerce.exception.PermissionDeniedException;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.mapper.AppUserMapper;
import com.junior.company.ecommerce.model.Address;
import com.junior.company.ecommerce.model.AppUser;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.Role;
import com.junior.company.ecommerce.repository.AppUserRepository;
import com.junior.company.ecommerce.repository.RoleRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    void shouldGetListOfUsers() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        List<AppUser> appUsers = new ArrayList<>(List.of(appUser));
        given(appUserRepository.findUsersAndAddresses()).willReturn(appUsers);
        List<AppUserResponse> appUserResponses = AppUserMapper.mapAppUsersToAppUserResponses(appUsers);

        // when
        List<AppUserResponse> result = appUserService.findUsers();

        // then
        verify(appUserRepository, times(1)).findUsersAndAddresses();
        assertThat(result).usingRecursiveComparison().isEqualTo(appUserResponses);
    }

    @Test
    void shouldFindUserById_whenCurrentUserIsAdmin_givenValidUserId() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findUserAndAddressByUserId(anyLong())).willReturn(Optional.of(appUser));

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("admin@email.com");
        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(appUser));

        AppUserResponse appUserResponse = AppUserMapper.mapAppUserToAppUserResponse(appUser);

        // when
        AppUserResponse result = appUserService.findUserById(1L);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(appUserResponse);
    }

    @Test
    void shouldFindUserById_whenCurrentUserIdIsEqualsUserId_givenValidUserId() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findUserAndAddressByUserId(anyLong())).willReturn(Optional.of(appUser));

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("admin@email.com");

        given(appUserRepository.findByEmail("admin@email.com")).willReturn(Optional.of(appUser));

        AppUserResponse appUserResponse = AppUserMapper.mapAppUserToAppUserResponse(appUser);

        // when
        AppUserResponse result = appUserService.findUserById(1L);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(appUserResponse);
    }

    @Test
    void shouldNotFindUserById_givenInvalidUserId() {

        // given
        Long userId = 0L;

        given(appUserRepository.findUserAndAddressByUserId(userId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> appUserService.findUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("User with id: %s not found", userId));
    }

    @Test
    void shouldNotFindUserById_whenCurrentUserIdIsNotEqualsUserId_givenValidUserId() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        AppUser currentAppUser = AppUser.builder()
                .id(2L)
                .email("current@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("current")
                .lastName("current")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findUserAndAddressByUserId(anyLong())).willReturn(Optional.of(appUser));

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("current@email.com");

        given(appUserRepository.findByEmail("current@email.com")).willReturn(Optional.of(currentAppUser));

        // when then
        assertThatThrownBy(() -> appUserService.findUserById(1L))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    void shouldGetCurrentUser() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(appUser.getEmail());

        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(appUser));

        // when
        AppUser result = appUserService.getCurrentUser();

        // then
        assertThat(result).isEqualTo(appUser);
    }

    @Test
    void shouldNotGetCurrentUser() {

        // given
        String email = "current_user";

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(email);

        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> appUserService.getCurrentUser())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format(
                        "User with email %s not found", email));
    }

    @Test
    void shouldRegisterUser_givenValidAppUserRequest() {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .apartmentNumber(101)
                .street("street_name")
                .city("city_name")
                .country("country_name")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .email("email@email.com")
                .password("password")
                .matchingPassword("password")
                .firstName("firstname")
                .lastName("lastname")
                .phoneNumber("123456789")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();

        Role userRole = new Role(1L, "USER");
        Cart cart = Cart.builder()
                .totalValue(0.0)
                .build();

        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(roleRepository.findRoleByName(anyString())).willReturn(Optional.of(userRole));

        AppUser appUser = AppUserMapper.mapAppUserRequestToAppUserCreate(appUserRequest);
        appUser.setEnabled(true);
        appUser.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));
        appUser.setRoles(List.of(userRole));
        appUser.setCart(cart);

        // when
        boolean result = appUserService.registerUser(appUserRequest);

        // then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(appUserArgumentCaptor.capture());
        AppUser capturedAppUser = appUserArgumentCaptor.getValue();

        assertThat(capturedAppUser).usingRecursiveComparison().isEqualTo(appUser);
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotRegisterUser_givenEmailIsAlreadyTaken() {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .apartmentNumber(101)
                .street("street_name")
                .city("city_name")
                .country("country_name")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .email("email@email.com")
                .password("password")
                .matchingPassword("password")
                .firstName("firstname")
                .lastName("lastname")
                .phoneNumber("123456789")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(appUser));

        // when then
        assertThatThrownBy(() -> appUserService.registerUser(appUserRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(
                        "Email %s is already taken", appUserRequest.getEmail()));
    }

    @Test
    void shouldUpdateUser_givenValidAppUserRequest() {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(1L)
                .apartmentNumber(101)
                .street("street_name")
                .city("city_name")
                .country("country_name")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(1L)
                .email("email@email.com")
                .password("password")
                .matchingPassword("password")
                .firstName("firstname")
                .lastName("lastname")
                .phoneNumber("123456789")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();
        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser currentUser = AppUser.builder()
                .id(1L)
                .email("user@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("user")
                .lastName("user")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("user@email.com");
        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(currentUser));

        AppUser updatedUser = AppUserMapper
                .mapAppUserRequestToAppUserUpdate(appUserRequest, currentUser.getAddress().getId());
        updatedUser.setEmail(currentUser.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));
        updatedUser.setRoles(currentUser.getRoles());
        updatedUser.setEnabled(currentUser.isEnabled());
        updatedUser.setCart(currentUser.getCart());
        updatedUser.setOrders(currentUser.getOrders());

        given(appUserRepository.save(any())).willReturn(updatedUser);

        // when
        boolean result = appUserService.updateUser(appUserRequest);

        // then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(appUserRepository).save(appUserArgumentCaptor.capture());
        AppUser capturedAppUser = appUserArgumentCaptor.getValue();

        assertThat(capturedAppUser).usingRecursiveComparison().isEqualTo(updatedUser);
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotUpdateUser_whenCurrentUserGotNoPermission() {

        // given
        AddressRequest addressRequest = AddressRequest.builder()
                .id(2L)
                .apartmentNumber(101)
                .street("street_name")
                .city("city_name")
                .country("country_name")
                .build();
        AppUserRequest appUserRequest = AppUserRequest.builder()
                .id(2L)
                .email("email@email.com")
                .password("password")
                .matchingPassword("password")
                .firstName("firstname")
                .lastName("lastname")
                .phoneNumber("123456789")
                .dob(LocalDate.of(2000, 1, 1))
                .address(addressRequest)
                .build();
        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser currentUser = AppUser.builder()
                .id(1L)
                .email("user@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("user")
                .lastName("user")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("user@email.com");
        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(currentUser));

        // when then
        assertThatThrownBy(() -> appUserService.updateUser(appUserRequest))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    void shouldDeleteUserById_whenCurrentUserIsAdmin_givenValidUserId() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser currentUser = AppUser.builder()
                .id(1L)
                .email("admin@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("admin")
                .lastName("admin")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findById(anyLong())).willReturn(Optional.of(currentUser));

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("admin@email.com");
        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(currentUser));

        // when
        boolean result = appUserService.deleteUserById(99L);

        // then
        assertThat(result).isTrue();
        verify(appUserRepository, times(1)).delete(currentUser);
    }

    @Test
    void shouldDeleteUserById_whenCurrentUserIsUser_givenValidUserId() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser currentUser = AppUser.builder()
                .id(1L)
                .email("user@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("user")
                .lastName("user")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findById(anyLong())).willReturn(Optional.of(currentUser));

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("admin@email.com");
        given(appUserRepository.findByEmail(anyString())).willReturn(Optional.of(currentUser));

        // when
        boolean result = appUserService.deleteUserById(1L);

        // then
        assertThat(result).isTrue();
        verify(appUserRepository, times(1)).delete(currentUser);
    }

    @Test
    void shouldNotDeleteUserById_whenCurrentUserGotNoPermission() {

        // given
        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        Cart cart1 = Cart.builder()
                .totalValue(0.0)
                .build();
        Address address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        AppUser currentUser = AppUser.builder()
                .id(1L)
                .email("user@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("user")
                .lastName("user")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();
        AppUser anotherUser = AppUser.builder()
                .id(2L)
                .email("other@email.com")
                .password(passwordEncoder.encode("password"))
                .roles(List.of(role))
                .enabled(true)
                .firstName("other")
                .lastName("other")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart1)
                .build();

        given(appUserRepository.findById(anyLong())).willReturn(Optional.of(anotherUser));

        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("admin@email.com");
        given(appUserRepository.findByEmail(any())).willReturn(Optional.of(currentUser));

        // when then
        assertThatThrownBy(() -> appUserService.deleteUserById(2L))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessageContaining("Permission denied");
    }

    @Test
    void shouldNotDeleteUserById_givenInvalidUserId() {

        // given
        Long userId = 0L;

        given(appUserRepository.findById(userId)).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> appUserService.deleteUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("User with id: %s not found", userId));
    }
}