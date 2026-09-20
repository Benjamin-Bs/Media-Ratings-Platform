package com.company.models;

import com.company.Interfaces.IIdentifiable;
import lombok.Getter;
import lombok.Setter;

public abstract class MediaEntrie implements IIdentifiable {

    @Setter
    private int id;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String description;

    //MediaType
    @Getter
    @Setter
    private int releaseYear;
    @Getter
    @Setter
    private String genre;
    @Getter
    @Setter
    private int ageRestriction;
    @Getter
    @Setter
    private int creatorID;


    public MediaEntrie(int id, String title, String description, int releaseYear, String genre, int ageRestriction, int creatorID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.ageRestriction = ageRestriction;
        this.creatorID = creatorID;
    }


    @Override
    public int getId(){
        return id;
    }

    public abstract MediaType getMediaType();

}
