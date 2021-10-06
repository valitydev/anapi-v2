package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatRefund;
import com.rbkmoney.openapi.anapi_v2.model.RefundSearchResult;
import com.rbkmoney.openapi.anapi_v2.model.RefundStatusError;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class StatRefundToRefundSearchResultConverter {

    public RefundSearchResult convert(StatRefund refund) {
        return new RefundSearchResult()
                .amount(refund.getAmount())
                .createdAt(TypeUtil.stringToInstant(refund.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(refund.getCurrencySymbolicCode())
                .id(refund.getId())
                .shopID(refund.getShopId())
                .status(mapStatus(refund.getStatus()))
                .externalID(refund.getExternalId())
                .error(mapStatusError(refund.getStatus()))
                .invoiceID(refund.getInvoiceId())
                .paymentID(refund.getPaymentId())
                .reason(refund.getReason());
    }

    protected RefundStatusError mapStatusError(InvoicePaymentRefundStatus status) {
        if (status.isSetFailed() && status.getFailed().getFailure().isSetFailure()) {
            var failure = status.getFailed().getFailure().getFailure();
            return new RefundStatusError()
                    .code(failure.getCode())
                    .message(failure.getReason());
        }

        return null;
    }

    protected RefundSearchResult.StatusEnum mapStatus(InvoicePaymentRefundStatus status) {
        if (status.isSetPending()) {
            return RefundSearchResult.StatusEnum.PENDING;
        }

        if (status.isSetFailed()) {
            return RefundSearchResult.StatusEnum.FAILED;
        }

        if (status.isSetSucceeded()) {
            return RefundSearchResult.StatusEnum.SUCCEEDED;
        }

        throw new IllegalArgumentException(
                String.format("Refund status %s cannot be processed", status));
    }
}
