package com.raifernando.util;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Manage the {@code .properties} file with the necessary credentials for this app to work properly.
 */
public class PropertiesFile {
    private final Properties properties = new Properties();
    private final String filename;

    /**
     * Create a new instance using the default file {@code (config.properties)}.
     */
    public PropertiesFile() {
        this("config.properties"); // Default filename
    }

    /**
     * Create new instance using a custom {@code .properties} file.
     * If changed from the default file {@code (config.properties)},
     * it is necessary to update the Makefile for this project to build correctly.
     * @param filename custom filename.
     */
    public PropertiesFile(String filename) {
        this.filename = filename;
        loadProperties();
    }

    /**
     * Load the content from the property file.
     */
    private void loadProperties() {
        try (FileInputStream fileInput = new FileInputStream(filename)) {
            properties.load(fileInput);
        } catch (FileNotFoundException e) {
            System.out.println(filename + " not found.");
        } catch (IOException e) {
            System.out.println("Error loading " + filename);
        }
    }

    /**
     * Get the property value from the key in the properties file.
     * @param key key for the property
     * @return the value, or null if not found
     */
    public String get(String key) {
        String property = properties.getProperty(key);
        return (property == null || property.isEmpty() ? null : property);
    }

    /**
     * Store the value in the specified key
     * @param key key to store the value
     * @param value value to be stored
     */
    public void store(String key, String value) {
        store(Map.of(key, value));
    }

    /**
     * Store the values of the specified keys in the {@link Map}.
     * @param listOfProperties map with the list of key-values pairs.
     */
    public void store(Map<String, String> listOfProperties) {
        for (Map.Entry<String, String> entry : listOfProperties.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        try (FileOutputStream fileOutput = new FileOutputStream(filename)){
            properties.store(fileOutput, null);
        } catch (FileNotFoundException e) {
            System.out.println(filename + " not found.");
        } catch (IOException e) {
            System.out.println("Error writing in " + filename);
        }
    }

    /**
     * @return the properties' filename.
     */
    public String getFileName() {
        return filename;
    }
}
