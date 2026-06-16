package com.cityguide.mapper;

import com.cityguide.dto.AttractionCreateDto;
import com.cityguide.dto.AttractionDto;
import com.cityguide.entity.Attraction;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AttractionMapper {


    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "distanceKm", ignore = true)
    AttractionDto toDto(Attraction attraction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Attraction toEntity(AttractionCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateFromDto(AttractionCreateDto dto, @MappingTarget Attraction attraction);
}
