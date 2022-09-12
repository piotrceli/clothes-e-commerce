package com.junior.company.ecommerce.controller;

import com.junior.company.ecommerce.dto.AppUserRequest;
import com.junior.company.ecommerce.model.Response;
import com.junior.company.ecommerce.service.AppUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.junior.company.ecommerce.swagger.SwaggerConstants.USERS_API_TAG;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Api(tags = {USERS_API_TAG})
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Get a list of all users", notes = "Available for ADMIN\n\n" +
            "Allows to view a list of all users registered in the system.")
    public ResponseEntity<Response> findUsers() {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Retrieved list of users")
                .data(Map.of("users", appUserService.findUsers()))
                .build());
    }

    @GetMapping("{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ApiOperation(value = "Get an user by id", notes = "Available for ADMIN, USER\n\n" +
            "Allows to view an user by id. Every user can be viewed by an admin, but " +
            "the current logged user can only view himself.")
    public ResponseEntity<Response> findUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Retrieved user by id: %s", userId))
                .data(Map.of("user", appUserService.findUserById(userId)))
                .build());
    }

    @PostMapping
    @ApiOperation(value = "Register a new user", notes = "Available for EVERYONE\n\n" +
            "Allows to register a new user in the system. The user's information must be valid. " +
            "Email must be unique. The registered user obtains 'USER' role.")
    public ResponseEntity<Response> registerUser(@Valid @RequestBody AppUserRequest appUserRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/users").toUriString());
        return ResponseEntity.created(uri).body(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .message("Registered new user")
                .data(Map.of("is_registered", appUserService.registerUser(appUserRequest)))
                .build());
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Update an existing user", notes = "Available for USER\n\n" +
            "Allows to update an existing user. The current logged user can update his own account. " +
            "Email cannot be updated.")
    public ResponseEntity<Response> updateUser(@Valid @RequestBody AppUserRequest appUserRequest) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Updated user")
                .data(Map.of("is_updated", appUserService.updateUser(appUserRequest)))
                .build());
    }

    @DeleteMapping("{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ApiOperation(value = "Delete an existing user by id", notes = "Available for ADMIN, USER\n\n" +
            "Allows to delete an existing user by id from the system. Every user can be deleted by an admin, " +
            "but the current logged user can only delete his own account.")
    public ResponseEntity<Response> deleteUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(Response.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deleted user with id: %s", userId))
                .data(Map.of("is_deleted", appUserService.deleteUserById(userId)))
                .build());
    }
}
