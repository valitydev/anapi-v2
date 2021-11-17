package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.RefundSearchResult;
import com.rbkmoney.anapi.v2.model.RefundStatusError;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.StatRefund;
import com.rbkmoney.magista.StatRefundResponse;
import org.junit.jupiter.api.Test;

import static com.rbkmoney.anapi.v2.model.RefundSearchResult.StatusEnum.*;
import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createSearchRefundAllResponse;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomString;
import static org.junit.jupiter.api.Assertions.*;

class StatRefundToRefundSearchResultConverterTest {

    private static final StatRefundToRefundSearchResultConverter converter =
            new StatRefundToRefundSearchResultConverter();

    @Test
    void convert() {
        StatRefundResponse magistaResponse = createSearchRefundAllResponse();
        StatRefund magistaRefund = magistaResponse.getRefunds().get(0);
        RefundSearchResult result = converter.convert(magistaRefund);
        assertAll(
                () -> assertEquals(magistaRefund.getAmount(), result.getAmount()),
                () -> assertEquals(magistaRefund.getCreatedAt(), result.getCreatedAt().toString()),
                () -> assertEquals(magistaRefund.getCurrencySymbolicCode(), result.getCurrency()),
                () -> assertEquals(magistaRefund.getId(), result.getId()),
                () -> assertEquals(magistaRefund.getShopId(), result.getShopID()),
                () -> assertEquals(magistaRefund.getExternalId(), result.getExternalID()),
                () -> assertEquals(magistaRefund.getInvoiceId(), result.getInvoiceID()),
                () -> assertEquals(magistaRefund.getPaymentId(), result.getPaymentID()),
                () -> assertEquals(magistaRefund.getReason(), result.getReason())
        );
    }

    @Test
    void mapStatusError() {
        assertNull(converter.mapStatusError(new InvoicePaymentRefundStatus()));
        String reason = randomString(10);
        String code = randomString(10);
        InvoicePaymentRefundStatus status = InvoicePaymentRefundStatus
                .failed(new InvoicePaymentRefundFailed()
                        .setFailure(OperationFailure.failure(new Failure()
                                .setReason(reason)
                                .setCode(code))));
        RefundStatusError statusError = converter.mapStatusError(status);
        assertEquals(reason, statusError.getMessage());
        assertEquals(code, statusError.getCode());
    }

    @Test
    void mapStatus() {
        assertEquals(PENDING,
                converter.mapStatus(InvoicePaymentRefundStatus.pending(new InvoicePaymentRefundPending())));
        assertEquals(FAILED,
                converter.mapStatus(InvoicePaymentRefundStatus.failed(new InvoicePaymentRefundFailed())));
        assertEquals(SUCCEEDED,
                converter.mapStatus(InvoicePaymentRefundStatus.succeeded(new InvoicePaymentRefundSucceeded())));
        assertThrows(IllegalArgumentException.class, () -> converter.mapStatus(new InvoicePaymentRefundStatus()));
    }
}