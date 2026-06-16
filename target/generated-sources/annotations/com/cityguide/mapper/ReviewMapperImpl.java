package com.cityguide.mapper;

import com.cityguide.dto.ReviewCreateDto;
import com.cityguide.dto.ReviewDto;
import com.cityguide.entity.Attraction;
import com.cityguide.entity.Review;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-16T17:27:26+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public ReviewDto toDto(Review review) {
        if ( review == null ) {
            return null;
        }

        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setAttractionId( reviewAttractionId( review ) );
        reviewDto.setId( review.getId() );
        reviewDto.setAuthorName( review.getAuthorName() );
        reviewDto.setRating( review.getRating() );
        reviewDto.setComment( review.getComment() );
        reviewDto.setCreatedAt( review.getCreatedAt() );

        return reviewDto;
    }

    @Override
    public Review toEntity(ReviewCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Review.ReviewBuilder review = Review.builder();

        review.authorName( dto.getAuthorName() );
        review.rating( dto.getRating() );
        review.comment( dto.getComment() );

        return review.build();
    }

    private Long reviewAttractionId(Review review) {
        if ( review == null ) {
            return null;
        }
        Attraction attraction = review.getAttraction();
        if ( attraction == null ) {
            return null;
        }
        Long id = attraction.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
