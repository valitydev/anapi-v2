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
                .invoiceTemplateCreatedAt(statInvoiceTemplate.getInvoiceTemplateCreatedAt() != null
                        ? TypeUtil.stringToInstant(statInvoiceTemplate.getInvoiceTemplateCreatedAt())
                        .atOffset(ZoneOffset.UTC) : null)
                .invoiceValidUntil(
                        TypeUtil.stringToInstant(statInvoiceTemplate.getInvoiceValidUntil()).atOffset(ZoneOffset.UTC))
                .details(mapDetails(statInvoiceTemplate.getDetails()))
                .eventCreatedAt(
                        TypeUtil.stringToInstant(statInvoiceTemplate.getEventCreatedAt()).atOffset(ZoneOffset.UTC))
                .name(statInvoiceTemplate.getName())
                .product(statInvoiceTemplate.getProduct())
                .shopID(statInvoiceTemplate.getShopId());
    }

    protected InvoiceTemplateDetails mapDetails(com.rbkmoney.damsel.domain.InvoiceTemplateDetails details) {
        if (details.isSetCart()) {
            return new InvoiceTemplateCart().cart(
                            details.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                                            .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                                            .price(invoiceLine.getPrice().getAmount())
                                            .product(invoiceLine.getProduct())
                                            .taxMode(mapTaxMode(invoiceLine.getMetadata()))
                                            .quantity((long) invoiceLine.getQuantity()))
                                    .collect(Collectors.toList()))
                    .templateType("invoiceTemplateMultiLine");
        }

        if (details.isSetProduct()) {
            return new InvoiceTemplateProduct()
                    .product(details.getProduct().getProduct())
                    .price(mapPrice(details.getProduct().getPrice()))
                    .metadata(details.getFieldMetaData())
                    .templateType("invoiceTemplateSingleLine");
        }

        throw new IllegalArgumentException(
                String.format("InvoiceTemplateDetails %s cannot be processed", details));

    }

    protected InvoiceTemplateProductPrice mapPrice(com.rbkmoney.damsel.domain.InvoiceTemplateProductPrice price) {
        if (price.isSetFixed()) {
            return mapCash(price.getFixed())
                    .costType("fixed");
        }

        if (price.isSetRange()) {
            return mapCashRange(price.getRange())
                    .costType("range");
        }

        if (price.isSetUnlim()) {
            return new CashUnlim()
                    .costType("unlim");
        }

        throw new IllegalArgumentException(
                String.format("InvoiceTemplateProductPrice %s cannot be processed", price));
    }

    protected Cash mapCash(com.rbkmoney.damsel.domain.Cash cash) {
        return new Cash()
                .amount(cash.getAmount())
                .currency(cash.getCurrency().getSymbolicCode());
    }

    protected CashRange mapCashRange(com.rbkmoney.damsel.domain.CashRange cash) {
        return new CashRange()
                .lowerBound(cash.getLower().getInclusive().getAmount())
                .upperBound(cash.getUpper().getInclusive().getAmount())
                .currency(cash.getLower().getInclusive().getCurrency().getSymbolicCode());
    }

    protected InvoiceLineTaxMode mapTaxMode(Map<String, Value> metadata) {
        Value taxMode = metadata.get("TaxMode");
        if (taxMode != null) {
            return new InvoiceLineTaxVAT()
                    .rate(InvoiceLineTaxVAT.RateEnum.fromValue(
                            taxMode.getStr()));
        }
        return null;
    }

    protected InvoiceTemplate.InvoiceTemplateStatusEnum mapStatus(InvoiceTemplateStatus invoiceTemplateStatus) {
        try {
            return InvoiceTemplate.InvoiceTemplateStatusEnum.fromValue(invoiceTemplateStatus.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("InvoiceTemplate status %s cannot be processed", invoiceTemplateStatus.name()));
        }
    }
}
