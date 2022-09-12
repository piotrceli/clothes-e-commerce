package com.junior.company.ecommerce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ProductRequest {

    private Long id;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "Product's name.", example = "black basic t-shirt")
    private String name;

    @NotNull(message = "Cannot be empty")
    @Min(value = 0, message = "Min is 0")
    @ApiModelProperty(notes = "Product's price.", example = "29.99")
    private Double price;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "Product's description.", example = "Casual t-shirt.")
    private String description;

    @NotNull(message = "Cannot be empty")
    @ApiModelProperty(notes = "Categories' ids suitable for product.", example = "[1, 2]")
    private Set<Long> categoriesIds;
}
