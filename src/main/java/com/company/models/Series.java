package com.company.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Series extends MediaEntrie {

    @Getter
    @Setter
    private int seasonsCount;

    @Getter
    @Setter
    private int episodesCount;

    public Series(String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID, int seasonsCount, int episodesCount) {
        super(title, description, releaseYear, genres, ageRestriction, creatorID);
        this.seasonsCount = seasonsCount;
        this.episodesCount = episodesCount;
    }

    public Series(int id, String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID, int seasonsCount, int episodesCount) {
        super(id, title, description, releaseYear, genres, ageRestriction, creatorID);
        this.seasonsCount = seasonsCount;
        this.episodesCount = episodesCount;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.SERIES;
    }

}
