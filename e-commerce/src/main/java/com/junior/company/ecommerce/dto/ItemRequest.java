package com.junior.company.ecommerce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ItemRequest {

    private Long id;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 1, message = "Min length is 1")
    @ApiModelProperty(notes = "Specific product item's size.", example = "XL")
    private String size;

    @NotNull(message = "Cannot be empty")
    @Min(value = 0, message = "Min is 0")
    @ApiModelProperty(notes = "Specific product item's quantity in the stock.", example = "30")
    private Integer quantity;
}
