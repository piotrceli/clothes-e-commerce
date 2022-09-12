package com.junior.company.ecommerce.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:static/api-description.html")
public class SwaggerConstants {

    public static final String API_KEY_REFERENCE = "JWT";
    public static final String AUTHORIZATION_SCOPE = "global";
    public static final String AUTHORIZATION_DESCRIPTION = "full access";
    public static final String API_TITLE = "Clothes e-commerce";

    private static String API_DESCRIPTION;
    @Value("${api.description}")
    private void setApiDescription(String apiDescription) {
        API_DESCRIPTION = apiDescription;
    }
    public static String getApiDescription() {
        return API_DESCRIPTION;
    }

    public static final String API_VERSION = "1.0";
    public static final String API_TERMS_OF_SERVICE_URL = null;
    public static final String CONTACT_NAME = null;
    public static final String CONTACT_URL = null;
    public static final String CONTACT_EMAIL = null;
    public static final String API_LICENSE = null;
    public static final String API_LICENSE_URL = null;
    public static final String USERS_API_TAG = "Users service";
    public static final String CATEGORIES_API_TAG = "Categories service";
    public static final String PRODUCTS_API_TAG = "Products service";
    public static final String SHOPPING_API_TAG = "Shopping service";
}
