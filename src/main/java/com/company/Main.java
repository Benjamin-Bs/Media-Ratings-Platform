package com.company;


import com.company.repository.InMemoryUserRepository;
import com.company.repository.UserRepository;
import com.company.models.MediaEntrie;
import com.company.models.Movie;
import com.company.models.User;
import com.company.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Main {
    static void main() throws IOException {

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

        User us = new User("Bib","passwd");

        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(us);
        System.out.println(json);
        //om.readValue(json, User.class);



        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080),10);

        httpServer.createContext("/users", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {

            }
        });

        httpServer.start();



    }

}
