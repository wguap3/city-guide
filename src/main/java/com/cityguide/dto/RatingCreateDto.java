package com.cityguide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Данные для выставления оценки")
public class RatingCreateDto {

    @NotBlank
    private String authorName;

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;
}
