package edu.escuelaing.arep.server;

import edu.escuelaing.arep.http.HttpRequest;
import edu.escuelaing.arep.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class SimpleHttpServer {

    private final RouteRegistry routeRegistry;
    private final StaticFileService staticFileService;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public SimpleHttpServer(RouteRegistry routeRegistry, StaticFileService staticFileService) {
        this.routeRegistry = routeRegistry;
        this.staticFileService = staticFileService;
    }

    public void start(int port) {
        running = true;
        try (ServerSocket socket = new ServerSocket(port)) {
            this.serverSocket = socket;
            System.out.println("Server started on port " + port);

            while (running) {
                try (Socket client = socket.accept()) {
                    handleClient(client);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error handling request: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not start server on port " + port, e);
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            return;
        }

        String line;
        while ((line = in.readLine()) != null && !line.isBlank()) {
            // Headers are not required for this lab implementation.
        }

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            writeResponse(out, HttpResponse.internalError("Malformed request line"));
            return;
        }

        String method = parts[0];
        String uri = parts[1];

        if (!"GET".equalsIgnoreCase(method)) {
            writeResponse(out, HttpResponse.methodNotAllowed("Only GET is supported"));
            return;
        }

        HttpRequest request = HttpRequest.fromRawUri(method, uri);
        Optional<RouteHandler> maybeHandler = routeRegistry.find(request);
        if (maybeHandler.isPresent()) {
            writeResponse(out, maybeHandler.get().handle(request));
            return;
        }

        HttpResponse staticResponse = staticFileService.resolve(request.getPath());
        if (staticResponse == null) {
            staticResponse = staticFileService.resolveFromClasspath(request.getPath());
        }
        if (staticResponse != null) {
            writeResponse(out, staticResponse);
            return;
        }

        writeResponse(out, HttpResponse.notFound("Resource not found"));
    }

    private void writeResponse(OutputStream out, HttpResponse response) throws IOException {
        String statusLine = "HTTP/1.1 " + response.getStatusCode() + " " + response.getReasonPhrase() + "\r\n";
        out.write(statusLine.getBytes(StandardCharsets.UTF_8));
        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            String headerLine = header.getKey() + ": " + header.getValue() + "\r\n";
            out.write(headerLine.getBytes(StandardCharsets.UTF_8));
        }
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
        out.write(response.getBody());
        out.flush();
    }
}

