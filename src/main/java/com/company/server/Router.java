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

        try {
            // Leitet alle Anfragen mit /users oder /api/users direkt an den UserController weiter
            if (path.startsWith("/api/users") || path.startsWith("/users")) {
                userController.handle(exchange);
            }
            // Leitet alle Anfragen mit /media oder /api/media direkt an den MediaController weiter
            else if (path.startsWith("/api/media") || path.startsWith("/media")) {
                mediaController.handle(exchange);
            }
            // Leitet alle Anfragen mit /ratings oder /api/ratings direkt an den RatingController weiter
            else if (path.startsWith("/api/ratings") || path.startsWith("/ratings")) {
                ratingController.handle(exchange);
            } else {
                sendResponse(exchange, 404, "{\"error\": \"Route nicht gefunden\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Interner Serverfehler: " + e.getMessage() + "\"}");
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
