package com.cityguide.mapper;

import com.cityguide.dto.AttractionCreateDto;
import com.cityguide.dto.AttractionDto;
import com.cityguide.entity.Attraction;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-16T17:27:26+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class AttractionMapperImpl implements AttractionMapper {

    @Override
    public AttractionDto toDto(Attraction attraction) {
        if ( attraction == null ) {
            return null;
        }

        AttractionDto attractionDto = new AttractionDto();

        attractionDto.setId( attraction.getId() );
        attractionDto.setName( attraction.getName() );
        attractionDto.setCategory( attraction.getCategory() );
        attractionDto.setLatitude( attraction.getLatitude() );
        attractionDto.setLongitude( attraction.getLongitude() );
        attractionDto.setDescription( attraction.getDescription() );
        attractionDto.setAddress( attraction.getAddress() );

        return attractionDto;
    }

    @Override
    public Attraction toEntity(AttractionCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Attraction.AttractionBuilder attraction = Attraction.builder();

        attraction.name( dto.getName() );
        attraction.category( dto.getCategory() );
        attraction.latitude( dto.getLatitude() );
        attraction.longitude( dto.getLongitude() );
        attraction.description( dto.getDescription() );
        attraction.address( dto.getAddress() );

        return attraction.build();
    }

    @Override
    public void updateFromDto(AttractionCreateDto dto, Attraction attraction) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            attraction.setName( dto.getName() );
        }
        if ( dto.getCategory() != null ) {
            attraction.setCategory( dto.getCategory() );
        }
        if ( dto.getLatitude() != null ) {
            attraction.setLatitude( dto.getLatitude() );
        }
        if ( dto.getLongitude() != null ) {
            attraction.setLongitude( dto.getLongitude() );
        }
        if ( dto.getDescription() != null ) {
            attraction.setDescription( dto.getDescription() );
        }
        if ( dto.getAddress() != null ) {
            attraction.setAddress( dto.getAddress() );
        }
    }
}
