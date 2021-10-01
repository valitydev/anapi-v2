package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.RefundSearchQuery;
import com.rbkmoney.openapi.anapi_v2.model.RefundStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;
import static com.rbkmoney.magista.InvoicePaymentRefundStatus.*;

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
                .setRefundStatus(refundStatus != null ? getRefundStatus(refundStatus) : null)
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID)
                .setPaymentId(paymentID)
                .setRefundId(refundID);
    }

    private com.rbkmoney.magista.InvoicePaymentRefundStatus getRefundStatus(String refundStatus) {
        return switch (Enum.valueOf(RefundStatus.StatusEnum.class, refundStatus)) {
            case PENDING -> pending;
            case SUCCEEDED -> succeeded;
            case FAILED -> failed;
            default -> throw new BadRequestException(
                    String.format("Refund status %s cannot be processed", refundStatus));
        };
    }

}
