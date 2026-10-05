package com.company.service;

import com.company.repository.MediaRepository;
import com.company.models.MediaEntrie;
import com.company.models.User;

import java.util.List;

public class MediaService {

    MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public MediaEntrie createMedia(MediaEntrie media, User creator) {
        media.setCreatorID(creator.getId());
        int createdId = mediaRepository.createMedia(media);
        return this.mediaRepository.getMediaById(createdId);
    }

    public void deleteMedia(int mediaId, User requestor) throws Exception {
        MediaEntrie media = mediaRepository.getMediaById(mediaId);
        if (media == null) {
            throw new Exception("Media Not Found");
        }

        if (media.getCreatorID() != requestor.getId()) {
            throw new Exception("Creator ID Not Match");
        }

        mediaRepository.deleteMedia(mediaId);
    }

    public List<MediaEntrie> getAllMedia() {
        return this.mediaRepository.getAllMedia();
    }

}
