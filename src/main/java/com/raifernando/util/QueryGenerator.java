package com.raifernando.util;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This class is used to generate URL queries.
 */
public class QueryGenerator {
    /**
     * Generates a query from the list of key-value pairs from the {@link Map}
     * @param parameters a map with the pairs
     * @return an {@link String} with the query
     */
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
