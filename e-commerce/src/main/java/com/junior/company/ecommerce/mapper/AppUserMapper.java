package com.junior.company.ecommerce.mapper;

import com.junior.company.ecommerce.dto.AddressRequest;
import com.junior.company.ecommerce.dto.AppUserRequest;
import com.junior.company.ecommerce.dto.AppUserResponse;
import com.junior.company.ecommerce.model.Address;
import com.junior.company.ecommerce.model.AppUser;

import java.util.List;
import java.util.stream.Collectors;

import static com.junior.company.ecommerce.mapper.constant.SharedConstant.EMPTY_ID;

public class AppUserMapper {

    public static AppUserResponse mapAppUserToAppUserResponse(AppUser appUser) {
        return AppUserResponse.builder()
                .id(appUser.getId())
                .email(appUser.getEmail())
                .roles(appUser.getRoles())
                .enabled(appUser.isEnabled())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .phoneNumber(appUser.getPhoneNumber())
                .dob(appUser.getDob())
                .address(appUser.getAddress())
                .build();
    }

    public static List<AppUserResponse> mapAppUsersToAppUserResponses(List<AppUser> appUsers) {
        return appUsers.stream().map(AppUserMapper::mapAppUserToAppUserResponse).collect(Collectors.toList());
    }

    public static AppUser mapAppUserRequestToAppUserCreate(AppUserRequest appUserRequest) {
        return AppUser.builder()
                .id(EMPTY_ID)
                .email(appUserRequest.getEmail())
                .firstName(appUserRequest.getFirstName())
                .lastName(appUserRequest.getLastName())
                .phoneNumber(appUserRequest.getPhoneNumber())
                .dob(appUserRequest.getDob())
                .address(mapAddressRequestToAddressCreate(appUserRequest.getAddress()))
                .build();
    }

    public static AppUser mapAppUserRequestToAppUserUpdate(AppUserRequest appUserRequest,
                                                           Long addressId) {
        return AppUser.builder()
                .id(appUserRequest.getId())
                .firstName(appUserRequest.getFirstName())
                .lastName(appUserRequest.getLastName())
                .phoneNumber(appUserRequest.getPhoneNumber())
                .dob(appUserRequest.getDob())
                .address(mapAddressRequestToAddressUpdate(appUserRequest.getAddress(), addressId))
                .build();
    }

    private static Address mapAddressRequestToAddressUpdate(AddressRequest addressRequest,
                                                            Long addressId) {
        return Address.builder()
                .id(addressId)
                .apartmentNumber(addressRequest.getApartmentNumber())
                .street(addressRequest.getStreet())
                .city(addressRequest.getCity())
                .country(addressRequest.getCountry())
                .build();
    }

    private static Address mapAddressRequestToAddressCreate(AddressRequest addressRequest) {
        return Address.builder()
                .id(EMPTY_ID)
                .apartmentNumber(addressRequest.getApartmentNumber())
                .street(addressRequest.getStreet())
                .city(addressRequest.getCity())
                .country(addressRequest.getCountry())
                .build();
    }

}
