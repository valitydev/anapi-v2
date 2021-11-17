package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.Invoice;
import com.rbkmoney.anapi.v2.model.InvoiceLineTaxVAT;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.msgpack.Value;
import com.rbkmoney.magista.StatInvoice;
import com.rbkmoney.magista.StatInvoiceResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.rbkmoney.anapi.v2.model.Invoice.StatusEnum.*;
import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createSearchInvoiceAllResponse;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomInt;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomString;
import static org.junit.jupiter.api.Assertions.*;

class StatInvoiceToInvoiceConverterTest {

    private static final StatInvoiceToInvoiceConverter converter = new StatInvoiceToInvoiceConverter();

    @Test
    void convert() {
        StatInvoiceResponse magistaResponse = createSearchInvoiceAllResponse();
        StatInvoice magistaInvoice = magistaResponse.getInvoices().get(0);
        magistaInvoice.getCart().getLines().get(0).setMetadata(Map.of("TaxMode", Value.str("10%")))
                .setQuantity(randomInt(1, 10000))
                .setPrice(new Cash()
                        .setAmount(randomInt(0, 1000000)));
        Invoice result = converter.convert(magistaInvoice);
        var expectedLine = magistaInvoice.getCart().getLines().get(0);
        var actualLine = result.getCart().get(0);
        assertAll(
                () -> assertEquals(magistaInvoice.getAmount(), result.getAmount()),
                () -> assertEquals(magistaInvoice.getCreatedAt(), result.getCreatedAt().toString()),
                () -> assertEquals(magistaInvoice.getCurrencySymbolicCode(), result.getCurrency()),
                () -> assertEquals(magistaInvoice.getExternalId(), result.getExternalID()),
                () -> assertEquals(expectedLine.getPrice().getAmount(), actualLine.getPrice()),
                () -> assertEquals(expectedLine.getProduct(), actualLine.getProduct()),
                () -> assertEquals(expectedLine.getQuantity() * expectedLine.getPrice().getAmount(),
                        actualLine.getCost()),
                () -> assertEquals(expectedLine.getMetadata().get("TaxMode").getStr(),
                        ((InvoiceLineTaxVAT) actualLine.getTaxMode()).getRate().getValue()),
                () -> assertEquals(magistaInvoice.getDescription(), result.getDescription()),
                () -> assertEquals(magistaInvoice.getDue(), result.getDueDate().toString()),
                () -> assertEquals(magistaInvoice.getId(), result.getId()),
                () -> assertEquals(magistaInvoice.getProduct(), result.getProduct()),
                () -> assertEquals(magistaInvoice.getShopId(), result.getShopID())
        );
    }

    @Test
    void mapStatusInfo() {
        Invoice invoice = new Invoice();
        InvoiceStatus status = InvoiceStatus.fulfilled(new InvoiceFulfilled()
                .setDetails(randomString(10)));
        converter.mapStatusInfo(invoice, status);
        assertEquals(FULFILLED, invoice.getStatus());
        assertEquals(status.getFulfilled().getDetails(), invoice.getReason());

        invoice = new Invoice();
        status = InvoiceStatus.paid(new InvoicePaid());
        converter.mapStatusInfo(invoice, status);
        assertEquals(PAID, invoice.getStatus());

        invoice = new Invoice();
        status = InvoiceStatus.unpaid(new InvoiceUnpaid());
        converter.mapStatusInfo(invoice, status);
        assertEquals(UNPAID, invoice.getStatus());

        invoice = new Invoice();
        status = InvoiceStatus.cancelled(new InvoiceCancelled()
                .setDetails(randomString(10)));
        converter.mapStatusInfo(invoice, status);
        assertEquals(CANCELLED, invoice.getStatus());
        assertEquals(status.getCancelled().getDetails(), invoice.getReason());

        assertThrows(IllegalArgumentException.class, () -> converter.mapStatusInfo(new Invoice(), new InvoiceStatus()));
    }

    @Test
    void mapTaxMode() {
        assertNull(converter.mapTaxMode(Map.of()));
        String taxMode = "10%";
        Map<String, Value> metadata = new HashMap<>();
        metadata.put("TaxMode", Value.str(taxMode));
        assertEquals(taxMode, ((InvoiceLineTaxVAT)converter.mapTaxMode(metadata)).getRate().getValue());
    }
}