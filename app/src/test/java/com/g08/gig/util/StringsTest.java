package com.g08.gig.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringsTest {

    @Test
    void safeTrim_null_returnsNull() {
        assertNull(Strings.safeTrim(null));
    }

    @ParameterizedTest
    @MethodSource("blankInputs")
    void safeTrim_blankOrWhitespace_returnsNull(String input) {
        assertNull(Strings.safeTrim(input));
    }

    static Stream<String> blankInputs() {
        return Stream.of("", "   ", "\t\n", "\r\n", " \t \n ");
    }

    @ParameterizedTest
    @MethodSource("trimmedNonEmptyCases")
    void safeTrim_returnsTrimmedNonEmpty(String input, String expected) {
        assertEquals(expected, Strings.safeTrim(input));
    }

    static Stream<Arguments> trimmedNonEmptyCases() {
        return Stream.of(
                Arguments.of("a", "a"),
                Arguments.of("  a  ", "a"),
                Arguments.of("\nx\n", "x"),
                Arguments.of(" \t ab \n", "ab"),
                Arguments.of("hello", "hello"),
                Arguments.of("  spaced words  ", "spaced words")
        );
    }

    @Test
    void safeTrim_preservesInnerWhitespace() {
        assertEquals("a  b", Strings.safeTrim("  a  b  "));
    }

    @ParameterizedTest
    @MethodSource("nullToEmptyCases")
    void nullToEmpty_returnsExpected(String input, String expected) {
        assertEquals(expected, Strings.nullToEmpty(input));
    }

    static Stream<Arguments> nullToEmptyCases() {
        return Stream.of(
                Arguments.of(null, ""),
                Arguments.of("", ""),
                Arguments.of("x", "x"),
                Arguments.of("  raw  ", "  raw  ")
        );
    }

    @Test
    void nullToEmpty_sameInstance_whenNonNull() {
        String s = "unchanged";
        assertSame(s, Strings.nullToEmpty(s));
    }

    @Test
    void constructor_isPrivate() throws Exception {
        Constructor<Strings> c = Strings.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(c.getModifiers()));
        c.setAccessible(true);
        c.newInstance();
    }
}
