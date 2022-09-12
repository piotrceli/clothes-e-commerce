package com.junior.company.ecommerce.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMatch {

    String message() default "Fields must match";
    String firstField();
    String secondField();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List
    {
        FieldMatch[] value();
    }
}
