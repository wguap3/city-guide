package com.cityguide.repository;

import com.cityguide.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAttractionIdOrderByCreatedAtDesc(Long attractionId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.attraction.id = :attractionId AND r.rating IS NOT NULL")
    Optional<Double> findAverageRatingByAttractionId(@Param("attractionId") Long attractionId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.attraction.id = :attractionId AND r.rating IS NOT NULL")
    Long countRatingsByAttractionId(@Param("attractionId") Long attractionId);
}
