package com.raifernando.spotify;

import com.raifernando.util.PropertiesFile;

import java.util.Map;

/**
 * This class is used to easily sign out the current user by removing the
 * credentials associated with the authenticated user in the {@link PropertiesFile}.
 */
public class SignOut {
    public static void main(String [] args) {
        signOut();
    }

    /**
     * Removes the current user's credentials, making it necessary to reauthorize the application,
     * allowing a new user to log in.
     */
    private static void signOut() {
        System.out.println("Signing out!");

        PropertiesFile propertiesFile = new PropertiesFile();
        propertiesFile.store(Map.of(
                "AUTHORIZATION_CODE", "",
                "AUTHORIZATION_CODE_TIME", "",
                "REFRESH_TOKEN", "",
                "USER_ACCESS_CODE", "",
                "USER_ACCESS_CODE_TIME", ""
        ));
    }
}
