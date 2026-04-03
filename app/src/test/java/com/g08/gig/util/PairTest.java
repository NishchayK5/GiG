package com.g08.gig.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PairTest {

    @Test
    void holdsFirstAndSecond() {
        Pair<String, Integer> p = new Pair<>("a", 42);
        assertEquals("a", p.first);
        assertEquals(42, p.second);
    }

    @Test
    void allowsNullMembers() {
        Pair<Object, Object> p = new Pair<>(null, null);
        assertNull(p.first);
        assertNull(p.second);
    }

    @Test
    void supportsMixedTypes() {
        Pair<Integer, String> p = new Pair<>(-1, "last");
        assertEquals(-1, p.first.intValue());
        assertEquals("last", p.second);
    }

    @Test
    void sameTypeParameters() {
        Pair<Long, Long> p = new Pair<>(1L, 2L);
        assertEquals(1L, p.first.longValue());
        assertEquals(2L, p.second.longValue());
    }

    @Test
    void firstNullSecondPresent() {
        Pair<String, List<String>> p = new Pair<>(null, List.of("a"));
        assertNull(p.first);
        assertEquals(1, p.second.size());
    }

    @Test
    void fieldsAreIndependentReferences() {
        Object a = new Object();
        Object b = new Object();
        Pair<Object, Object> p = new Pair<>(a, b);
        assertSame(a, p.first);
        assertSame(b, p.second);
        assertNotEquals(p.first, p.second);
    }

    @Test
    void pairInstancesNotEqualByDefault() {
        Pair<String, String> p1 = new Pair<>("k", "v");
        Pair<String, String> p2 = new Pair<>("k", "v");
        assertNotEquals(p1, p2);
    }
}
