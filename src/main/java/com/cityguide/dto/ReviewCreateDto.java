package com.cityguide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Данные для создания отзыва")
public class ReviewCreateDto {

    @NotBlank
    private String authorName;

    private String comment;

    @Min(1) @Max(5)
    private Integer rating;
}
