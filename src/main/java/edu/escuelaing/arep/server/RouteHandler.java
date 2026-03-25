package edu.escuelaing.arep.server;

import edu.escuelaing.arep.http.HttpRequest;
import edu.escuelaing.arep.http.HttpResponse;

@FunctionalInterface
public interface RouteHandler {
    HttpResponse handle(HttpRequest request);
}

