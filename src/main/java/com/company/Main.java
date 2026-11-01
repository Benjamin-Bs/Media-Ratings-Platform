package com.company;

import com.company.repository.*;
import com.company.server.RestServer;
import com.company.service.*;
import com.company.service.controller.*;

import java.util.Scanner;

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

            // 4. Interaktive Konsolen-Eingabe (optional vor dem Server-Start)
            Scanner scanner = new Scanner(System.in);
            boolean runningConsole = true;

            System.out.println("=== Media Ratings Platform ===");

            while (runningConsole) {
                System.out.println("\n[1] Neuen Benutzer registrieren");
                System.out.println("[2] Benutzer anmelden (Login-Test)");
                System.out.println("[3] Konsolen-Menü beenden & REST-Server starten");
                System.out.print("Wähle eine Option (1-3): ");

                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1" -> {
                        System.out.println("\n--- Benutzer Registrierung ---");
                        System.out.print("Benutzername: ");
                        String username = scanner.nextLine();


                        System.out.print("Passwort: ");
                        String password = scanner.nextLine();

                        // Nutzung deines PostgresUserRepository direkt
                        PostgresUserRepository postgresUserRepo = (PostgresUserRepository) userRepository;
                        boolean success = postgresUserRepo.registerUser(username, password);

                        if (success) {
                            System.out.println("-> Benutzer erfolgreich registriert und Passwort mit BCrypt gehasht!");
                        } else {
                            System.out.println("-> Fehler bei der Registrierung (z.B. Benutzername bereits vergeben).");
                        }
                    }
                    case "2" -> {
                        System.out.println("\n--- Benutzer Login ---");
                        System.out.print("Benutzername: ");
                        String username = scanner.nextLine();

                        System.out.print("Passwort: ");
                        String password = scanner.nextLine();

                        PostgresUserRepository postgresUserRepo = (PostgresUserRepository) userRepository;
                        boolean isLoggedIn = postgresUserRepo.loginUser(username, password);

                        if (isLoggedIn) {
                            System.out.println("-> Login erfolgreich! Willkommen zurück, " + username + ".");
                        } else {
                            System.out.println("-> Anmeldedaten ungültig.");
                        }
                    }
                    case "3" -> {
                        System.out.println("Konsolen-Menü beendet.");
                        runningConsole = false;
                    }
                    default -> System.out.println("Ungültige Auswahl. Bitte 1, 2 oder 3 eingeben.");
                }
            }

            // 5. Controller
            MediaController mediaController = new MediaController(mediaService, userService);
            UserController userController = new UserController(userService);
            RatingController ratingController = new RatingController(ratingService, userService);

            // 6. REST-Server starten
            RestServer server = new RestServer(8080, mediaController, userController, ratingController);
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
