package com.cityguide.repository;

import com.cityguide.entity.Attraction;
import com.cityguide.entity.Category;
import com.cityguide.repository.projection.AttractionNearbyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {


    @Query(value = """
            SELECT
                a.id AS id,
                a.name AS name,
                a.category AS category,
                a.latitude AS latitude,
                a.longitude AS longitude,
                a.description AS description,
                a.address AS address,
                COALESCE(AVG(r.rating), 0) AS averageRating,
                COUNT(r.rating) AS ratingCount,
                (6371 * acos(
                    LEAST(1.0, GREATEST(-1.0,
                        cos(radians(:lat)) * cos(radians(a.latitude))
                        * cos(radians(a.longitude) - radians(:lon))
                        + sin(radians(:lat)) * sin(radians(a.latitude))
                    ))
                )) AS distanceKm
            FROM attractions a
            LEFT JOIN reviews r ON r.attraction_id = a.id AND r.rating IS NOT NULL
            WHERE
                (6371 * acos(
                    LEAST(1.0, GREATEST(-1.0,
                        cos(radians(:lat)) * cos(radians(a.latitude))
                        * cos(radians(a.longitude) - radians(:lon))
                        + sin(radians(:lat)) * sin(radians(a.latitude))
                    ))
                )) <= :radius
                AND (:category IS NULL OR a.category = :category)
            GROUP BY a.id
            HAVING (:minRating IS NULL OR COALESCE(AVG(r.rating), 0) >= :minRating)
            """,
            nativeQuery = true)
    List<AttractionNearbyProjection> findNearby(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radius") double radius,
            @Param("category") String category,
            @Param("minRating") Double minRating
    );
}
