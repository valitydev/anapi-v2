package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.magista.RefundSearchQuery;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.CommonUtil.merge;
import static com.rbkmoney.anapi.v2.util.DamselUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.DamselUtil.getRefundStatus;

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

}
