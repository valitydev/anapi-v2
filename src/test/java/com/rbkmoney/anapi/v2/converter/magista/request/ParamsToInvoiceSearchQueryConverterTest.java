package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.InvoiceSearchQuery;
import com.rbkmoney.magista.InvoiceStatus;
import com.rbkmoney.magista.PaymentParams;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamsToInvoiceSearchQueryConverterTest {

    private static final ParamsToInvoiceSearchQueryConverter converter =
            new ParamsToInvoiceSearchQueryConverter();

    @Test
    void convert() {
        InvoiceSearchQuery query = converter.convert("1",
                OffsetDateTime.MIN,
                OffsetDateTime.MAX,
                10,
                List.of("1", "2"),
                List.of("1", "2"),
                "paid",
                "1",
                "2",
                0L,
                1000L,
                "test");
        assertNotNull(query);
    }

    @Test
    void mapPaymentParams() {
        Long amountFrom = 0L;
        Long amountTo = 1000L;
        PaymentParams params = converter.mapPaymentParams(amountFrom, amountTo);
        assertEquals(amountFrom, params.getPaymentAmountFrom());
        assertEquals(amountTo, params.getPaymentAmountTo());
    }

    @Test
    void mapStatus() {
        for (InvoiceStatus status : InvoiceStatus.values()) {
            assertEquals(status, converter.mapStatus(status.name()));
        }
        assertThrows(BadRequestException.class, () -> converter.mapStatus("unexpected"));
    }
}