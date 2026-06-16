package com.cityguide.mapper;

import com.cityguide.dto.ReviewCreateDto;
import com.cityguide.dto.ReviewDto;
import com.cityguide.entity.Review;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "attractionId", source = "attraction.id")
    ReviewDto toDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attraction", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewCreateDto dto);
}
