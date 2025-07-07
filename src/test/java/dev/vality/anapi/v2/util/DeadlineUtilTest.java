package dev.vality.anapi.v2.util;

import dev.vality.anapi.v2.exception.DeadlineException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeadlineUtilTest {

    @Test
    void checkDeadlineTest() {
        DeadlineUtil.checkDeadline(null, null);

        DeadlineUtil.checkDeadline("12m", null);
        DeadlineUtil.checkDeadline("1.2m", null);
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("-1.2m", null));

        DeadlineUtil.checkDeadline("12s", null);
        DeadlineUtil.checkDeadline("1.2s", null);
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("-1.2s", null));

        DeadlineUtil.checkDeadline("12ms", null);
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("1.2ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("-12ms", null));

        DeadlineUtil.checkDeadline("12m12s12ms", null);
        DeadlineUtil.checkDeadline("1.2m1.2s12ms", null);
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("1.2m1.2s1.2ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("12s12s", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("12m12m", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("12ms12ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("12s12ms12ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("12s12s12ms", null));

        DeadlineUtil.checkDeadline(Instant.now().plus(1, ChronoUnit.DAYS).toString(), null);
        assertThrows(DeadlineException.class,
                () -> DeadlineUtil.checkDeadline(Instant.now().minus(1, ChronoUnit.DAYS).toString(), null));

        assertThrows(DeadlineException.class, () -> DeadlineUtil.checkDeadline("undefined", null));
    }

    @Test
    void extractMillisecondsTest() {
        assertEquals(12, (long) DeadlineUtil.extractMilliseconds("12ms", null));
        assertEquals(12, (long) DeadlineUtil.extractMilliseconds("1.2m1.2s12ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractMilliseconds("1.2ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractMilliseconds("-12ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractMilliseconds("12ms12ms", null));
    }

    @Test
    void extractSecondsTest() {
        assertEquals(12000, (long) DeadlineUtil.extractSeconds("12s", null));
        assertEquals(1200, (long) DeadlineUtil.extractSeconds("1.2s", null));
        assertEquals(1200, (long) DeadlineUtil.extractSeconds("1.2m1.2s12ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractSeconds("-1.2s", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractSeconds("12s12s", null));

    }

    @Test
    void extractMinutesTest() {
        assertEquals(720000, (long) DeadlineUtil.extractMinutes("12m", null));
        assertEquals(72000, (long) DeadlineUtil.extractMinutes("1.2m", null));
        assertEquals(72000, (long) DeadlineUtil.extractMinutes("1.2m1.2s12ms", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractMinutes("-1.2m", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.extractMinutes("12m12m", null));
    }

    @Test
    void containsRelativeValuesTest() {
        Assertions.assertTrue(DeadlineUtil.containsRelativeValues("12m", null));
        Assertions.assertTrue(DeadlineUtil.containsRelativeValues("12s", null));
        Assertions.assertTrue(DeadlineUtil.containsRelativeValues("12ms", null));
        Assertions.assertTrue(DeadlineUtil.containsRelativeValues("1.2m1.2s12ms", null));

        assertThrows(DeadlineException.class, () -> DeadlineUtil.containsRelativeValues("-1.2s", null));
        assertThrows(DeadlineException.class, () -> DeadlineUtil.containsRelativeValues("12s12s", null));

        Assertions.assertFalse(DeadlineUtil.containsRelativeValues(Instant.now().toString(), null));
        Assertions.assertFalse(DeadlineUtil.containsRelativeValues("asd", null));
    }
}
