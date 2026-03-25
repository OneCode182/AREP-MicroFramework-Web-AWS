package edu.escuelaing.arep;

import edu.escuelaing.arep.http.HttpResponse;
import edu.escuelaing.arep.server.StaticFileService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaticFileServiceTest {

    @Test
    void shouldServeHtmlFromMainResourcesFolder() {
        StaticFileService service = new StaticFileService();
        HttpResponse response = service.resolve("/index.html");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getContentType().startsWith("text/html"));
        String body = new String(response.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("AREP IoC Reflection Server"));
    }

    @Test
    void shouldServePngFile() {
        StaticFileService service = new StaticFileService();
        HttpResponse response = service.resolve("/images/sample.png");

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("image/png", response.getContentType());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void shouldReturnNullWhenStaticFileDoesNotExist() {
        StaticFileService service = new StaticFileService();
        HttpResponse response = service.resolve("/does-not-exist.txt");
        assertNull(response);
    }
}

