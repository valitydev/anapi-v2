package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundFailed;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundPending;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundSucceeded;
import com.rbkmoney.magista.RefundSearchQuery;
import com.rbkmoney.openapi.anapi_v2.model.RefundStatus;
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
                                     String shopID,
                                     List<String> shopIDs,
                                     String paymentInstitutionRealm,
                                     Integer offset,
                                     List<String> invoiceIDs,
                                     String invoiceID,
                                     String paymentID,
                                     String refundID,
                                     String externalID,
                                     String refundStatus,
                                     List<String> excludedShops,
                                     String continuationToken) {
        return new RefundSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs),
                                continuationToken))
                .setRefundStatus(refundStatus != null ? getRefundStatus(refundStatus) : null)
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID)
                .setPaymentId(paymentID)
                .setRefundId(refundID);
    }

    private InvoicePaymentRefundStatus getRefundStatus(String refundStatus) {
        var invoicePaymentRefundStatus = new InvoicePaymentRefundStatus();
        switch (Enum.valueOf(RefundStatus.StatusEnum.class, refundStatus)) {
            case PENDING -> invoicePaymentRefundStatus.setPending(new InvoicePaymentRefundPending());
            case SUCCEEDED -> invoicePaymentRefundStatus.setSucceeded(new InvoicePaymentRefundSucceeded());
            case FAILED -> invoicePaymentRefundStatus.setFailed(new InvoicePaymentRefundFailed());
            default -> throw new BadRequestException(
                    String.format("Refund status %s cannot be processed", refundStatus));
        }
        return invoicePaymentRefundStatus;
    }

}
