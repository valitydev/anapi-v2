package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.anapi.v2.util.OpenApiUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatInvoice;
import com.rbkmoney.openapi.anapi_v2.model.Invoice;
import com.rbkmoney.openapi.anapi_v2.model.InvoiceLine;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Component
public class StatInvoiceToInvoiceConverter {

    public Invoice convert(StatInvoice invoice) {
        return new Invoice()
                .amount(invoice.getAmount())
                .createdAt(TypeUtil.stringToInstant(invoice.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(invoice.getCurrencySymbolicCode())
                .externalID(invoice.getExternalId())
                .cart(invoice.getCart() != null
                        ? invoice.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                                .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                                .price(invoiceLine.getPrice().getAmount())
                                .product(invoiceLine.getProduct())
                        //.getTaxMode()
                ).collect(Collectors.toList()) : null)
                .description(invoice.getDescription())
                .dueDate(TypeUtil.stringToInstant(invoice.getDue()).atOffset(ZoneOffset.UTC))
                .id(invoice.getId())
                .product(invoice.getProduct())
                //.reason()
                .shopID(invoice.getShopId())
                .status(OpenApiUtil.mapToInvoiceStatus(invoice.getStatus()));
    }

}
