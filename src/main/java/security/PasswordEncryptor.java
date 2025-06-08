package security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Clase para encriptar contraseñas utilizando algoritmo SHA-256
 */
public class PasswordEncryptor {

    /**
     * Encripta una contraseña usando SHA-256
     *
     * @param plainPassword La contraseña en texto plano
     * @return La contraseña encriptada en formato Base64
     */
    public static String encrypt(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una contraseña encriptada
     *
     * @param plainPassword La contraseña en texto plano a verificar
     * @param encryptedPassword La contraseña encriptada almacenada
     * @return true si las contraseñas coinciden, false en caso contrario
     */
    public static boolean verify(String plainPassword, String encryptedPassword) {
        String encryptedInput = encrypt(plainPassword);
        return encryptedInput != null && encryptedInput.equals(encryptedPassword);
    }
}