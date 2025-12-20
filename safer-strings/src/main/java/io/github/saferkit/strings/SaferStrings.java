package io.github.saferkit.strings;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Consistent, null-safe string utilities.
 * <p>
 * This class provides string operations with explicit null handling. Methods that accept null
 * include "Null" in their name (e.g., {@code isNullOrEmpty}). Methods without "Null" in the name
 * throw {@link NullPointerException} when given null input, making null handling explicit and predictable.
 * </p>
 *
 * <h2>Examples</h2>
 * <pre>{@code
 * // Null-safe checks
 * SaferStrings.isNullOrEmpty(null);     // true
 * SaferStrings.isNullOrBlank("  ");     // true
 *
 * // Non-null checks (throw NPE on null)
 * SaferStrings.isEmpty("");             // true
 * SaferStrings.isEmpty(null);           // throws NullPointerException
 *
 * // Null conversions
 * SaferStrings.nullToEmpty(null);       // ""
 * SaferStrings.emptyToNull("");         // null
 * SaferStrings.trimToEmpty("  text  "); // "text"
 * }</pre>
 *
 * @since 1.0.0
 */
public final class SaferStrings {

    private SaferStrings() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Checks if a CharSequence is null or has zero length.
     *
     * @param s the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null or empty
     */
    public static boolean isNullOrEmpty(@Nullable CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * Checks if a CharSequence is null, empty, or contains only whitespace.
     *
     * @param s the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty, or whitespace-only
     */
    public static boolean isNullOrBlank(@Nullable CharSequence s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a CharSequence has zero length.
     *
     * @param s the CharSequence to check, must not be null
     * @return {@code true} if the CharSequence is empty
     * @throws NullPointerException if s is null
     */
    public static boolean isEmpty(CharSequence s) {
        Objects.requireNonNull(s, "s must not be null");
        return s.length() == 0;
    }

    /**
     * Checks if a CharSequence is empty or contains only whitespace.
     *
     * @param s the CharSequence to check, must not be null
     * @return {@code true} if the CharSequence is empty or whitespace-only
     * @throws NullPointerException if s is null
     */
    public static boolean isBlank(CharSequence s) {
        Objects.requireNonNull(s, "s must not be null");
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a CharSequence is not empty.
     *
     * @param s the CharSequence to check, must not be null
     * @return {@code true} if the CharSequence has length &gt; 0
     * @throws NullPointerException if s is null
     */
    public static boolean isNotEmpty(CharSequence s) {
        return !isEmpty(s);
    }

    /**
     * Checks if a CharSequence is not empty and contains at least one non-whitespace character.
     *
     * @param s the CharSequence to check, must not be null
     * @return {@code true} if the CharSequence contains non-whitespace
     * @throws NullPointerException if s is null
     */
    public static boolean isNotBlank(CharSequence s) {
        return !isBlank(s);
    }

    // ========== Null conversions ==========

    /**
     * Converts null to empty string.
     *
     * @param s the CharSequence to convert, may be null
     * @return the input as a String, or empty string if input was null
     */
    public static String nullToEmpty(@Nullable CharSequence s) {
        return s == null ? "" : s.toString();
    }

    /**
     * Converts empty string to null.
     *
     * @param s the CharSequence to convert, must not be null
     * @return the input as a String, or null if input was empty
     * @throws NullPointerException if s is null
     */
    public static @Nullable String emptyToNull(CharSequence s) {
        Objects.requireNonNull(s, "s must not be null");
        return s.length() == 0 ? null : s.toString();
    }

    /**
     * Trims whitespace and converts null to empty string.
     *
     * @param s the CharSequence to trim, may be null
     * @return trimmed string, or empty string if input was null or blank
     */
    public static String trimToEmpty(@Nullable CharSequence s) {
        if (s == null) {
            return "";
        }
        return s.toString().trim();
    }

    /**
     * Trims whitespace and converts empty/blank to null.
     *
     * @param s the CharSequence to trim, may be null
     * @return trimmed string, or null if input was null, empty, or blank
     */
    public static @Nullable String trimToNull(@Nullable CharSequence s) {
        if (s == null) {
            return null;
        }
        String trimmed = s.toString().trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Checks if the first CharSequence contains the second, ignoring case.
     * Returns false if either input is null.
     *
     * @param str    the CharSequence to search in, not null
     * @param search the CharSequence to search for, not null
     * @return {@code true} if str contains search (case-insensitive)
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence search) {
        String strString = str.toString();
        String searchString = search.toString();
        int searchLength = searchString.length();
        int strLength = strString.length();

        if (searchLength == 0) {
            return true;
        }

        for (int i = 0; i <= strLength - searchLength; i++) {
            if (strString.regionMatches(true, i, searchString, 0, searchLength)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the CharSequence starts with the prefix, ignoring case.
     * Returns false if either input is null.
     *
     * @param str    the CharSequence to check, not null
     * @param prefix the prefix to check for, not null
     * @return {@code true} if str starts with prefix (case-insensitive)
     */
    public static boolean startsWithIgnoreCase(CharSequence str, CharSequence prefix) {
        String strString = str.toString();
        String prefixString = prefix.toString();
        return strString.regionMatches(true, 0, prefixString, 0, prefixString.length());
    }

    /**
     * Checks if the CharSequence ends with the suffix, ignoring case.
     * Returns false if either input is null.
     *
     * @param str    the CharSequence to check, not null
     * @param suffix the suffix to check for, not null
     * @return {@code true} if str ends with suffix (case-insensitive)
     */
    public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
        String strString = str.toString();
        String suffixString = suffix.toString();
        int strLength = strString.length();
        int suffixLength = suffixString.length();
        return strString.regionMatches(true, strLength - suffixLength, suffixString, 0, suffixLength);
    }
}
