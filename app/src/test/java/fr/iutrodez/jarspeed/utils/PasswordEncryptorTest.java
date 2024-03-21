package fr.iutrodez.jarspeed.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class PasswordEncryptorTest {

    @Test
    public void testEncryptPassword() {
        String password = "myPassword123";
        String encryptedPassword = PasswordEncryptor.encryptPassword(password);

        // Vérifier que le mot de passe crypté n'est pas vide
        assertNotNull(encryptedPassword);

        // Vérifier que le mot de passe crypté a une longueur attendue (SHA-256 génère une chaîne de 64 caractères)
        assertEquals(64, encryptedPassword.length());

        // Vérifier que le mot de passe crypté est différent du mot de passe en clair
        assertNotEquals(password, encryptedPassword);

        // Vérifier qu'un mot de passe identique donne un résultat identique
        String reEncryptedPassword = PasswordEncryptor.encryptPassword(password);
        assertEquals(encryptedPassword, reEncryptedPassword);

        // Vérifier que la méthode fonctionne avec un mot de passe vide
        String emptyPassword = "";
        String encryptedEmptyPassword = PasswordEncryptor.encryptPassword(emptyPassword);
        assertNotNull(encryptedEmptyPassword);
        assertEquals(64, encryptedEmptyPassword.length());
    }
}
