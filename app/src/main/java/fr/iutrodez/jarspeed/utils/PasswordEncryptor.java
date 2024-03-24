package fr.iutrodez.jarspeed.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for encrypting passwords.
 */
public class PasswordEncryptor {

    /**
     * Encrypts a password using the SHA-256 hashing algorithm.
     *
     * @param password The password to encrypt.
     * @return The encrypted password in hexadecimal format, or null if an error occurs.
     */
    public static String encryptPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply SHA-256 hashing to the password
            byte[] hash = digest.digest(password.getBytes());

            // Convert the byte array into a hexadecimal format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Handle the NoSuchAlgorithmException
            e.printStackTrace();
            return null;
        }
    }
}