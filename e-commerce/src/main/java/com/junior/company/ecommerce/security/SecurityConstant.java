package com.junior.company.ecommerce.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SecurityConstant {

    private SecurityConstant(){
    }

    private static String SECRET_KEY;
    @Value("${secret.key}")
    private void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }
    public static String getSecretKey() {
        return SECRET_KEY;
    }

    public static final Date ACCESS_TOKEN_EXPIRES_TIME = new Date(System.currentTimeMillis() + 60 * 60 * 1000);

    public static final String[] SWAGGER_URL = {
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "favicon.ico"
    };

    public static final String LOGIN_URL = "/api/v1/login";
}
