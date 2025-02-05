package com.raifernando.spotify;

import com.raifernando.util.PropertiesFile;

import java.io.IOException;

public class SignOut {
    public static void main(String [] args) throws IOException {
        signOut();
    }

    /**
     * Removes the current user's credentials, making it necessary to reauthorize the application, allowing a new user to login.
     * @throws IOException
     */
    private static void signOut() throws IOException {
        System.out.println("Signing out!");

        PropertiesFile.setFileName("config.properties");
        PropertiesFile.storeInFile("AUTHORIZATION_CODE", "");
        PropertiesFile.storeInFile("USER_ACCESS_CODE", "");
        PropertiesFile.closeFiles();
    }
}
