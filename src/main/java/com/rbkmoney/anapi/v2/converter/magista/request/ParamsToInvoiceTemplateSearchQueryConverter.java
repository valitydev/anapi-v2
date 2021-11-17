package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoiceTemplateSearchQuery;
import com.rbkmoney.magista.InvoiceTemplateStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;

@Component
public class ParamsToInvoiceTemplateSearchQueryConverter {
    public InvoiceTemplateSearchQuery convert(String partyID,
                                              OffsetDateTime fromTime,
                                              OffsetDateTime toTime,
                                              Integer limit,
                                              List<String> shopIDs,
                                              String invoiceTemplateStatus,
                                              String invoiceTemplateID,
                                              String continuationToken,
                                              String name,
                                              String product,
                                              OffsetDateTime invoiceValidUntil) {
        return new InvoiceTemplateSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs,
                                continuationToken))
                .setInvoiceTemplateId(invoiceTemplateID)
                .setInvoiceTemplateStatus(invoiceTemplateStatus != null
                        ? mapStatus(invoiceTemplateStatus) : null)
                .setName(name)
                .setProduct(product)
                .setInvoiceValidUntil(invoiceValidUntil != null
                        ? TypeUtil.temporalToString(invoiceValidUntil) : null);
    }

    protected InvoiceTemplateStatus mapStatus(String status) {
        try {
            return InvoiceTemplateStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("InvoiceTemplate status %s cannot be processed", status));
        }
    }
}
