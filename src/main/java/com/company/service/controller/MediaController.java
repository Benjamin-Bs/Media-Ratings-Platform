package com.company.service.controller;

import com.company.models.*;
import com.company.service.MediaService;
import com.company.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MediaController implements HttpHandler {

    private final MediaService mediaService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public MediaController(MediaService mediaService, UserService userService) {
        this.mediaService = mediaService;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                if ("/media".equals(path) || "/media/".equals(path)) {
                    List<MediaEntrie> mediaList = mediaService.getAllMedia();
                    sendResponse(httpExchange, 200, objectMapper.writeValueAsString(mediaList));
                    return;
                }
            }

            if ("POST".equalsIgnoreCase(method)) {
                if ("/media".equals(path) || "/media/".equals(path)) {
                    User user = getAuthenticatedUser(httpExchange);

                    String requestBody = readRequestBody(httpExchange);
                    Movie movie = objectMapper.readValue(requestBody, Movie.class);

                    MediaEntrie createdMedia = mediaService.createMedia(movie, user);
                    sendResponse(httpExchange, 201, objectMapper.writeValueAsString(createdMedia));
                    return;
                }
            }

            if ("DELETE".equalsIgnoreCase(method)) {
                if (path.startsWith("/media/")) {
                    User user = getAuthenticatedUser(httpExchange);

                    String idStr = path.substring("/media/".length());
                    int mediaId = Integer.parseInt(idStr);

                    mediaService.deleteMedia(mediaId, user);
                    sendResponse(httpExchange, 200, "{\"message\": \"Media deleted successfully\"}");
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
