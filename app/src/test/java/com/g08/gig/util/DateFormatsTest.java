package com.g08.gig.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatsTest {

    @Test
    void requestTime_formatsDateTimeCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2025, Month.MARCH, 11, 14, 30);
        String formatted = dateTime.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(dateTime, parsed, "Format then parse should round-trip (locale-independent)");
    }

    @Test
    void requestTime_parsesFormattedStringBack() {
        LocalDateTime original = LocalDateTime.of(2025, Month.MARCH, 11, 14, 30);
        String formatted = original.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(2025, parsed.getYear());
        assertEquals(Month.MARCH, parsed.getMonth());
        assertEquals(11, parsed.getDayOfMonth());
        assertEquals(14, parsed.getHour());
        assertEquals(30, parsed.getMinute());
        assertEquals(original, parsed);
    }

    @Test
    void requestTime_singleDigitDayAndMorning_roundTrips() {
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.JANUARY, 5, 9, 5);
        String formatted = dateTime.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(dateTime, parsed);
    }

    @Test
    void requestTime_noon_roundTrips() {
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.JUNE, 30, 12, 0);
        String formatted = dateTime.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(dateTime, parsed);
    }

    @Test
    void requestTime_midnight_roundTrips() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.DECEMBER, 31, 0, 0);
        String formatted = dateTime.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(dateTime, parsed);
    }

    @Test
    void requestTime_endOfDay_roundTrips() {
        LocalDateTime dateTime = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59);
        String formatted = dateTime.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(dateTime, parsed);
    }

    @Test
    void requestTime_leapDayFeb29_roundTrips() {
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.FEBRUARY, 29, 15, 45);
        String formatted = dateTime.format(DateFormats.REQUEST_TIME);
        LocalDateTime parsed = LocalDateTime.parse(formatted, DateFormats.REQUEST_TIME);
        assertEquals(dateTime, parsed);
    }

    @Test
    void requestTime_formatterConstantIsStable() {
        assertSame(DateFormats.REQUEST_TIME, DateFormats.REQUEST_TIME);
    }

    @Test
    void constructor_isPrivateAndUncallable() throws Exception {
        Constructor<DateFormats> c = DateFormats.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(c.getModifiers()));
        c.setAccessible(true);
        c.newInstance();
    }
}
