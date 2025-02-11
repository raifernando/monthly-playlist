package com.raifernando.localserver;

import com.raifernando.spotify.OAuth;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.net.URI;
import java.rmi.ServerException;

public class AuthCodeReceiver {
    private static HttpServer server;

    public static void startServer() throws ServerException {
        System.out.println("Starting local server for Spotify Authentication.");
        try {
            server = HttpServer.create(new java.net.InetSocketAddress(8080), 0);
            server.createContext("/callback", new CallbackHandler());
            server.start();
        } catch (Exception e) {
            server.stop(1);
            throw new ServerException("Failed to start server.");
        }
    }

    public static void stopServer() {
        System.out.println("Stopping server.");
        server.stop(1);
    }
}

class CallbackHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws java.io.IOException {
        // Get the query string from the URL
        URI uri = exchange.getRequestURI();
        String query = uri.getQuery();

        // Extract the authorization code
        String code = query.split("=")[1];  // Code is the second part of "code=AUTH_CODE"
        code = code.substring(0, code.length() - 6); // Remove "&state" part

        OAuth.latch.countDown();
        OAuth.authCode = code;

        // Send a response back to the browser
        String response = "Authorization code received! You may close this page.";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}

