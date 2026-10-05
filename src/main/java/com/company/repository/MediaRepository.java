package com.company.repository;

import com.company.models.MediaEntrie;

import java.util.List;

public interface MediaRepository {

    int createMedia(MediaEntrie media);

    MediaEntrie getMediaById(int mediaId);

    List<MediaEntrie> getAllMedia();

    MediaEntrie updateMedia(MediaEntrie media);

    void deleteMedia(int mediaId);

}
