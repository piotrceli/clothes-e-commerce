package com.junior.company.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@Getter
public class Response {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSS")
    @ApiModelProperty(notes = "Timestamp of a request", example = "2022-12-12 20:01:22.1873561")
    private LocalDateTime timestamp;
    @ApiModelProperty(notes = "Http status code", example = "OK")
    private HttpStatus status;
    @ApiModelProperty(notes = "Http status code value", example = "200")
    private int statusCode;
    @ApiModelProperty(notes = "Information message", example = "Retrieved list of trainers")
    private String message;
    @ApiModelProperty(notes = "Retrieved data")
    private Map<?, ?> data;
}
