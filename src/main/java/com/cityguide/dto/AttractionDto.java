package com.cityguide.dto;

import com.cityguide.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Информация о достопримечательности")
public class AttractionDto {

    private Long id;

    private String name;

    private Category category;

    private Double latitude;

    private Double longitude;

    private String description;

    private String address;

    private Double averageRating;

    private Long ratingCount;

    private Double distanceKm;
}
