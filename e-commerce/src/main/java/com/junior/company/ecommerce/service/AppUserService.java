package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.AppUserRequest;
import com.junior.company.ecommerce.dto.AppUserResponse;
import com.junior.company.ecommerce.model.AppUser;

import java.util.List;

public interface AppUserService {

    List<AppUserResponse> findUsers();

    AppUserResponse findUserById(Long userId);

    boolean registerUser(AppUserRequest appUserRequest);

    boolean updateUser(AppUserRequest appUserRequest);

    boolean deleteUserById(Long userId);

    AppUser getCurrentUser();
}
