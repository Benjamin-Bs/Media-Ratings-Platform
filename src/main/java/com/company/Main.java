package com.company;


import com.company.data.InMemoryUserRepository;
import com.company.data.UserRepository;
import com.company.models.MediaEntrie;
import com.company.models.Movie;
import com.company.models.User;
import com.company.service.UserService;

import java.util.Scanner;

public class Main {
    static void main() {

        Scanner sc = new Scanner(System.in);

        // Die Schichten miteinander Verbinden
        UserRepository userRepository = new InMemoryUserRepository();
        UserService userService = new UserService(userRepository);

        try {
            System.out.println("Registieren");
            System.out.println("Username: ");
            String name = sc.nextLine();
            System.out.println("Password: ");
            String password = sc.nextLine();

            User registerUser = userService.register(name, password);
            System.out.println("Benutzer wurde erstellt");

            System.out.println("ID: " + registerUser.getId() + ", Username: " + registerUser.getUsername());


            System.out.println("Login");

            String token = userService.login(name, password);
            System.out.println("Angemeldet");
            System.out.println("Token: " + token);

            User loggedInUser = userService.getProfilByToken(token);
            System.out.println("Willkommen " + loggedInUser.getUsername());


            MediaEntrie movie = new Movie(1, "La La Land", "Its a movie", 2012, "Musical", 16, 34);


        } catch (Exception e) {
            System.err.println("Der Fehler: " + e.getMessage());
        }

    }
}
