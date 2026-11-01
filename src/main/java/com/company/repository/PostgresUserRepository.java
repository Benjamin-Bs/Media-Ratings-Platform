package com.company.repository;

import com.company.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresUserRepository implements UserRepository {

    public PostgresUserRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String createTableSql = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    username VARCHAR(255) NOT NULL UNIQUE,
                    password_hash VARCHAR(255),
                    token VARCHAR(255),
                    fav_media_ids TEXT
                );
            """;

        // 1. Benenne 'password' um zu 'password_hash', falls 'password' noch existiert
        String renameColumnSql = """
                DO $$ 
                BEGIN 
                    IF EXISTS (
                        SELECT 1 FROM information_schema.columns 
                        WHERE table_name='users' AND column_name='password'
                    ) THEN 
                        ALTER TABLE users RENAME COLUMN password TO password_hash; 
                    END IF; 
                END $$;
            """;

        // 2. Stelle sicher, dass alle weiteren Spalten existieren
        String addColumnsSql = """
                ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
                ALTER TABLE users ADD COLUMN IF NOT EXISTS token VARCHAR(255);
                ALTER TABLE users ADD COLUMN IF NOT EXISTS fav_media_ids TEXT;
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSql);
            stmt.execute(renameColumnSql);
            stmt.execute(addColumnsSql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int createUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, token, fav_media_ids) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());

            String passToStore = (user.getPassword() != null && user.getPassword().startsWith("$2a$"))
                    ? user.getPassword()
                    : BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            stmt.setString(2, passToStore);
            stmt.setString(3, user.getToken());
            stmt.setString(4, user.getFavMediaIds() != null ? joinIntegers(user.getFavMediaIds()) : "");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                user.setId(generatedId);
                return generatedId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean registerUser(String username, String rawPassword) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(String username, String rawPassword) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return storedHash != null && BCrypt.checkpw(rawPassword, storedHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUser(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserByToken(String token) {
        String sql = "SELECT * FROM users WHERE token = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, token = ?, fav_media_ids = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getToken());
            stmt.setString(4, user.getFavMediaIds() != null ? joinIntegers(user.getFavMediaIds()) : "");
            stmt.setInt(5, user.getId());

            stmt.executeUpdate();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        String token = rs.getString("token");
        String favMediaRaw = rs.getString("fav_media_ids");

        User user = new User(id, username, passwordHash);
        user.setToken(token);

        if (favMediaRaw != null && !favMediaRaw.isBlank()) {
            List<Integer> favIds = new ArrayList<>();
            for (String s : favMediaRaw.split(",")) {
                try {
                    favIds.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            user.setFavMediaIds(favIds);
        }

        return user;
    }

    private String joinIntegers(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
