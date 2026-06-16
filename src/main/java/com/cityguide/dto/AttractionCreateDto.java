package com.cityguide.dto;

import com.cityguide.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
@Schema(description = "Данные для создания достопримечательности")
public class AttractionCreateDto {

    @NotBlank
    private String name;

    @NotNull
    private Category category;

    @NotNull
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double longitude;

    private String description;

    private String address;
}
