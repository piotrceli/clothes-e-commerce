package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.AppUserRequest;
import com.junior.company.ecommerce.dto.AppUserResponse;
import com.junior.company.ecommerce.exception.PermissionDeniedException;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.mapper.AppUserMapper;
import com.junior.company.ecommerce.model.AppUser;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.Role;
import com.junior.company.ecommerce.repository.AppUserRepository;
import com.junior.company.ecommerce.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public List<AppUserResponse> findUsers() {
        log.info("Retrieving list of users");
        return AppUserMapper.mapAppUsersToAppUserResponses(appUserRepository.findUsersAndAddresses());
    }

    @Override
    public AppUserResponse findUserById(Long userId) {
        log.info("Retrieving user by id: {}", userId);
        AppUser appUser = appUserRepository.findUserAndAddressByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("User with id: %s not found", userId)));

        for (Role role : getCurrentUser().getRoles()) {
            if (Objects.equals(role.getName(), "ADMIN") ||
                    Objects.equals(getCurrentUser().getId(), userId)) {
                return AppUserMapper.mapAppUserToAppUserResponse(appUser);
            }
        }
        throw new PermissionDeniedException("Permission denied");
    }

    @Override
    public boolean registerUser(AppUserRequest appUserRequest) {
        log.info("Registering new user");
        if (appUserRepository.findByEmail(appUserRequest.getEmail()).isPresent()) {
            throw new IllegalStateException(String.format("Email %s is already taken", appUserRequest.getEmail()));
        }
        AppUser appUser = AppUserMapper.mapAppUserRequestToAppUserCreate(appUserRequest);
        appUser.setEnabled(true);
        appUser.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));

        Optional<Role> optionalRole = roleRepository.findRoleByName("USER");
        List<Role> roles = new ArrayList<>();
        optionalRole.ifPresent(roles::add);
        appUser.setRoles(roles);
        appUser.setCart(Cart.builder().totalValue(0.0).build());
        appUserRepository.save(appUser);
        return true;
    }

    @Override
    public boolean updateUser(AppUserRequest appUserRequest) {
        log.info("Updating user with email: {}", appUserRequest.getEmail());
        if (!Objects.equals(appUserRequest.getId(), getCurrentUser().getId())) {
            throw new PermissionDeniedException("Permission denied");
        }

        AppUser appUser = AppUserMapper
                .mapAppUserRequestToAppUserUpdate(appUserRequest, getCurrentUser().getAddress().getId());
        appUser.setEmail(getCurrentUser().getEmail());
        appUser.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));
        appUser.setRoles(getCurrentUser().getRoles());
        appUser.setEnabled(getCurrentUser().isEnabled());
        appUser.setCart(getCurrentUser().getCart());
        appUser.setOrders(getCurrentUser().getOrders());
        appUserRepository.save(appUser);
        return true;
    }

    @Override
    public boolean deleteUserById(Long userId) {
        log.info("Deleting user by id: {}", userId);
        AppUser appUser = appUserRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException(String.format("User with id: %s not found", userId)));

        for (Role role : getCurrentUser().getRoles()) {
            if (Objects.equals(role.getName(), "ADMIN") ||
                    (Objects.equals(getCurrentUser().getId(), userId))) {
                appUserRepository.delete(appUser);
                return true;
            }
        }
        throw new PermissionDeniedException("Permission denied");
    }

    @Override
    public AppUser getCurrentUser() {
        Principal principal = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return appUserRepository.findByEmail(principal.getName()).orElseThrow(() ->
                new ResourceNotFoundException(String.format("User with email %s not found", principal.getName())));
    }
}
