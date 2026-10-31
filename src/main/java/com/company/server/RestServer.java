package com.company.server;

import com.company.service.controller.MediaController;
import com.company.service.controller.RatingController;
import com.company.service.controller.UserController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class RestServer {

    private final int port;
    private final HttpServer server;

    public RestServer(int port, MediaController mediaController, UserController userController, RatingController ratingController) throws IOException {
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        Router router = new Router(mediaController, userController, ratingController);
        this.server.createContext("/", router);

        this.server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        server.start();
        System.out.println("REST-Server gestartet auf Port " + port);
    }

    public void stop() {
        server.stop(0);
        System.out.println("REST-Server gestoppt.");
    }
}
