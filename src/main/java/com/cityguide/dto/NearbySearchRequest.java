package com.cityguide.dto;

import com.cityguide.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Параметры поиска достопримечательностей поблизости")
public class NearbySearchRequest {

    @NotNull
    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double lat;

    @NotNull
    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double lon;

    @NotNull
    @DecimalMin("0.0")
    private Double radius;

    private Category category;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private Double minRating;

    @Min(1) @Max(100)
    private Integer maxResults = 10;

    private String sortBy = "distance";
}
