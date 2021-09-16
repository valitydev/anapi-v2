package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.magista.InvoiceSearchQuery;
import com.rbkmoney.magista.PaymentParams;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.mapStatus;

@Component
public class ParamsToInvoiceSearchQueryConverter {

    public InvoiceSearchQuery convert(String partyID,
                                      OffsetDateTime fromTime,
                                      OffsetDateTime toTime,
                                      Integer limit,
                                      String shopID,
                                      List<String> shopIDs,
                                      String paymentInstitutionRealm,
                                      List<String> invoiceIDs,
                                      String invoiceStatus,
                                      String invoiceID,
                                      String externalID,
                                      Long invoiceAmountFrom,
                                      Long invoiceAmountTo,
                                      List<String> excludedShops,
                                      String continuationToken) {
        return new InvoiceSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs),
                                continuationToken))
                .setPaymentParams(
                        new PaymentParams()
                                .setPaymentAmountFrom(invoiceAmountFrom)
                                .setPaymentAmountTo(invoiceAmountTo)
                                .setPaymentStatus(invoiceStatus != null ? mapStatus(invoiceStatus) : null)
                )
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID);
    }
}
