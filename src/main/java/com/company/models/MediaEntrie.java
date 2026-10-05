package com.company.models;

import com.company.Interfaces.IIdentifiable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class MediaEntrie implements IIdentifiable {

    @Setter
    private int id;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private int releaseYear;
    @Getter
    @Setter
    private List<String> genres;
    @Getter
    @Setter
    private int ageRestriction;
    @Getter
    @Setter
    private int creatorID;


    public MediaEntrie(String title, String description, int releaseYear, List<String> genre, int ageRestriction, int creatorID) {
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        if (genres != null) {
            this.genres = genres;
        } else {
            this.genres = new ArrayList<>();
        }
        this.ageRestriction = ageRestriction;
        this.creatorID = creatorID;
    }

    public MediaEntrie(int id, String title, String description, int releaseYear, List<String> genres, int ageRestriction, int creatorID) {
        this(title, description, releaseYear, genres, ageRestriction, creatorID);
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public abstract MediaType getMediaType();

}
