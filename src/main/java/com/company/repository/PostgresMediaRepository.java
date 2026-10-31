package com.company.repository;

import com.company.models.Game;
import com.company.models.MediaEntrie;
import com.company.models.MediaType;
import com.company.models.Movie;
import com.company.models.Series;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostgresMediaRepository implements MediaRepository {

    public PostgresMediaRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS media_entries (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        type VARCHAR(50) NOT NULL,
                        description TEXT,
                        release_year INT NOT NULL,
                        genres TEXT,
                        age_restriction INT,
                        creator_id INT NOT NULL,
                        episodes INT,
                        seasons INT,
                        platform VARCHAR(255),
                        duration INT
                    );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int createMedia(MediaEntrie media) {
        String sql = """
                    INSERT INTO media_entries (title, type, description, release_year, genres, age_restriction, creator_id, episodes, seasons, platform, duration)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getMediaType() != null ? media.getMediaType().name() : null);
            stmt.setString(3, media.getDescription());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setString(5, media.getGenres() != null ? String.join(",", media.getGenres()) : "");
            stmt.setInt(6, media.getAgeRestriction());
            stmt.setInt(7, media.getCreatorID());

            if (media instanceof Series series) {
                stmt.setInt(8, series.getEpisodesCount());
                stmt.setInt(9, series.getSeasonsCount());
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.INTEGER);
            } else if (media instanceof Game game) {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setString(10, game.getPlatform());
                stmt.setNull(11, Types.INTEGER);
            } else if (media instanceof Movie movie) {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setInt(11, movie.getDurationMinutes());
            } else {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.INTEGER);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                media.setId(generatedId);
                return generatedId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public MediaEntrie getMediaById(int mediaId) {
        String sql = "SELECT * FROM media_entries WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMedia(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<MediaEntrie> getAllMedia() {
        List<MediaEntrie> list = new ArrayList<>();
        String sql = "SELECT * FROM media_entries";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToMedia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public MediaEntrie updateMedia(MediaEntrie media) {
        String sql = """
                    UPDATE media_entries SET
                        title = ?,
                        type = ?,
                        description = ?,
                        release_year = ?,
                        genres = ?,
                        age_restriction = ?,
                        creator_id = ?,
                        episodes = ?,
                        seasons = ?,
                        platform = ?,
                        duration = ?
                    WHERE id = ?;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getMediaType() != null ? media.getMediaType().name() : null);
            stmt.setString(3, media.getDescription());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setString(5, media.getGenres() != null ? String.join(",", media.getGenres()) : "");
            stmt.setInt(6, media.getAgeRestriction());
            stmt.setInt(7, media.getCreatorID());

            if (media instanceof Series series) {
                stmt.setInt(8, series.getEpisodesCount());
                stmt.setInt(9, series.getSeasonsCount());
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.INTEGER);
            } else if (media instanceof Game game) {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setString(10, game.getPlatform());
                stmt.setNull(11, Types.INTEGER);
            } else if (media instanceof Movie movie) {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setInt(11, movie.getDurationMinutes());
            } else {
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.INTEGER);
            }

            stmt.setInt(12, media.getId());
            stmt.executeUpdate();
            return media;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteMedia(int mediaId) {
        String sql = "DELETE FROM media_entries WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mediaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private MediaEntrie mapResultSetToMedia(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String typeStr = rs.getString("type");
        String description = rs.getString("description");
        int releaseYear = rs.getInt("release_year");

        String genresRaw = rs.getString("genres");
        List<String> genres = (genresRaw != null && !genresRaw.isBlank())
                ? Arrays.asList(genresRaw.split(","))
                : new ArrayList<>();

        int ageRestriction = rs.getInt("age_restriction");
        int creatorId = rs.getInt("creator_id");

        MediaType type = typeStr != null ? MediaType.valueOf(typeStr) : null;

        if (type == MediaType.SERIES) {
            int seasons = rs.getInt("seasons");
            int episodes = rs.getInt("episodes");
            return new Series(id, title, description, releaseYear, genres, ageRestriction, creatorId, seasons, episodes);
        } else if (type == MediaType.GAME) {
            String platform = rs.getString("platform");
            return new Game(id, title, description, releaseYear, genres, ageRestriction, creatorId, platform);
        } else if (type == MediaType.MOVIE) {
            int duration = rs.getInt("duration");
            return new Movie(id, title, description, releaseYear, genres, ageRestriction, creatorId, duration);
        }

        return null;
    }
}
