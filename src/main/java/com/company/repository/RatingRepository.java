package com.company.repository;

import com.company.models.Rating;

import java.util.List;

public interface RatingRepository {

    int createRating(Rating rating);

    Rating getRatingById(int ratingId);

    List<Rating> getRatingsByMediaId(int mediaId);

    List<Rating> getRatingsByUserId(int userId);

    Rating updateRating(Rating rating);

    void deleteRating(int ratingId);

}
