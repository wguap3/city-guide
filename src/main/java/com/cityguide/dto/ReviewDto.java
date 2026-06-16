package com.cityguide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Отзыв о достопримечательности")
public class ReviewDto {

    private Long id;

    private Long attractionId;

    private String authorName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}
