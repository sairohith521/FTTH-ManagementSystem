package ftth.util;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing and verification.
 *
 * IMPORTANT:
 * - Never store plain passwords.
 * - Always store hashed passwords.
 */
public final class PasswordUtil {

    // Prevent instantiation
    private PasswordUtil() {
    }

    /**
     * Hash a plain text password using SHA-256.
     *
     * @param plainPassword password entered by user
     * @return hashed password (hex string)
     */
    public static String hash(String plainPassword) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedHash =
                digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify if plain password matches stored hash.
     *
     * @param plainPassword password entered by user
     * @param storedHash password hash from database
     * @return true if match, false otherwise
     */
    public static boolean matches(String plainPassword, String storedHash) {

        if (plainPassword == null || storedHash == null) {
            return false;
        }

        String hashedInput = hash(plainPassword);
        return hashedInput.equals(storedHash);
    }

    /**
     * Convert byte array to hex string.
     */
    private static String bytesToHex(byte[] hash) {

        StringBuilder hexString = new StringBuilder(2 * hash.length);

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
