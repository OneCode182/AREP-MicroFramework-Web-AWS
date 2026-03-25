package edu.escuelaing.arep;

import edu.escuelaing.arep.ioc.MicroApplicationContext;
import edu.escuelaing.arep.server.SimpleHttpServer;
import edu.escuelaing.arep.server.StaticFileService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleHttpServerIntegrationTest {

    private static SimpleHttpServer server;
    private static Thread serverThread;
    private static final int PORT = 18080;

    @BeforeAll
    static void setUp() throws InterruptedException {
        MicroApplicationContext context = new MicroApplicationContext();
        context.loadFromPackage("edu.escuelaing.arep.app.controllers");

        StaticFileService staticFileService = new StaticFileService();
        server = new SimpleHttpServer(context.routeRegistry(), staticFileService);

        serverThread = new Thread(() -> server.start(PORT));
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(1200);
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void shouldServeGreetingEndpointWithQueryParam() throws Exception {
        HttpURLConnection conn = openGet("/greeting?name=MIT");
        assertEquals(200, conn.getResponseCode());
        String body = readBody(conn);
        assertEquals("Hola MIT", body.trim());
        conn.disconnect();
    }

    @Test
    void shouldServeGreetingEndpointWithDefaultValue() throws Exception {
        HttpURLConnection conn = openGet("/greeting");
        assertEquals(200, conn.getResponseCode());
        String body = readBody(conn);
        assertEquals("Hola World", body.trim());
        conn.disconnect();
    }

    @Test
    void shouldServeStaticIndexHtml() throws Exception {
        HttpURLConnection conn = openGet("/index.html");
        assertEquals(200, conn.getResponseCode());
        String body = readBody(conn);
        assertTrue(body.contains("AREP IoC Reflection Server"));
        conn.disconnect();
    }

    private static HttpURLConnection openGet(String path) throws Exception {
        URL url = new URL("http://localhost:" + PORT + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        return connection;
    }

    private static String readBody(HttpURLConnection connection) throws Exception {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                body.append(line).append('\n');
            }
            return body.toString();
        }
    }
}

