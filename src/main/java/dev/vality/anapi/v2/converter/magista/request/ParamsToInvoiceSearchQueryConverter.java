package dev.vality.anapi.v2.converter.magista.request;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.util.ConverterUtil;
import dev.vality.magista.InvoiceSearchQuery;
import dev.vality.magista.InvoiceStatus;
import dev.vality.magista.PaymentParams;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class ParamsToInvoiceSearchQueryConverter {

    public InvoiceSearchQuery convert(String partyID,
                                      OffsetDateTime fromTime,
                                      OffsetDateTime toTime,
                                      Integer limit,
                                      List<String> shopIDs,
                                      List<String> invoiceIDs,
                                      String invoiceStatus,
                                      String invoiceID,
                                      String externalID,
                                      Long invoiceAmountFrom,
                                      Long invoiceAmountTo,
                                      String continuationToken) {
        return new InvoiceSearchQuery()
                .setCommonSearchQueryParams(
                        ConverterUtil.fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setPaymentParams(
                        mapPaymentParams(invoiceAmountFrom, invoiceAmountTo)
                )
                .setInvoiceStatus(invoiceStatus != null ? mapStatus(invoiceStatus) : null)
                .setInvoiceIds(ConverterUtil.merge(invoiceID, invoiceIDs))
                .setExternalId(externalID);
    }

    protected PaymentParams mapPaymentParams(Long invoiceAmountFrom, Long invoiceAmountTo) {
        var params = new PaymentParams();
        if (invoiceAmountFrom != null) {
            params.setPaymentAmountFrom(invoiceAmountFrom);
        }
        if (invoiceAmountTo != null) {
            params.setPaymentAmountTo(invoiceAmountTo);
        }
        return params;
    }

    protected InvoiceStatus mapStatus(String status) {
        try {
            return InvoiceStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Invoice status %s cannot be processed", status));
        }
    }
}
