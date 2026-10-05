package com.company.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Game extends MediaEntrie {

    @Getter
    @Setter
    private String platform;

    public Game(String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID, String platform) {
        super(title, description, releaseYear, genres, ageRestriction, creatorID);
        this.platform = platform;
    }

    public Game(int id, String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID, String platform) {
        super(id, title, description, releaseYear, genres, ageRestriction, creatorID);
        this.platform = platform;
    }

    @Override
    public MediaType getMediaType() {
        return MediaType.GAME;
    }

}
