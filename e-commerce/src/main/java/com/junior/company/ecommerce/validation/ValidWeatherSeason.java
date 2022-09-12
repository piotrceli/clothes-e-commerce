package com.junior.company.ecommerce.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = WeatherSeasonValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWeatherSeason {

    String message() default "Given wrong Weather Season. Pick: SPRING / SUMMER / AUTUMN / WINTER / NONE";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
