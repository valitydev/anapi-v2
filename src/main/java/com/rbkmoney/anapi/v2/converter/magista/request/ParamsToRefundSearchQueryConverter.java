package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.RefundSearchQuery;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;

@Component
public class ParamsToRefundSearchQueryConverter {

    public RefundSearchQuery convert(String partyID,
                                     OffsetDateTime fromTime,
                                     OffsetDateTime toTime,
                                     Integer limit,
                                     List<String> shopIDs,
                                     List<String> invoiceIDs,
                                     String invoiceID,
                                     String paymentID,
                                     String refundID,
                                     String externalID,
                                     String refundStatus,
                                     String continuationToken) {
        return new RefundSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setRefundStatus(refundStatus != null ? mapStatus(refundStatus) : null)
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID)
                .setPaymentId(paymentID)
                .setRefundId(refundID);
    }

    protected InvoicePaymentRefundStatus mapStatus(String status) {
        try {
            return InvoicePaymentRefundStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Refund status %s cannot be processed", status));
        }
    }

}
