package com.raifernando.spotify;

import com.raifernando.util.PropertiesFile;

import java.util.Map;

public class SignOut {
    public static void main(String [] args) {
        signOut();
    }

    /**
     * Removes the current user's credentials, making it necessary to reauthorize the application, allowing a new user to login.
     */
    private static void signOut() {
        System.out.println("Signing out!");

        PropertiesFile propertiesFile = new PropertiesFile();
        propertiesFile.store(Map.of(
                "AUTHORIZATION_CODE", "",
                "USER_ACCESS_CODE", ""
        ));
    }
}
