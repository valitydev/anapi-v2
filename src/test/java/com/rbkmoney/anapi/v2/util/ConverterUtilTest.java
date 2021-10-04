package com.rbkmoney.anapi.v2.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConverterUtilTest {

    @Test
    void testMerge() {
        String id = "1";
        List<String> ids = List.of("2", "3");
        List<String> result = ConverterUtil.merge(id, ids);
        assertEquals(List.of("1", "2", "3"), result);

        result = ConverterUtil.merge(null, ids);
        assertEquals(List.of("2", "3"), result);

        result = ConverterUtil.merge(id, null);
        assertEquals(List.of("1"), result);

        result = ConverterUtil.merge(null, null);
        assertNotNull(result);
    }
}
