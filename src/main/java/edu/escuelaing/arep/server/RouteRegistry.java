package edu.escuelaing.arep.server;

import edu.escuelaing.arep.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RouteRegistry {

    private final Map<String, RouteHandler> getRoutes = new HashMap<>();

    public void registerGet(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    public Optional<RouteHandler> find(HttpRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return Optional.empty();
        }
        return Optional.ofNullable(getRoutes.get(request.getPath()));
    }

    public int countGetRoutes() {
        return getRoutes.size();
    }
}

