package com.tommy.api.center.jbp.tool;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String hex(final byte[] bytes) {
        final char[] chars = new char[bytes.length << 1];
        int index = 0;
        for (final byte b : bytes) {  // two characters form the hex value.
            chars[index++] = HEX_CHARS[(b >> 4) & 0xF];
            chars[index++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    public static String md5Hex(final String text) {
        return hash(text, "MD5");
    }

    private static String hash(final String text, final String algorithm) {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            final byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return hex(digest);
        } catch (final NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }
}
