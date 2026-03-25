package edu.escuelaing.arep;

import edu.escuelaing.arep.http.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpRequestTest {

    @Test
    void shouldParsePathAndQueryParams() {
        HttpRequest req = HttpRequest.fromRawUri("GET", "/greeting?name=Pedro&age=22");
        assertEquals("/greeting", req.getPath());
        assertEquals("Pedro", req.getQueryParam("name"));
        assertEquals("22", req.getQueryParam("age"));
    }

    @Test
    void shouldHandleMissingParams() {
        HttpRequest req = HttpRequest.fromRawUri("GET", "/health");
        assertEquals("/health", req.getPath());
        assertNull(req.getQueryParam("name"));
    }

    @Test
    void shouldDecodeUrlEncodedValues() {
        HttpRequest req = HttpRequest.fromRawUri("GET", "/greeting?name=Juan%20Pablo");
        assertEquals("Juan Pablo", req.getQueryParam("name"));
    }
}

