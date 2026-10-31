package com.company.repository;

import com.company.models.Rating;
import com.company.models.RatingStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresRatingRepository implements RatingRepository {

    public PostgresRatingRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS ratings (
                        id SERIAL PRIMARY KEY,
                        media_id INT NOT NULL,
                        user_id INT NOT NULL,
                        score INT NOT NULL,
                        comment TEXT,
                        status VARCHAR(50) NOT NULL,
                        likes INT DEFAULT 0
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
    public int createRating(Rating rating) {
        String sql = """
                    INSERT INTO ratings (media_id, user_id, score, comment, status, likes)
                    VALUES (?, ?, ?, ?, ?, ?) RETURNING id;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating.getMediaEntryId());
            stmt.setInt(2, rating.getUserId());
            stmt.setInt(3, rating.getStarValue());
            stmt.setString(4, rating.getComment());
            stmt.setString(5, rating.getRatingStatus() != null ? rating.getRatingStatus().name() : RatingStatus.HIDDEN.name());
            stmt.setInt(6, rating.getLikeCount());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                rating.setId(generatedId);
                return generatedId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Rating getRatingById(int ratingId) {
        String sql = "SELECT * FROM ratings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRating(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Rating> getRatingsByMediaId(int mediaId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE media_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mediaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Rating> getRatingsByUserId(int userId) {
        List<Rating> list = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Rating updateRating(Rating rating) {
        String sql = """
                    UPDATE ratings SET
                        media_id = ?,
                        user_id = ?,
                        score = ?,
                        comment = ?,
                        status = ?,
                        likes = ?
                    WHERE id = ?;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, rating.getMediaEntryId());
            stmt.setInt(2, rating.getUserId());
            stmt.setInt(3, rating.getStarValue());
            stmt.setString(4, rating.getComment());
            stmt.setString(5, rating.getRatingStatus() != null ? rating.getRatingStatus().name() : RatingStatus.HIDDEN.name());
            stmt.setInt(6, rating.getLikeCount());
            stmt.setInt(7, rating.getId());

            stmt.executeUpdate();
            return rating;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteRating(int ratingId) {
        String sql = "DELETE FROM ratings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int mediaId = rs.getInt("media_id");
        int userId = rs.getInt("user_id");
        int score = rs.getInt("score");
        String comment = rs.getString("comment");

        Rating rating = new Rating(id, userId, mediaId, score, comment);

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            rating.setRatingStatus(RatingStatus.valueOf(statusStr));
        }

        return rating;
    }
}
