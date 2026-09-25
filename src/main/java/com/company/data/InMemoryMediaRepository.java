package com.company.data;

import com.company.models.MediaEntrie;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

public class InMemoryMediaRepository implements MediaRepository {

    private static int nextId = 1;
    private final List<MediaEntrie> mediaList = new ArrayList<>();

    @Override
    public int createMedia(MediaEntrie media) {
        media.setId(nextId++);
        mediaList.add(media);
        return media.getId();
    }

    @Override
    public MediaEntrie getMediaById(int mediaId) {
        for (MediaEntrie media : mediaList) {
            if (media.getId() == mediaId) {
                return media;
            }
        }
        return null;
    }

    @Override
    public List<MediaEntrie> getAllMedia() {
        return new ArrayList<>(mediaList);
    }

    @Override
    public MediaEntrie updateMedia(MediaEntrie media) {
        for (int i = 0; i < mediaList.size(); i++) {
            if (mediaList.get(i).getId() == media.getId()) {
                mediaList.set(i, media);
                return media;
            }
        }
        return null;
    }

    @Override
    public void deleteMedia(int mediaId) {
        for (int i = 0; i < mediaList.size(); i++) {
            if (mediaList.get(i).getId() == mediaId) {
                mediaList.remove(i);
                break;
            }
        }
    }
}
