package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;

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
        //TODO: Mapping for paymentInstitutionRealm, excludedShops
        return new InvoiceSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs),
                                continuationToken))
                .setPaymentParams(
                        new PaymentParams()
                                .setPaymentAmountFrom(invoiceAmountFrom != null ? invoiceAmountFrom : 0L)
                                .setPaymentAmountTo(invoiceAmountTo != null ? invoiceAmountTo : 0L)
                )
                .setInvoiceStatus(invoiceStatus != null ? mapStatus(invoiceStatus) : null)
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID);
    }

    private InvoiceStatus mapStatus(String statusParam) {
        var status = Enum.valueOf(com.rbkmoney.openapi.anapi_v2.model.InvoiceStatus.StatusEnum.class, statusParam);
        return switch (status) {
            case CANCELLED -> InvoiceStatus.cancelled;
            case FULFILLED -> InvoiceStatus.fulfilled;
            case PAID -> InvoiceStatus.paid;
            case UNPAID -> InvoiceStatus.unpaid;
            default -> throw new BadRequestException(
                    String.format("Invoice status %s cannot be processed", status));
        };
    }
}
