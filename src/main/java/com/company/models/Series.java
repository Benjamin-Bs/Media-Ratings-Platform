package com.company.models;

public class Series extends MediaEntrie {

    public Series(int id, String title, String description, int releaseYear, String genre, int ageRestriction, int creatorID) {
        super(id, title, description, releaseYear, genre, ageRestriction, creatorID);
    }

    @Override
    public MediaType getMediaType(){
        return MediaType.SERIES;
    }

}
