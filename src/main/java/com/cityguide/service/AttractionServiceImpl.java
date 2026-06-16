package com.cityguide.service;

import com.cityguide.dto.*;
import com.cityguide.entity.Attraction;
import com.cityguide.entity.Review;
import com.cityguide.exception.ResourceNotFoundException;
import com.cityguide.mapper.AttractionMapper;
import com.cityguide.mapper.ReviewMapper;
import com.cityguide.repository.AttractionRepository;
import com.cityguide.repository.ReviewRepository;
import com.cityguide.repository.projection.AttractionNearbyProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttractionServiceImpl implements AttractionService {

    private final AttractionRepository attractionRepository;
    private final ReviewRepository reviewRepository;
    private final AttractionMapper attractionMapper;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public AttractionDto create(AttractionCreateDto dto) {
        Attraction attraction = attractionMapper.toEntity(dto);
        attraction = attractionRepository.save(attraction);
        return enrichDto(attractionMapper.toDto(attraction), attraction.getId());
    }

    @Override
    public AttractionDto getById(Long id) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.attraction(id));
        return enrichDto(attractionMapper.toDto(attraction), id);
    }

    @Override
    public List<AttractionDto> findNearby(NearbySearchRequest req) {
        String categoryStr = req.getCategory() != null ? req.getCategory().name() : null;

        List<AttractionNearbyProjection> projections = attractionRepository.findNearby(
                req.getLat(), req.getLon(), req.getRadius(), categoryStr, req.getMinRating()
        );

        Comparator<AttractionNearbyProjection> comparator = switch (
                req.getSortBy() == null ? "distance" : req.getSortBy().toLowerCase()) {
            case "rating"   -> Comparator.comparingDouble(
                    p -> -(p.getAverageRating() == null ? 0.0 : p.getAverageRating()));
            case "name"     -> Comparator.comparing(AttractionNearbyProjection::getName,
                    String.CASE_INSENSITIVE_ORDER);
            default         -> Comparator.comparingDouble(p ->
                    p.getDistanceKm() == null ? Double.MAX_VALUE : p.getDistanceKm());
        };

        return projections.stream()
                .sorted(comparator)
                .limit(req.getMaxResults() == null ? 10 : req.getMaxResults())
                .map(this::projectionToDto)
                .toList();
    }

    @Override
    @Transactional
    public ReviewDto addRating(Long attractionId, RatingCreateDto dto) {
        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> ResourceNotFoundException.attraction(attractionId));

        Review review = Review.builder()
                .attraction(attraction)
                .authorName(dto.getAuthorName())
                .rating(dto.getRating())
                .build();

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    public ReviewDto addReview(Long attractionId, ReviewCreateDto dto) {
        if (dto.getComment() == null && dto.getRating() == null) {
            throw new IllegalArgumentException("Отзыв должен содержать текст и/или оценку");
        }

        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> ResourceNotFoundException.attraction(attractionId));

        Review review = reviewMapper.toEntity(dto);
        review.setAttraction(attraction);

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public List<ReviewDto> getReviews(Long attractionId) {
        if (!attractionRepository.existsById(attractionId)) {
            throw ResourceNotFoundException.attraction(attractionId);
        }
        return reviewRepository.findByAttractionIdOrderByCreatedAtDesc(attractionId)
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }


    private AttractionDto enrichDto(AttractionDto dto, Long attractionId) {
        dto.setAverageRating(
                reviewRepository.findAverageRatingByAttractionId(attractionId).orElse(null));
        dto.setRatingCount(reviewRepository.countRatingsByAttractionId(attractionId));
        return dto;
    }

    private AttractionDto projectionToDto(AttractionNearbyProjection p) {
        AttractionDto dto = new AttractionDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setCategory(com.cityguide.entity.Category.valueOf(p.getCategory()));
        dto.setLatitude(p.getLatitude());
        dto.setLongitude(p.getLongitude());
        dto.setDescription(p.getDescription());
        dto.setAddress(p.getAddress());
        dto.setAverageRating(p.getAverageRating() != null && p.getAverageRating() > 0
                ? p.getAverageRating() : null);
        dto.setRatingCount(p.getRatingCount());
        dto.setDistanceKm(p.getDistanceKm());
        return dto;
    }
}
