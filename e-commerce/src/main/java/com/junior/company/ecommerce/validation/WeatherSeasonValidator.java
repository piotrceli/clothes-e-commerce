package com.junior.company.ecommerce.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherSeasonValidator implements ConstraintValidator<ValidWeatherSeason, String> {

    private static final String WEATHER_SEASON_PATTERN = "SPRING|SUMMER|AUTUMN|WINTER|NONE";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(WEATHER_SEASON_PATTERN);
        if (value == null) {
          return false;
        }
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }
}
