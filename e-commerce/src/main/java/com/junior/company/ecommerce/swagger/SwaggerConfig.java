package com.junior.company.ecommerce.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

import static com.junior.company.ecommerce.swagger.SwaggerConstants.API_KEY_REFERENCE;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.API_LICENSE;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.API_LICENSE_URL;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.API_TERMS_OF_SERVICE_URL;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.API_TITLE;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.API_VERSION;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.AUTHORIZATION_DESCRIPTION;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.AUTHORIZATION_SCOPE;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.CATEGORIES_API_TAG;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.CONTACT_EMAIL;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.CONTACT_NAME;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.CONTACT_URL;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.PRODUCTS_API_TAG;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.SHOPPING_API_TAG;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.USERS_API_TAG;
import static com.junior.company.ecommerce.swagger.SwaggerConstants.getApiDescription;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket getDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(singletonList(securityContext()))
                .securitySchemes(singletonList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build()
                .tags(new Tag(USERS_API_TAG, "APIs related to users"),
                        new Tag(CATEGORIES_API_TAG, "APIs related to categories"),
                        new Tag(PRODUCTS_API_TAG, "APIs related to products"),
                        new Tag(SHOPPING_API_TAG, "APIs related to shopping activities"));
    }

    private ApiKey apiKey() {
        return new ApiKey(API_KEY_REFERENCE, AUTHORIZATION, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(securityReference()).build();
    }

    private List<SecurityReference> securityReference() {
        AuthorizationScope[] authorizationScope = {
                new AuthorizationScope(AUTHORIZATION_SCOPE, AUTHORIZATION_DESCRIPTION)};
        return singletonList(new SecurityReference(API_KEY_REFERENCE, authorizationScope));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(API_TITLE, getApiDescription(), API_VERSION, API_TERMS_OF_SERVICE_URL,
                contact(), API_LICENSE, API_LICENSE_URL, emptyList());
    }

    private Contact contact() {
        return new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL);
    }
}
