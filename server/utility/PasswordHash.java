package server.utility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hashes password
 */
public class PasswordHash {
    /**
     * Hashes password;
     * @param password Password itself.
     * @return Hashed password.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-224");
            byte[] md = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger integer = new BigInteger(1, md);
            StringBuilder hashText = new StringBuilder(integer.toString());
            while (hashText.length() < 32) hashText.insert(0, "0");
            return hashText.toString();
        } catch (NoSuchAlgorithmException exception) {
            ResponseOutputDeliver.appendError("Алгоритм хэширования не обнаружен!");
        }
        return null;
    }
}
