package edu.escuelaing.arep;

import edu.escuelaing.arep.http.HttpRequest;
import edu.escuelaing.arep.http.HttpResponse;
import edu.escuelaing.arep.ioc.MicroApplicationContext;
import edu.escuelaing.arep.server.RouteHandler;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MicroApplicationContextTest {

    @Test
    void shouldLoadControllersFromPackageAndRegisterGetMappings() {
        MicroApplicationContext context = new MicroApplicationContext();
        context.loadFromPackage("edu.escuelaing.arep.app.controllers");

        assertTrue(context.totalRegisteredGetRoutes() >= 2);
        assertTrue(context.beanContainer().containsBean(edu.escuelaing.arep.app.controllers.GreetingController.class));
    }

    @Test
    void shouldResolveRequestParamWithDefaultValue() {
        MicroApplicationContext context = new MicroApplicationContext();
        context.loadFromPackage("edu.escuelaing.arep.app.controllers");

        HttpRequest request = HttpRequest.fromRawUri("GET", "/greeting");
        Optional<RouteHandler> route = context.routeRegistry().find(request);
        assertTrue(route.isPresent());

        HttpResponse response = route.get().handle(request);
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertEquals("Hola World", body);
    }

    @Test
    void shouldResolveRequestParamFromQueryString() {
        MicroApplicationContext context = new MicroApplicationContext();
        context.loadFromPackage("edu.escuelaing.arep.app.controllers");

        HttpRequest request = HttpRequest.fromRawUri("GET", "/greeting?name=Ana");
        Optional<RouteHandler> route = context.routeRegistry().find(request);
        assertTrue(route.isPresent());

        HttpResponse response = route.get().handle(request);
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertEquals("Hola Ana", body);
    }
}
