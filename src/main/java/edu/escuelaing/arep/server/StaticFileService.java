package edu.escuelaing.arep.server;

import edu.escuelaing.arep.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileService {

    private String staticRoot = "src/main/resources/public";

    public void setStaticRoot(String staticRoot) {
        this.staticRoot = staticRoot;
    }

    public HttpResponse resolve(String requestPath) {
        String normalized = (requestPath == null || requestPath.equals("/")) ? "/index.html" : requestPath;
        Path path = Paths.get(staticRoot, normalized.startsWith("/") ? normalized.substring(1) : normalized).normalize();

        if (!Files.exists(path) || Files.isDirectory(path)) {
            return null;
        }

        try {
            byte[] data = Files.readAllBytes(path);
            return HttpResponse.ok(contentType(path.toString()), data);
        } catch (IOException e) {
            return HttpResponse.internalError("Error reading static file");
        }
    }

    public HttpResponse resolveFromClasspath(String requestPath) {
        String normalized = (requestPath == null || requestPath.equals("/")) ? "/index.html" : requestPath;
        String resourcePath = "/public" + normalized;
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            byte[] bytes = in.readAllBytes();
            return HttpResponse.ok(contentType(normalized), bytes);
        } catch (IOException e) {
            return HttpResponse.internalError("Error reading resource from classpath");
        }
    }

    private String contentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }
        if (path.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}

