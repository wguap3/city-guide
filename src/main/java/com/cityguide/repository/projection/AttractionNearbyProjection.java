package com.cityguide.repository.projection;


public interface AttractionNearbyProjection {
    Long getId();
    String getName();
    String getCategory();
    Double getLatitude();
    Double getLongitude();
    String getDescription();
    String getAddress();
    Double getAverageRating();
    Long getRatingCount();
    Double getDistanceKm();
}
