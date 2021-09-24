package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoiceTemplateStatus;
import com.rbkmoney.magista.StatInvoiceTemplate;
import com.rbkmoney.openapi.anapi_v2.model.*;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatInvoiceTemplateToInvoiceTemplateConverter {
    public InvoiceTemplate convert(StatInvoiceTemplate statInvoiceTemplate) {
        return new InvoiceTemplate()
                .description(statInvoiceTemplate.getDescription())
                .invoiceTemplateId(statInvoiceTemplate.getInvoiceTemplateId())
                .invoiceTemplateStatus(mapStatus(statInvoiceTemplate.getInvoiceTemplateStatus()))
                .invoiceTemplateCreatedAt(TypeUtil.stringToInstant(statInvoiceTemplate.getInvoiceTemplateCreatedAt())
                        .atOffset(ZoneOffset.UTC))
                .invoiceValidUntil(
                        TypeUtil.stringToInstant(statInvoiceTemplate.getInvoiceValidUntil()).atOffset(ZoneOffset.UTC))
                .details(mapDetails(statInvoiceTemplate.getDetails()))
                .eventCreatedAt(
                        TypeUtil.stringToInstant(statInvoiceTemplate.getEventCreatedAt()).atOffset(ZoneOffset.UTC))
                .name(statInvoiceTemplate.getName())
                .product(statInvoiceTemplate.getProduct())
                .shopID(statInvoiceTemplate.getShopId());
    }

    private InvoiceTemplateDetails mapDetails(com.rbkmoney.damsel.domain.InvoiceTemplateDetails details) {

        return new InvoiceTemplateDetails().cart(details.getCart() != null
                        ? details.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                                .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                                .price(invoiceLine.getPrice().getAmount())
                                .product(invoiceLine.getProduct())
                                .taxMode(getTaxMode(invoiceLine.getMetadata())))
                        .collect(Collectors.toList()) : null)
                .product(new InvoiceTemplateProduct()
                        .product(details.getProduct().getProduct())
                        .price(mapPrice(details.getProduct().getPrice())))
                .templateType(InvoiceTemplateDetails.TemplateTypeEnum.INVOICETEMPLATEMULTILINE);

    }

    private InvoiceTemplateProductPrice mapPrice(com.rbkmoney.damsel.domain.InvoiceTemplateProductPrice price) {
        InvoiceTemplateProductPrice result = new InvoiceTemplateProductPrice();
        if (price.isSetFixed()) {
            return result.costType(InvoiceTemplateProductPrice.CostTypeEnum.FIXED)
                    .fixed(mapCash(price.getFixed()));
        }

        if (price.isSetRange()) {
            return result.costType(InvoiceTemplateProductPrice.CostTypeEnum.RANGE)
                    .range(mapCashRange(price.getRange()));
        }

        if (price.isSetUnlim()) {
            return result.costType(InvoiceTemplateProductPrice.CostTypeEnum.UNLIM);
        }

        throw new IllegalArgumentException(
                String.format("InvoiceTemplateProductPrice %s cannot be processed", price));
    }

    private Cash mapCash(com.rbkmoney.damsel.domain.Cash cash) {
        return new Cash()
                .amount(cash.getAmount())
                .currency(cash.getCurrency().getSymbolicCode());
    }

    private CashRange mapCashRange(com.rbkmoney.damsel.domain.CashRange cash) {
        return new CashRange()
                .lowerBound(cash.getLower().getInclusive().getAmount())
                .upperBound(cash.getUpper().getInclusive().getAmount());
    }

    private InvoiceLineTaxMode getTaxMode(Map<String, Value> metadata) {
        Value taxMode = metadata.get("TaxMode");
        if (taxMode != null) {
            return new InvoiceLineTaxVAT()
                    .rate(InvoiceLineTaxVAT.RateEnum.fromValue(
                            taxMode.getStr()));
        }
        return null;
    }

    private InvoiceTemplate.InvoiceTemplateStatusEnum mapStatus(InvoiceTemplateStatus invoiceTemplateStatus) {
        return switch (invoiceTemplateStatus) {
            case created -> InvoiceTemplate.InvoiceTemplateStatusEnum.CREATED;
            case deleted -> InvoiceTemplate.InvoiceTemplateStatusEnum.DELETED;
            default -> throw new IllegalArgumentException(
                    String.format("InvoiceTemplate status %s cannot be processed", invoiceTemplateStatus.name()));
        };
    }
}
