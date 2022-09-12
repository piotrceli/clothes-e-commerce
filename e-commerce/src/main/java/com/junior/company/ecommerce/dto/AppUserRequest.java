package com.junior.company.ecommerce.dto;

import com.junior.company.ecommerce.validation.FieldMatch;
import com.junior.company.ecommerce.validation.ValidEmail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@FieldMatch.List({
        @FieldMatch(firstField = "password", secondField = "matchingPassword", message = "The password must match")})
@Getter
@SuperBuilder
@NoArgsConstructor
public class AppUserRequest {

    private Long id;

    @NotBlank(message = "Cannot be empty")
    @ValidEmail
    @ApiModelProperty(notes = "User's email", example = "tom@email.com")
    private String email;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 8, message = "Min length is 8")
    @ApiModelProperty(notes = "User's password with minimum 8 signs", example = "1y5E3X8k")
    private String password;

    @NotBlank(message = "Cannot be empty")
    @ApiModelProperty(notes = "Matching User's password with minimum 8 signs", example = "1y5E3X8k")
    private String matchingPassword;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "User's first name", example = "Tom")
    private String firstName;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "User's last name", example = "Jones")
    private String lastName;

    @NotBlank(message = "Cannot be empty")
    @Length(min = 2, message = "Min length is 2")
    @ApiModelProperty(notes = "User's phone number", example = "500600700")
    private String phoneNumber;

    @NotNull(message = "Cannot be empty")
    @ApiModelProperty(notes = "User's date of birth", example = "1990-02-20")
    private LocalDate dob;

    @Valid
    @NotNull(message = "Cannot be empty")
    @ApiModelProperty(notes = "User's address information.")
    private AddressRequest address;
}
