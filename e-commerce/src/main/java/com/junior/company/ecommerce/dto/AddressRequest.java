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
public class AddressRequest {

    private Long id;

    @NotNull(message = "Cannot be empty")
    @Min(value = 0, message = "Min is 0")
    @ApiModelProperty(notes = "User's address apartment number", example = "101")
    private Integer apartmentNumber;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "User's address street name", example = "Pine")
    private String street;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "User's address city", example = "Seattle")
    private String city;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "User's address country", example = "USA")
    private String country;
}
