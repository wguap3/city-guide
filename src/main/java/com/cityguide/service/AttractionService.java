package com.cityguide.service;

import com.cityguide.dto.*;

import java.util.List;

public interface AttractionService {

    AttractionDto create(AttractionCreateDto dto);

    AttractionDto getById(Long id);

    List<AttractionDto> findNearby(NearbySearchRequest request);

    ReviewDto addRating(Long attractionId, RatingCreateDto dto);

    ReviewDto addReview(Long attractionId, ReviewCreateDto dto);

    List<ReviewDto> getReviews(Long attractionId);
}
