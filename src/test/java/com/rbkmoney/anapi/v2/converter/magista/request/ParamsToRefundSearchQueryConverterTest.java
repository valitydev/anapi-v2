package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.RefundSearchQuery;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamsToRefundSearchQueryConverterTest {

    private static final ParamsToRefundSearchQueryConverter converter = new ParamsToRefundSearchQueryConverter();

    @Test
    void convert() {
        RefundSearchQuery query = converter.convert("1",
                OffsetDateTime.MIN,
                OffsetDateTime.MAX,
                10,
                List.of("1", "2", "3"),
                List.of("1", "2", "3"),
                "1",
                "1",
                "1",
                "1",
                "pending",
                "test");
        assertNotNull(query);
    }

    @Test
    void mapRefundStatus() {
        assertEquals(InvoicePaymentRefundStatus.succeeded, converter.mapStatus("succeeded"));
        assertEquals(InvoicePaymentRefundStatus.failed, converter.mapStatus("failed"));
        assertEquals(InvoicePaymentRefundStatus.pending, converter.mapStatus("pending"));
        assertThrows(BadRequestException.class, () -> converter.mapStatus("unexpected"));
    }
}