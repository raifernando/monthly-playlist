package com.raifernando.util;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class QueryGenerator {
    public static String generateQueryString(Map<String, String> parameters) {
        try {
            URIBuilder uriBuilder = new URIBuilder("");

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }

            URI uri = uriBuilder.build();

            return uri.getQuery();
        } catch (URISyntaxException e) {
            return "";
        }
    }
}
