package io.github.yudady.util;

import java.util.List;
import javax.annotation.Nullable;
import org.slf4j.helpers.MessageFormatter;


import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author neo
 */
public final class Strings {
    public static byte[] bytes(String text) {
        return text.getBytes(UTF_8);
    }

    public static String format(String pattern, Object... params) {
        return MessageFormatter.arrayFormat(pattern, params).getMessage();
    }

    public static int compare(@Nullable String text1, @Nullable String text2) {
        if (text1 == null && text2 == null) return 0;
        if (text1 != null && text2 == null) return 1;
        if (text1 == null) return -1;
        return text1.compareTo(text2);
    }

    public static boolean isBlank(@Nullable String text) {
        return text == null || text.isBlank();
    }

    public static boolean equals(@Nullable String text1, @Nullable String text2) {
        if (text1 == null) return text2 == null;
        return text1.equals(text2);
    }

    @Nullable
    public static String truncate(@Nullable String text, int maxLength) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength);
    }

    @Nullable
    public static String strip(@Nullable String text) {
        if (text == null) return null;
        return text.strip();
    }

    public static boolean startsWith(String text, char prefix) {
        if (text.isEmpty()) return false;
        return text.charAt(0) == prefix;
    }

    public static String[] split(String text, char delimiter) {
        List<String> tokens = Lists.newArrayList();
        int start = 0;
        while (true) {
            int next = text.indexOf(delimiter, start);
            if (next == -1) break;
            tokens.add(text.substring(start, next));
            start = next + 1;
        }
        if (start == 0) return new String[]{text};
        else tokens.add(text.substring(start));
        return tokens.toArray(new String[0]);
    }
}
