package com.company.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeSchema() {
        String createUsers = """
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        token VARCHAR(255)
                    );
                """;

        String createMedia = """
                    CREATE TABLE IF NOT EXISTS media_entries (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        type VARCHAR(50) NOT NULL,
                        release_year INT NOT NULL,
                        creator VARCHAR(255),
                        episodes INT,
                        seasons INT,
                        platform VARCHAR(255),
                        duration INT
                    );
                """;

        String createRatings = """
                    CREATE TABLE IF NOT EXISTS ratings (
                        id SERIAL PRIMARY KEY,
                        media_id INT NOT NULL,
                        user_id INT NOT NULL,
                        score INT NOT NULL,
                        comment TEXT,
                        status VARCHAR(50) NOT NULL,
                        likes INT DEFAULT 0,
                        CONSTRAINT fk_rating_media FOREIGN KEY (media_id) REFERENCES media_entries(id) ON DELETE CASCADE,
                        CONSTRAINT fk_rating_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsers);
            stmt.execute(createMedia);
            stmt.execute(createRatings);

            System.out.println("Datenbankschema erfolgreich initialisiert.");

        } catch (SQLException e) {
            System.err.println("Fehler bei der Initialisierung der Datenbank: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

