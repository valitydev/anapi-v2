package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.model.PaymentStatus;
import com.rbkmoney.damsel.domain.LegacyBankCardPaymentSystem;
import com.rbkmoney.damsel.domain.LegacyBankCardTokenProvider;
import com.rbkmoney.damsel.domain.LegacyTerminalPaymentProvider;
import com.rbkmoney.magista.InvoicePaymentFlowType;
import com.rbkmoney.magista.PaymentSearchQuery;
import com.rbkmoney.magista.PaymentToolType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamsToPaymentSearchQueryConverterTest {

    private static final ParamsToPaymentSearchQueryConverter converter = new ParamsToPaymentSearchQueryConverter();

    @Test
    void convert() {
        PaymentSearchQuery query = converter.convert("1",
                OffsetDateTime.MIN,
                OffsetDateTime.MAX,
                10,
                List.of("1", "2", "3"),
                List.of("1", "2", "3"),
                "cancelled",
                "hold",
                "paymentTerminal",
                "euroset",
                "1",
                "1",
                "1",
                "mail@mail.com",
                "127.0.0.1",
                "fingerprint",
                "1",
                "123456",
                "7890",
                "012345678",
                "123456",
                "applepay",
                "mastercard",
                0L,
                1000L,
                List.of("1", "2", "3"),
                "test");
        assertNotNull(query);
    }

    @Test
    void mapPaymentTool() {
        assertEquals(PaymentToolType.bank_card, converter.mapPaymentTool("bankCard"));
        assertEquals(PaymentToolType.payment_terminal, converter.mapPaymentTool("paymentTerminal"));
        assertThrows(BadRequestException.class, () -> converter.mapPaymentTool("unexpected"));
    }

    @Test
    void mapInvoicePaymentFlow() {
        assertEquals(InvoicePaymentFlowType.instant, converter.mapInvoicePaymentFlow("instant"));
        assertEquals(InvoicePaymentFlowType.hold, converter.mapInvoicePaymentFlow("hold"));
        assertThrows(BadRequestException.class, () -> converter.mapInvoicePaymentFlow("unexpected"));
    }

    @Test
    void mapStatus() {
        for (PaymentStatus.StatusEnum status : PaymentStatus.StatusEnum.values()) {
            assertNotNull(converter.mapStatus(status.getValue()));
        }

        assertThrows(BadRequestException.class, () -> converter.mapStatus("unexpected"));
    }

    @Test
    void mapTerminalProvider() {
        for (LegacyTerminalPaymentProvider provider : LegacyTerminalPaymentProvider.values()) {
            assertEquals(provider, converter.mapTerminalProvider(provider.name()));
        }
        assertThrows(BadRequestException.class, () -> converter.mapTerminalProvider("unexpected"));
    }

    @Test
    void mapTokenProvider() {
        for (LegacyBankCardTokenProvider provider : LegacyBankCardTokenProvider.values()) {
            assertEquals(provider, converter.mapTokenProvider(provider.name()));
        }
        assertThrows(BadRequestException.class, () -> converter.mapTokenProvider("unexpected"));
    }

    @Test
    void mapPaymentSystem() {
        for (LegacyBankCardPaymentSystem system : LegacyBankCardPaymentSystem.values()) {
            assertEquals(system, converter.mapPaymentSystem(system.name()));
        }
        assertThrows(BadRequestException.class, () -> converter.mapPaymentSystem("unexpected"));
    }
}