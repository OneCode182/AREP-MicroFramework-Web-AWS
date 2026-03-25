package edu.escuelaing.arep.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;
    private final String reasonPhrase;
    private final String contentType;
    private final byte[] body;
    private final Map<String, String> headers;

    private HttpResponse(int statusCode, String reasonPhrase, String contentType, byte[] body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    public static HttpResponse ok(String contentType, byte[] body) {
        return of(200, "OK", contentType, body);
    }

    public static HttpResponse notFound(String message) {
        return of(404, "Not Found", "text/plain; charset=UTF-8", message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public static HttpResponse methodNotAllowed(String message) {
        return of(405, "Method Not Allowed", "text/plain; charset=UTF-8", message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public static HttpResponse internalError(String message) {
        return of(500, "Internal Server Error", "text/plain; charset=UTF-8", message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public static HttpResponse of(int statusCode, String reasonPhrase, String contentType, byte[] body) {
        Map<String, String> responseHeaders = new LinkedHashMap<>();
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", String.valueOf(body.length));
        responseHeaders.put("Connection", "close");
        return new HttpResponse(statusCode, reasonPhrase, contentType, body, responseHeaders);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

