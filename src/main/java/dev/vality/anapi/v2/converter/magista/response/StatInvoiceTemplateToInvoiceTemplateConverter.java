package dev.vality.anapi.v2.converter.magista.response;

import dev.vality.anapi.v2.model.*;
import dev.vality.damsel.msgpack.Value;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.magista.InvoiceTemplateStatus;
import dev.vality.magista.StatInvoiceTemplate;
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

    protected InvoiceTemplateDetails mapDetails(dev.vality.damsel.domain.InvoiceTemplateDetails details) {
        try {
            var field = dev.vality.damsel.domain.InvoiceTemplateDetails._Fields.findByName(
                    details.getSetField().getFieldName());
            return switch (field) {
                case CART -> new InvoiceTemplateCart().cart(
                                details.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                                                .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                                                .price(invoiceLine.getPrice().getAmount())
                                                .product(invoiceLine.getProduct())
                                                .taxMode(mapTaxMode(invoiceLine.getMetadata()))
                                                .quantity((long) invoiceLine.getQuantity()))
                                        .collect(Collectors.toList()))
                        .templateType("invoiceTemplateMultiLine");
                case PRODUCT -> new InvoiceTemplateProduct()
                        .product(details.getProduct().getProduct())
                        .price(mapPrice(details.getProduct().getPrice()))
                        .metadata(details.getFieldMetaData())
                        .templateType("invoiceTemplateSingleLine");
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("InvoiceTemplateDetails %s cannot be processed", details));
        }

    }

    protected InvoiceTemplateProductPrice mapPrice(dev.vality.damsel.domain.InvoiceTemplateProductPrice price) {
        try {
            var field = dev.vality.damsel.domain.InvoiceTemplateProductPrice._Fields.findByName(
                    price.getSetField().getFieldName());
            return switch (field) {
                case FIXED -> mapCash(price.getFixed()).costType("fixed");
                case RANGE -> mapCashRange(price.getRange()).costType("range");
                case UNLIM -> new CashUnlim().costType("unlim");
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("InvoiceTemplateProductPrice %s cannot be processed", price));
        }
    }

    protected Cash mapCash(dev.vality.damsel.domain.Cash cash) {
        return new Cash()
                .amount(cash.getAmount())
                .currency(cash.getCurrency().getSymbolicCode());
    }

    protected CashRange mapCashRange(dev.vality.damsel.domain.CashRange cash) {
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
