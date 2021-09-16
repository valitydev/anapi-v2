package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoicePaymentRefundStatus;
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
                .status(refund.getStatus() != null ? mapToRefundStatus(refund.getStatus()) : null)
                .externalID(refund.getExternalId())
                .error(refund.getStatus().isSetFailed()
                        && refund.getStatus().getFailed().getFailure().isSetFailure()
                        ? new RefundStatusError()
                        .code(refund.getStatus().getFailed().getFailure().getFailure().getCode())
                        .message(refund.getStatus().getFailed().getFailure().getFailure().getReason())
                        : null)
                .invoiceID(refund.getInvoiceId())
                .paymentID(refund.getPaymentId())
                .reason(refund.getReason());
    }

    private RefundSearchResult.StatusEnum mapToRefundStatus(InvoicePaymentRefundStatus status) {
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
