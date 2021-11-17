package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.Invoice;
import com.rbkmoney.anapi.v2.model.InvoiceLine;
import com.rbkmoney.anapi.v2.model.InvoiceLineTaxMode;
import com.rbkmoney.anapi.v2.model.InvoiceLineTaxVAT;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatInvoice;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatInvoiceToInvoiceConverter {

    public Invoice convert(StatInvoice invoice) {
        Invoice result = new Invoice()
                .amount(invoice.getAmount())
                .createdAt(TypeUtil.stringToInstant(invoice.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(invoice.getCurrencySymbolicCode())
                .externalID(invoice.getExternalId())
                .cart(invoice.isSetCart()
                        ? invoice.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                        .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                        .price(invoiceLine.getPrice().getAmount())
                        .product(invoiceLine.getProduct())
                        .taxMode(mapTaxMode(invoiceLine.getMetadata()))
                ).collect(Collectors.toList()) : null)
                .description(invoice.getDescription())
                .dueDate(TypeUtil.stringToInstant(invoice.getDue()).atOffset(ZoneOffset.UTC))
                .id(invoice.getId())
                .product(invoice.getProduct())
                .shopID(invoice.getShopId());

        mapStatusInfo(result, invoice.getStatus());
        return result;
    }

    protected void mapStatusInfo(Invoice invoice, InvoiceStatus status) {
        try {
            var field = InvoiceStatus._Fields.findByName(status.getSetField().getFieldName());
            switch (field) {
                case FULFILLED -> {
                    invoice.setReason(status.getFulfilled().getDetails());
                    invoice.setStatus(Invoice.StatusEnum.FULFILLED);
                }
                case CANCELLED -> {
                    invoice.setReason(status.getCancelled().getDetails());
                    invoice.setStatus(Invoice.StatusEnum.CANCELLED);
                }
                case PAID -> invoice.setStatus(Invoice.StatusEnum.PAID);
                case UNPAID -> invoice.setStatus(Invoice.StatusEnum.UNPAID);
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invoice status %s cannot be processed", status));
        }
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

}
