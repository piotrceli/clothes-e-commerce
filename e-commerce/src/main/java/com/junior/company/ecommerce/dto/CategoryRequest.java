package com.junior.company.ecommerce.dto;

import com.junior.company.ecommerce.validation.ValidWeatherSeason;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@SuperBuilder
@NoArgsConstructor
public class CategoryRequest {

    private Long id;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "Category's name", example = "t-shirt")
    private String name;

    @ValidWeatherSeason
    @ApiModelProperty(notes = "Weather season suitable for a given category. " +
            "Valid options: SPRING/SUMMER/AUTUMN/WINTER.", example = "SPRING")
    private String weatherSeason;
}
