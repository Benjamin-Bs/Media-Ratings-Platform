package com.company;

import com.company.repository.*;
import com.company.server.RestServer;
import com.company.service.*;
import com.company.service.controller.*;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Automatische Initialisierung der Tabellen
            DatabaseInitializer.initializeSchema();

            // 2. Repositories
            UserRepository userRepository = new PostgresUserRepository();
            MediaRepository mediaRepository = new PostgresMediaRepository();
            RatingRepository ratingRepository = new PostgresRatingRepository();

            // 3. Services
            UserService userService = new UserService(userRepository);
            MediaService mediaService = new MediaService(mediaRepository);
            RatingService ratingService = new RatingService(ratingRepository);

            // 4. Controller
            MediaController mediaController = new MediaController(mediaService, userService);
            UserController userController = new UserController(userService);
            RatingController ratingController = new RatingController(ratingService, userService);

            // 5. Server starten
            RestServer server = new RestServer(8080, mediaController, userController, ratingController);
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
