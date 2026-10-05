package com.company.service.controller;

import com.company.models.User;
import com.company.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/users")) {
                    List<User> users = userService.getAllUsers();
                    sendResponse(httpExchange, 200, objectMapper.writeValueAsString(users));
                    return;
                }
            }

            if (method.equals("POST")) {
                if (path.equals("/users/register")) {
                    String requestBody = readRequestBody(httpExchange);
                    User newUser = objectMapper.readValue(requestBody, User.class);

                    User registeredUser = userService.register(newUser.getUsername(), newUser.getPassword());
                    sendResponse(httpExchange, 201, objectMapper.writeValueAsString(registeredUser));
                    return;
                }

                if (path.equals("/users/login")) {
                    String requestBody = readRequestBody(httpExchange);
                    User loginUser = objectMapper.readValue(requestBody, User.class);

                    String token = userService.login(loginUser.getUsername(), loginUser.getPassword());
                    sendResponse(httpExchange, 200, "{\"token\": \"" + token + "\"}");
                    return;
                }
            }

            sendResponse(httpExchange, 404, "{\"error\": \"Not Found\"}");

        } catch (Exception e) {
            sendResponse(httpExchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String readRequestBody(HttpExchange httpExchange) throws IOException {
        InputStream is = httpExchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void sendResponse(HttpExchange httpExchange, int statusCode, String jsonResponse) throws IOException {
        byte[] responseBody = jsonResponse.getBytes(StandardCharsets.UTF_8);

        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(statusCode, responseBody.length);

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(responseBody);
        }
    }
}
