package io.github.saferkit.strings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SaferStringsTest {
    @Test
    void testIsNullOrEmpty() {
        assertTrue(SaferStrings.isNullOrEmpty(null));
        assertTrue(SaferStrings.isNullOrEmpty(""));
        assertFalse(SaferStrings.isNullOrEmpty(" "));
        assertFalse(SaferStrings.isNullOrEmpty("a"));
        assertFalse(SaferStrings.isNullOrEmpty("  text  "));
    }

    @Test
    void testIsNullOrBlank() {
        assertTrue(SaferStrings.isNullOrBlank(null));
        assertTrue(SaferStrings.isNullOrBlank(""));
        assertTrue(SaferStrings.isNullOrBlank(" "));
        assertTrue(SaferStrings.isNullOrBlank("   "));
        assertTrue(SaferStrings.isNullOrBlank("\t\n\r"));
        assertFalse(SaferStrings.isNullOrBlank("a"));
        assertFalse(SaferStrings.isNullOrBlank("  text  "));
    }

    @Test
    void testIsEmpty() {
        assertTrue(SaferStrings.isEmpty(""));
        assertFalse(SaferStrings.isEmpty(" "));
        assertFalse(SaferStrings.isEmpty("a"));
        assertFalse(SaferStrings.isEmpty("  text  "));
    }

    @Test
    void testIsEmptyThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> SaferStrings.isEmpty(null));
    }

    @Test
    void testIsBlank() {
        assertTrue(SaferStrings.isBlank(""));
        assertTrue(SaferStrings.isBlank(" "));
        assertTrue(SaferStrings.isBlank("   "));
        assertTrue(SaferStrings.isBlank("\t\n\r"));
        assertFalse(SaferStrings.isBlank("a"));
        assertFalse(SaferStrings.isBlank("  text  "));
    }

    @Test
    void testIsBlankThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> SaferStrings.isBlank(null));
    }

    @Test
    void testIsNotEmpty() {
        assertFalse(SaferStrings.isNotEmpty(""));
        assertTrue(SaferStrings.isNotEmpty(" "));
        assertTrue(SaferStrings.isNotEmpty("a"));
        assertTrue(SaferStrings.isNotEmpty("  text  "));
    }

    @Test
    void testIsNotEmptyThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> SaferStrings.isNotEmpty(null));
    }

    @Test
    void testIsNotBlank() {
        assertFalse(SaferStrings.isNotBlank(""));
        assertFalse(SaferStrings.isNotBlank(" "));
        assertFalse(SaferStrings.isNotBlank("   "));
        assertFalse(SaferStrings.isNotBlank("\t\n\r"));
        assertTrue(SaferStrings.isNotBlank("a"));
        assertTrue(SaferStrings.isNotBlank("  text  "));
    }

    @Test
    void testIsNotBlankThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> SaferStrings.isNotBlank(null));
    }

    // ========== Null conversions ==========

    @Test
    void testNullToEmpty() {
        assertEquals("", SaferStrings.nullToEmpty(null));
        assertEquals("", SaferStrings.nullToEmpty(""));
        assertEquals(" ", SaferStrings.nullToEmpty(" "));
        assertEquals("text", SaferStrings.nullToEmpty("text"));
    }

    @Test
    void testEmptyToNull() {
        assertNull(SaferStrings.emptyToNull(""));
        assertEquals(" ", SaferStrings.emptyToNull(" "));
        assertEquals("text", SaferStrings.emptyToNull("text"));
    }

    @Test
    void testEmptyToNullThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> SaferStrings.emptyToNull(null));
    }

    @Test
    void testTrimToEmpty() {
        assertEquals("", SaferStrings.trimToEmpty(null));
        assertEquals("", SaferStrings.trimToEmpty(""));
        assertEquals("", SaferStrings.trimToEmpty(" "));
        assertEquals("", SaferStrings.trimToEmpty("   "));
        assertEquals("text", SaferStrings.trimToEmpty("  text  "));
        assertEquals("a b", SaferStrings.trimToEmpty("  a b  "));
    }

    @Test
    void testTrimToNull() {
        assertNull(SaferStrings.trimToNull(null));
        assertNull(SaferStrings.trimToNull(""));
        assertNull(SaferStrings.trimToNull(" "));
        assertNull(SaferStrings.trimToNull("   "));
        assertNull(SaferStrings.trimToNull("\t\n\r"));
        assertEquals("text", SaferStrings.trimToNull("  text  "));
        assertEquals("a b", SaferStrings.trimToNull("  a b  "));
    }

    // ========== Safe comparisons ==========

    @Test
    void testContainsIgnoreCase() {
        assertTrue(SaferStrings.containsIgnoreCase("Hello World", "world"));
        assertTrue(SaferStrings.containsIgnoreCase("Hello World", "WORLD"));
        assertTrue(SaferStrings.containsIgnoreCase("Hello World", "hello"));
        assertTrue(SaferStrings.containsIgnoreCase("Hello World", "o W"));
        assertFalse(SaferStrings.containsIgnoreCase("Hello World", "xyz"));
        assertThrows(NullPointerException.class, () -> SaferStrings.containsIgnoreCase(null, "world"));
        assertThrows(NullPointerException.class, () -> SaferStrings.containsIgnoreCase("Hello World", null));
        assertThrows(NullPointerException.class, () -> SaferStrings.containsIgnoreCase(null, null));
    }

    @Test
    void testStartsWithIgnoreCase() {
        assertTrue(SaferStrings.startsWithIgnoreCase("Hello World", "hello"));
        assertTrue(SaferStrings.startsWithIgnoreCase("Hello World", "HELLO"));
        assertTrue(SaferStrings.startsWithIgnoreCase("Hello World", "Hello"));
        assertFalse(SaferStrings.startsWithIgnoreCase("Hello World", "world"));
        assertThrows(NullPointerException.class, () -> SaferStrings.startsWithIgnoreCase(null, "hello"));
        assertThrows(NullPointerException.class, () -> SaferStrings.startsWithIgnoreCase("Hello World", null));
        assertThrows(NullPointerException.class, () -> SaferStrings.startsWithIgnoreCase(null, null));
    }

    @Test
    void testEndsWithIgnoreCase() {
        assertTrue(SaferStrings.endsWithIgnoreCase("Hello World", "world"));
        assertTrue(SaferStrings.endsWithIgnoreCase("Hello World", "WORLD"));
        assertTrue(SaferStrings.endsWithIgnoreCase("Hello World", "World"));
        assertFalse(SaferStrings.endsWithIgnoreCase("Hello World", "hello"));
        assertThrows(NullPointerException.class, () -> SaferStrings.endsWithIgnoreCase(null, "world"));
        assertThrows(NullPointerException.class, () -> SaferStrings.endsWithIgnoreCase("Hello World", null));
        assertThrows(NullPointerException.class, () -> SaferStrings.endsWithIgnoreCase(null, null));
    }

    // ========== Edge cases and special characters ==========

    @Test
    void testUnicodeHandling() {
        String emoji = "Hello 👋 World";
        assertFalse(SaferStrings.isNullOrEmpty(emoji));
        assertTrue(SaferStrings.containsIgnoreCase(emoji, "world"));
    }

    @Test
    void testEmptyStringBuilderToString() {
        StringBuilder sb = new StringBuilder();
        assertTrue(SaferStrings.isNullOrEmpty(sb));
        assertEquals("", SaferStrings.nullToEmpty(sb));
    }

    @Test
    void testStringBuilderInput() {
        StringBuilder sb = new StringBuilder("hello");
        assertFalse(SaferStrings.isNullOrEmpty(sb));
        assertEquals("hello", SaferStrings.nullToEmpty(sb));
        assertTrue(SaferStrings.containsIgnoreCase(sb, "HELLO"));
    }
}
