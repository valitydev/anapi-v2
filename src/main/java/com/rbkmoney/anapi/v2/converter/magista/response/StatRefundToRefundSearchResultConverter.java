package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.RefundSearchResult;
import com.rbkmoney.anapi.v2.model.RefundStatusError;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatRefund;
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
        try {
            var field = InvoicePaymentRefundStatus._Fields.findByName(status.getSetField().getFieldName());
            return switch (field) {
                case PENDING -> RefundSearchResult.StatusEnum.PENDING;
                case SUCCEEDED -> RefundSearchResult.StatusEnum.SUCCEEDED;
                case FAILED -> RefundSearchResult.StatusEnum.FAILED;
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Refund status %s cannot be processed", status));
        }
    }
}
