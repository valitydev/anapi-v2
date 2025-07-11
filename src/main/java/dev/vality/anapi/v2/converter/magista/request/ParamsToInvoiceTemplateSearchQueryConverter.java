package dev.vality.anapi.v2.converter.magista.request;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.util.ConverterUtil;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.magista.InvoiceTemplateSearchQuery;
import dev.vality.magista.InvoiceTemplateStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

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
                        ConverterUtil.fillCommonParams(fromTime, toTime, limit, partyID, shopIDs,
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
