package com.raifernando.util;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Manages the {@code .properties} file containing the necessary credentials for this application to function properly.
 */
public class PropertiesFile {
    private final Properties properties = new Properties();
    private final String filename;

    /**
     * Creates a new instance using the default filename {@code config.properties}.
     */
    public PropertiesFile() {
        this("config.properties"); // Default filename
    }

    /**
     * Creates new instance using a custom {@code .properties} filename.
     * @param filename custom filename.
     */
    public PropertiesFile(String filename) {
        this.filename = filename;
        loadProperties();
    }

    /**
     * Loads the content from the properties file.
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
     * Retrieves the property value associated with the key in the properties file.
     * @param key key for the property
     * @return the value, or {@code null} if not found
     */
    @Nullable
    public String get(String key) {
        String property = properties.getProperty(key);
        return (property == null || property.isEmpty() ? null : property);
    }

    /**
     * Stores the value in the specified key
     * @param key key to store the value
     * @param value value to be stored
     */
    public void store(String key, String value) {
        store(Map.of(key, value));
    }

    /**
     * Stores the values of the specified keys from the {@link Map}.
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
