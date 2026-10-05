package com.company.repository;

import com.company.models.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvMediaRepository implements MediaRepository {

    private final String databaseLocation;

    public CsvMediaRepository(String databaseLocation) throws IOException {
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

    private String mediaToCsvLine(MediaEntrie media) {
        String genresStr = "";
        if (media.getGenres() != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < media.getGenres().size(); i++) {
                sb.append(media.getGenres().get(i));
                if (i < media.getGenres().size() - 1) {
                    sb.append(";");
                }
            }
            genresStr = sb.toString();
        }

        String type = media.getMediaType().name();
        String title = media.getTitle();
        if (title != null) {
            title = title.replace(",", " ");
        } else {
            title = "";
        }

        String description = media.getDescription();
        if (description != null) {
            description = description.replace(",", " ");
        } else {
            description = "";
        }

        String extra1 = "";
        String extra2 = "";

        if (media instanceof Movie) {
            Movie movie = (Movie) media;
            extra1 = String.valueOf(movie.getDurationMinutes());
        } else if (media instanceof Series) {
            Series series = (Series) media;
            extra1 = String.valueOf(series.getSeasonsCount());
            extra2 = String.valueOf(series.getEpisodesCount());
        } else if (media instanceof Game) {
            Game game = (Game) media;
            if (game.getPlatform() != null) {
                extra1 = game.getPlatform();
            }
        }

        return media.getId() + "," +
                type + "," +
                title + "," +
                description + "," +
                media.getReleaseYear() + "," +
                genresStr + "," +
                media.getAgeRestriction() + "," +
                media.getCreatorID() + "," +
                extra1 + "," +
                extra2 + "\n";
    }

    private MediaEntrie csvToMedia(String line) {
        if (line == null) {
            return null;
        }
        if (line.trim().isEmpty()) {
            return null;
        }

        String[] columns = line.split(",", -1);
        if (columns.length < 10) {
            return null;
        }

        int id = Integer.parseInt(columns[0]);
        MediaType type = MediaType.valueOf(columns[1]);
        String title = columns[2];
        String description = columns[3];
        int releaseYear = Integer.parseInt(columns[4]);

        List<String> genres = new ArrayList<>();
        if (!columns[5].isEmpty()) {
            String[] genreArray = columns[5].split(";");
            for (String g : genreArray) {
                genres.add(g);
            }
        }

        int ageRestriction = Integer.parseInt(columns[6]);
        int creatorId = Integer.parseInt(columns[7]);

        if (type == MediaType.MOVIE) {
            int duration = Integer.parseInt(columns[8]);
            return new Movie(id, title, description, releaseYear, genres, ageRestriction, creatorId, duration);
        }

        if (type == MediaType.SERIES) {
            int seasons = Integer.parseInt(columns[8]);
            int episodes = Integer.parseInt(columns[9]);
            return new Series(id, title, description, releaseYear, genres, ageRestriction, creatorId, seasons, episodes);
        }

        if (type == MediaType.GAME) {
            String platform = columns[8];
            return new Game(id, title, description, releaseYear, genres, ageRestriction, creatorId, platform);
        }

        return null;
    }

    private void flush(List<MediaEntrie> list) {
        StringBuilder sb = new StringBuilder();
        for (MediaEntrie media : list) {
            sb.append(mediaToCsvLine(media));
        }

        try {
            Files.writeString(Path.of(databaseLocation), sb.toString());
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern der Datei", e);
        }
    }

    @Override
    public int createMedia(MediaEntrie media) {
        List<MediaEntrie> list = getAllMedia();

        int maxId = 0;
        for (MediaEntrie m : list) {
            if (m.getId() > maxId) {
                maxId = m.getId();
            }
        }

        int newId = maxId + 1;
        media.setId(newId);
        list.add(media);

        flush(list);
        return newId;
    }

    @Override
    public MediaEntrie getMediaById(int id) {
        List<MediaEntrie> list = getAllMedia();
        for (MediaEntrie media : list) {
            if (media.getId() == id) {
                return media;
            }
        }
        return null;
    }

    @Override
    public List<MediaEntrie> getAllMedia() {
        List<MediaEntrie> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Path.of(databaseLocation));
            for (String line : lines) {
                MediaEntrie media = csvToMedia(line);
                if (media != null) {
                    list.add(media);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Lesen der Datei", e);
        }
        return list;
    }

    @Override
    public MediaEntrie updateMedia(MediaEntrie media) {
        List<MediaEntrie> list = getAllMedia();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == media.getId()) {
                list.set(i, media);
                flush(list);
                return media;
            }
        }
        return null;
    }

    @Override
    public void deleteMedia(int id) {
        List<MediaEntrie> list = getAllMedia();
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
