package edu.escuelaing.arep.http;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;

    private HttpRequest(String method, String path, Map<String, String> queryParams) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
    }

    public static HttpRequest fromRawUri(String method, String uri) {
        if (uri == null || uri.isBlank()) {
            return new HttpRequest(method, "/", Collections.emptyMap());
        }

        String[] parts = uri.split("\\?", 2);
        String path = parts[0].isBlank() ? "/" : parts[0];
        Map<String, String> params = parseQuery(parts.length > 1 ? parts[1] : "");
        return new HttpRequest(method, path, params);
    }

    private static Map<String, String> parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, String> values = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.isBlank()) {
                continue;
            }
            String[] kv = pair.split("=", 2);
            String key = decode(kv[0]);
            String value = kv.length == 2 ? decode(kv[1]) : "";
            values.put(key, value);
        }
        return Collections.unmodifiableMap(values);
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}

