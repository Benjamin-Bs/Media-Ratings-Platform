package com.company.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Movie extends MediaEntrie {

    @Getter
    @Setter
    private int durationMinutes;

    public Movie(String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID, int durationMinutes) {
        super(title, description, releaseYear, genres, ageRestriction, creatorID);
        this.durationMinutes = durationMinutes;
    }

    public Movie(int id, String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID, int durationMinutes) {
        super(id, title, description, releaseYear, genres, ageRestriction, creatorID);
        this.durationMinutes = durationMinutes;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.MOVIE;
    }

}
