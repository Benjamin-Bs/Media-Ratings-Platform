package com.company.models;

import com.company.Interfaces.IIdentifiable;
import com.company.Interfaces.ILikeable;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Rating implements IIdentifiable, ILikeable {

    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private int userId;
    @Getter
    @Setter
    private int mediaEntryId;

    @Getter
    @Setter
    private int starValue;
    @Getter
    @Setter
    private String comment;
    @Getter
    @Setter
    private Instant timeStamp;

    private RatingStatus ratingStatus;

    private List<Integer> likedUserId;

    public Rating(int id, int userId, int mediaEntryId, int starValue, String comment) {
        this.id = id;
        this.userId = userId;
        this.mediaEntryId = mediaEntryId;
        this.starValue = starValue;
        this.comment = comment;
        this.ratingStatus = RatingStatus.HIDDEN;
        this.likedUserId = new ArrayList<>();
        this.timeStamp = Instant.now();
    }

    @Override
    public int getId() {
        return id;
    }


    public boolean isConfirmed() {
        return this.ratingStatus == RatingStatus.CONFIRMED;
    }

    @Override
    public int getLikeCount(){
        return likedUserId.size();
    }

}
