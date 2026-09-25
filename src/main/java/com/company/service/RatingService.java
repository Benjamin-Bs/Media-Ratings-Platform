package com.company.service;

import com.company.data.RatingRepository;
import com.company.models.Rating;
import com.company.models.User;

public class RatingService {

    RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating addRating(User user, int mediaId, int stars, String comment) throws Exception {
        // Validierung laut Spec: Sterne müssen zwischen 1 und 5 sein!
        if (stars < 1 || stars > 5) {
            throw new Exception("Bewertung muss zwischen 1 und 5 Sternen liegen!");
        }

        Rating rating = new Rating(user.getId(), mediaId, stars, comment);
        int createId = ratingRepository.createRating(rating);
        return ratingRepository.getRatingById(createId);
    }

    public void likeRating(int ratingId, User user) throws Exception {
        Rating rating = ratingRepository.getRatingById(ratingId);
        if (rating == null) {
            throw new Exception("Rating nicht gefunden!");
        }

        // Validierung laut Spec: Man darf ein Rating nur 1-mal liken!
        if (rating.hasUserLiked(user.getId())) {
            throw new Exception("Du hast diese Bewertung bereits gelikt!");
        }

        rating.getLikedByUserIds().add(user.getId());
        ratingRepository.updateRating(rating);
    }

}