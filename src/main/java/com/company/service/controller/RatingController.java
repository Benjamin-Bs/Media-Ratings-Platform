package com.company.service.controller;

import com.company.models.Rating;
import com.company.models.User;
import com.company.service.RatingService;
import com.company.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public RatingController(RatingService ratingService, UserService userService) {
        this.ratingService = ratingService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        try {
            if (method.equals("POST")) {
                if (path.equals("/ratings")) {
                    User user = getAuthenticatedUser(httpExchange);

                    String requestBody = readRequestBody(httpExchange);
                    Rating ratingInput = objectMapper.readValue(requestBody, Rating.class);

                    Rating createdRating = ratingService.addRating(
                            user,
                            ratingInput.getMediaEntryId(),
                            ratingInput.getStarValue(),
                            ratingInput.getComment()
                    );

                    sendResponse(httpExchange, 201, objectMapper.writeValueAsString(createdRating));
                    return;
                }

                if (path.startsWith("/ratings/") && path.endsWith("/like")) {
                    User user = getAuthenticatedUser(httpExchange);

                    // Extrahiert die ID aus /ratings/{id}/like
                    String[] parts = path.split("/");
                    int ratingId = Integer.parseInt(parts[2]);

                    ratingService.likeRating(ratingId, user);
                    sendResponse(httpExchange, 200, "{\"message\": \"Rating liked successfully\"}");
                    return;
                }
            }

            sendResponse(httpExchange, 404, "{\"error\": \"Not Found\"}");

        } catch (Exception e) {
            sendResponse(httpExchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private User getAuthenticatedUser(HttpExchange httpExchange) throws Exception {
        String authHeader = httpExchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null) {
            throw new Exception("Missing Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        return userService.getProfilByToken(token);
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
