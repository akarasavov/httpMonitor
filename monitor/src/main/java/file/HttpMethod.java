package file;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, HttpMethod> mappings = new HashMap<>(16);

    static {
        for (HttpMethod httpMethod : values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }


    public static HttpMethod resolve(String method) {
        if (method != null && mappings.containsKey(method)) {
            return mappings.get(method);
        }
        throw new IllegalArgumentException(method + " is not http method");
    }
}

