package fr.iutrodez.jarspeed.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {

    // Méthode pour crypter un mot de passe avec SHA-256
    public static String encryptPassword(String password) {
        try {
            // Création d'un objet MessageDigest avec l'algorithme SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Appliquer le hachage au mot de passe
            byte[] hash = digest.digest(password.getBytes());

            // Convertir le tableau de bytes en format hexadécimal
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
            // Gérer l'exception NoSuchAlgorithmException
            e.printStackTrace();
            return null;
        }
    }
}