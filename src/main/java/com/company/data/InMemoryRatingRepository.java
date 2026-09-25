package com.company.data;

import com.company.models.Rating;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRatingRepository implements RatingRepository {

    private static int nextId = 1;
    private List<Rating> ratings = new ArrayList<>();

    @Override
    public int createRating(Rating rating) {
        rating.setId(nextId++);
        ratings.add(rating);
        return rating.getId();
    }

    @Override
    public Rating getRatingById(int ratingId) {
        for (Rating rating : ratings) {
            if (rating.getId() == ratingId) {
                return rating;
            }
        }
        return null;
    }

    @Override
    public List<Rating> getRatingsByMediaId(int mediaId) {
        List<Rating> ratingsResults = new ArrayList<>();
        for (Rating rating : ratings) {
            if (rating.getMediaEntryId() == mediaId) {
                ratingsResults.add(rating);
            }
        }
        return ratingsResults;
    }

    @Override
    public List<Rating> getRatingsByUserId(int userId) {
        List<Rating> ratingResults = new ArrayList<>();
        for (Rating rating : ratings) {
            if (rating.getUserId() == userId) {
                ratingResults.add(rating);
            }
        }
        return ratingResults;
    }

    @Override
    public Rating updateRating(Rating rating) {
        for (int i = 0; i < ratings.size(); i++) {
            if (ratings.get(i).getId() == rating.getId()) {
                ratings.set(i, rating);
                return rating;
            }
        }
        return null;
    }

    @Override
    public void deleteRating(int ratingId) {
        for (int i = 0; i < ratings.size(); i++) {
            if (ratings.get(i).getId() == ratingId) {
                ratings.remove(i);
                break;
            }
        }
    }
}
