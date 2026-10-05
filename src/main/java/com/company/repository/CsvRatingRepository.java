package com.company.repository;

import com.company.models.Rating;
import com.company.models.RatingStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CsvRatingRepository implements RatingRepository {

    private final String databaseLocation;

    public CsvRatingRepository(String databaseLocation) throws IOException {
        this.databaseLocation = databaseLocation;
        File file = new File(databaseLocation);

        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null) {
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }
            }
            file.createNewFile();
        }
    }

    private String ratingToCsvLine(Rating rating) {
        String likesStr = "";
        if (rating.getLikedUserId() != null) {
            if (!rating.getLikedUserId().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < rating.getLikedUserId().size(); i++) {
                    sb.append(rating.getLikedUserId().get(i));
                    if (i < rating.getLikedUserId().size() - 1) {
                        sb.append(";");
                    }
                }
                likesStr = sb.toString();
            }
        }

        String comment = rating.getComment();
        if (comment != null) {
            comment = comment.replace(",", " ");
        } else {
            comment = "";
        }

        return rating.getId() + "," +
                rating.getUserId() + "," +
                rating.getMediaEntryId() + "," +
                rating.getStarValue() + "," +
                comment + "," +
                rating.getRatingStatus().name() + "," +
                rating.getTimeStamp().toString() + "," +
                likesStr + "\n";
    }

    private Rating csvToRating(String line) {
        if (line == null) {
            return null;
        }
        if (line.trim().isEmpty()) {
            return null;
        }

        String[] columns = line.split(",", -1);
        if (columns.length < 8) {
            return null;
        }

        int id = Integer.parseInt(columns[0]);
        int userId = Integer.parseInt(columns[1]);
        int mediaEntryId = Integer.parseInt(columns[2]);
        int starValue = Integer.parseInt(columns[3]);
        String comment = columns[4];

        // Nutzung des Konstruktors mit ID für bestehende Datensätze
        Rating rating = new Rating(id, userId, mediaEntryId, starValue, comment);
        rating.setRatingStatus(RatingStatus.valueOf(columns[5]));
        rating.setTimeStamp(Instant.parse(columns[6]));

        if (!columns[7].isEmpty()) {
            String[] likesArray = columns[7].split(";");
            List<Integer> likesList = new ArrayList<>();
            for (String likeIdStr : likesArray) {
                likesList.add(Integer.parseInt(likeIdStr));
            }
            rating.setLikedUserId(likesList);
        }

        return rating;
    }

    private void flush(List<Rating> ratings) {
        StringBuilder sb = new StringBuilder();
        for (Rating rating : ratings) {
            sb.append(ratingToCsvLine(rating));
        }

        try {
            Files.writeString(Path.of(databaseLocation), sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern der Datei", e);
        }
    }

    public List<Rating> findAll() {
        List<Rating> ratings = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Path.of(databaseLocation));
            for (String line : lines) {
                Rating rating = csvToRating(line);
                if (rating != null) {
                    ratings.add(rating);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Lesen der Datei", e);
        }
        return ratings;
    }

    @Override
    public int createRating(Rating rating) {
        List<Rating> list = findAll();

        int maxId = 0;
        for (Rating r : list) {
            if (r.getId() > maxId) {
                maxId = r.getId();
            }
        }

        int newId = maxId + 1;
        rating.setId(newId);
        list.add(rating);

        flush(list);
        return newId;
    }

    @Override
    public Rating getRatingById(int id) {
        List<Rating> list = findAll();
        for (Rating rating : list) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        return null;
    }

    @Override
    public List<Rating> getRatingsByMediaId(int mediaId) {
        List<Rating> list = findAll();
        List<Rating> result = new ArrayList<>();

        for (Rating rating : list) {
            if (rating.getMediaEntryId() == mediaId) {
                result.add(rating);
            }
        }

        return result;
    }

    @Override
    public List<Rating> getRatingsByUserId(int userId) {
        List<Rating> list = findAll();
        List<Rating> result = new ArrayList<>();

        for (Rating rating : list) {
            if (rating.getUserId() == userId) {
                result.add(rating);
            }
        }

        return result;
    }

    @Override
    public Rating updateRating(Rating rating) {
        List<Rating> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == rating.getId()) {
                list.set(i, rating);
                flush(list);
                return rating;
            }
        }
        return null;
    }

    @Override
    public void deleteRating(int id) {
        List<Rating> list = findAll();
        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                list.remove(i);
                found = true;
                break;
            }
        }

        if (found) {
            flush(list);
        }
    }
}
