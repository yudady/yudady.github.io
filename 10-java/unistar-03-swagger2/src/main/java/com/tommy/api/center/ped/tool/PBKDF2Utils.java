package com.tommy.api.center.ped.tool;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PED PBKDF2 加密使用操作
 */
public final class PBKDF2Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PBKDF2Utils.class);
    private static final String DELIMITER = ":";
    private static final int SALT_BYTE_SIZE = 24; // Salt Lenth
    private static final int HASH_BYTE_SIZE = 20 * 8; // To match the size of the PBKDF2-HMAC-SHA-1 hash
    private static final int PBKDF_2_ITERATIONS = 1020; // No of iteration for the operation
    private static final String PBKDF2 = "PBKDF2WithHmacSHA1";

    public static String encrypt(final String text) {
        try {
            final SecureRandom rngc = new SecureRandom();
            final byte[] salt = new byte[SALT_BYTE_SIZE];
            rngc.nextBytes(salt);
            final String result = Encodings.base64(salt) + DELIMITER + PBKDF_2_ITERATIONS + DELIMITER + Encodings.base64(getSecretKey(text, salt, PBKDF_2_ITERATIONS));
            System.out.println("[LOG] text = " + text);
            System.out.println("[LOG] result = " + result);
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void validate(final String key, final String text) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String[] keyArray = key.split(":");
        final byte[] salt = Encodings.decodeBase64(keyArray[0]);
        final int iterations = Integer.parseInt(keyArray[1]);
        final String encryptString = Encodings.base64(getSecretKey(text, salt, iterations));
        LOGGER.info("key = {}, inputText = {}, encryptString = {}", key, text, encryptString);
        if (!Objects.equals(keyArray[2], encryptString)) {
            final String msg = "key is not match : key = " + key + ", inputText = " + text + ", encryptString = " + encryptString + "  key is not match.";
            throw new RuntimeException(msg);
        }
        // return Collections.singletonMap(Strings.equals(keyArray[2], encryptString), encryptString);
    }

    public static byte[] getSecretKey(final String text, final byte[] salt, final int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2);
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(text.toCharArray(), salt, iterations, HASH_BYTE_SIZE);
        final Key secretKey = factory.generateSecret(pbeKeySpec);
        return secretKey.getEncoded();
    }

    private PBKDF2Utils() {

    }
}