package dev.vality.anapi.v2.converter.magista.request;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.util.ConverterUtil;
import dev.vality.magista.InvoicePaymentRefundStatus;
import dev.vality.magista.RefundSearchQuery;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

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
                        ConverterUtil.fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setRefundStatus(refundStatus != null ? mapStatus(refundStatus) : null)
                .setInvoiceIds(ConverterUtil.merge(invoiceID, invoiceIDs))
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
