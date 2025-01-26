package com.raifernando.lastfm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LastfmCredentials {
    public static String api_key;

    public static void loadKeys() throws IOException {
        Properties properties = new Properties();
        FileInputStream file = new FileInputStream("config.properties");
        properties.load(file);

        api_key = properties.getProperty("API_KEY");
    }
}
