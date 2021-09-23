package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.anapi.v2.exception.DeadlineException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.rbkmoney.anapi.v2.util.DeadlineUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class DeadlineUtilTest {

    @Test
    void checkDeadlineTest() {
        checkDeadline(null, null);

        checkDeadline("12m", null);
        checkDeadline("1.2m", null);
        assertThrows(DeadlineException.class, () -> checkDeadline("-1.2m", null));

        checkDeadline("12s", null);
        checkDeadline("1.2s", null);
        assertThrows(DeadlineException.class, () -> checkDeadline("-1.2s", null));

        checkDeadline("12ms", null);
        assertThrows(DeadlineException.class, () -> checkDeadline("1.2ms", null));
        assertThrows(DeadlineException.class, () -> checkDeadline("-12ms", null));

        checkDeadline("12m12s12ms", null);
        checkDeadline("1.2m1.2s12ms", null);
        assertThrows(DeadlineException.class, () -> checkDeadline("1.2m1.2s1.2ms", null));
        assertThrows(DeadlineException.class, () -> checkDeadline("12s12s", null));
        assertThrows(DeadlineException.class, () -> checkDeadline("12m12m", null));
        assertThrows(DeadlineException.class, () -> checkDeadline("12ms12ms", null));
        assertThrows(DeadlineException.class, () -> checkDeadline("12s12ms12ms", null));
        assertThrows(DeadlineException.class, () -> checkDeadline("12s12s12ms", null));

        checkDeadline(Instant.now().plus(1, ChronoUnit.DAYS).toString(), null);
        assertThrows(DeadlineException.class,
                () -> checkDeadline(Instant.now().minus(1, ChronoUnit.DAYS).toString(), null));

        assertThrows(DeadlineException.class, () -> checkDeadline("undefined", null));
    }

    @Test
    void extractMillisecondsTest() {
        assertEquals(12, (long) extractMilliseconds("12ms", null));
        assertEquals(12, (long) extractMilliseconds("1.2m1.2s12ms", null));
        assertThrows(DeadlineException.class, () -> extractMilliseconds("1.2ms", null));
        assertThrows(DeadlineException.class, () -> extractMilliseconds("-12ms", null));
        assertThrows(DeadlineException.class, () -> extractMilliseconds("12ms12ms", null));
    }

    @Test
    void extractSecondsTest() {
        assertEquals(12000, (long) extractSeconds("12s", null));
        assertEquals(1200, (long) extractSeconds("1.2s", null));
        assertEquals(1200, (long) extractSeconds("1.2m1.2s12ms", null));
        assertThrows(DeadlineException.class, () -> extractSeconds("-1.2s", null));
        assertThrows(DeadlineException.class, () -> extractSeconds("12s12s", null));

    }

    @Test
    void extractMinutesTest() {
        assertEquals(720000, (long) extractMinutes("12m", null));
        assertEquals(72000, (long) extractMinutes("1.2m", null));
        assertEquals(72000, (long) extractMinutes("1.2m1.2s12ms", null));
        assertThrows(DeadlineException.class, () -> extractMinutes("-1.2m", null));
        assertThrows(DeadlineException.class, () -> extractMinutes("12m12m", null));
    }

    @Test
    void containsRelativeValuesTest() {
        assertTrue(containsRelativeValues("12m", null));
        assertTrue(containsRelativeValues("12s", null));
        assertTrue(containsRelativeValues("12ms", null));
        assertTrue(containsRelativeValues("1.2m1.2s12ms", null));

        assertThrows(DeadlineException.class, () -> containsRelativeValues("-1.2s", null));
        assertThrows(DeadlineException.class, () -> containsRelativeValues("12s12s", null));

        assertFalse(containsRelativeValues(Instant.now().toString(), null));
        assertFalse(containsRelativeValues("asd", null));
    }
}
