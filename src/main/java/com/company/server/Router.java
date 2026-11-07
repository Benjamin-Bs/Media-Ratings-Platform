package com.company.server;

import com.company.service.controller.MediaController;
import com.company.service.controller.RatingController;
import com.company.service.controller.UserController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Router implements HttpHandler {

    private final MediaController mediaController;
    private final UserController userController;
    private final RatingController ratingController;

    public Router(MediaController mediaController, UserController userController, RatingController ratingController) {
        this.mediaController = mediaController;
        this.userController = userController;
        this.ratingController = ratingController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Pfad normalisieren (z.B. Trailing Slashes entfernen)
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }

        try {
            if (path.startsWith("/api/media")) {
                handleMediaRoutes(exchange, path, method);
            } else if (path.startsWith("/api/users")) {
                handleUserRoutes(exchange, path, method);
            } else if (path.startsWith("/api/ratings")) {
                handleRatingRoutes(exchange, path, method);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Route nicht gefunden: " + path + "\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Interner Serverfehler: " + e.getMessage() + "\"}");
        }
    }

    private void handleMediaRoutes(HttpExchange exchange, String path, String method) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            sendResponse(exchange, 200, "{\"message\": \"Media Liste\"}");
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            sendResponse(exchange, 201, "{\"message\": \"Media erstellt\", \"data\": " + (body.isEmpty() ? "{}" : body) + "}");
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Methode nicht erlaubt\"}");
        }
    }

    private void handleUserRoutes(HttpExchange exchange, String path, String method) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            sendResponse(exchange, 200, "{\"message\": \"User Liste\"}");
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            sendResponse(exchange, 201, "{\"message\": \"User erstellt\"}");
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Methode nicht erlaubt\"}");
        }
    }

    private void handleRatingRoutes(HttpExchange exchange, String path, String method) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            sendResponse(exchange, 200, "{\"message\": \"Rating Liste\"}");
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            sendResponse(exchange, 201, "{\"message\": \"Rating abgegeben\"}");
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Methode nicht erlaubt\"}");
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
