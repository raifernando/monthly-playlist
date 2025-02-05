package com.raifernando.util;

import java.io.*;
import java.util.Properties;

public class PropertiesFile {
    public static Properties properties = new Properties();
    private static FileInputStream fileInput = null;
    private static FileOutputStream fileOutput = null;
    private static String FILE_NAME;

    public PropertiesFile(String fileName) {
        FILE_NAME = fileName;
    }

    public static void setFileName(String fileName) {
        FILE_NAME = fileName;
    }

    private static void loadProperties() {
        if (fileInput == null) {
            try {
                fileInput = new FileInputStream(FILE_NAME);
            } catch (FileNotFoundException e) {
                System.out.println(FILE_NAME + "not found.");
                return;
            }

            try {
                properties.load(fileInput);
            } catch (IOException e) {
                System.out.println("Error loading file");
            }
        }
    }

    public static String getFromFile(String key) {
        loadProperties();

        String property = properties.getProperty(key);
        return (property.isEmpty() ? null : property);
    }

    public static void storeInFile(String key, String value) throws IOException {
        loadProperties();

        if (fileOutput == null) {
            try {
                fileOutput = new FileOutputStream(FILE_NAME);
            } catch (FileNotFoundException e) {
                System.out.println(FILE_NAME + "not found.");
                return;
            }
        }

        properties.setProperty(key, value);
        try {
            properties.store(fileOutput, null);
        } catch (IOException e) {
            System.out.println("Error writing in file");
        }
        PropertiesFile.closeFiles();
    }

    public static void closeFiles() throws IOException {
        if (fileInput != null) {
            fileInput.close();
            fileInput = null;
        }

        if (fileOutput != null) {
            fileOutput.close();
            fileOutput = null;
        }
    }

    public static String getFileName() {
        return FILE_NAME;
    }
}
