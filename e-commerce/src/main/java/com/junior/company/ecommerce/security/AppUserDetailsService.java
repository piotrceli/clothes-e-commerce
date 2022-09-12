package com.junior.company.ecommerce.security;

import com.junior.company.ecommerce.model.AppUser;
import com.junior.company.ecommerce.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: {}", email);
        AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User with email: %s not found", email)));
        return new AppUserDetails(appUser);
    }
}
