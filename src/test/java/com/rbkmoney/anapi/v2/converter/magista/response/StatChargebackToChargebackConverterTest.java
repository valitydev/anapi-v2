package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.Chargeback;
import com.rbkmoney.damsel.base.Content;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.StatChargeback;
import com.rbkmoney.magista.StatChargebackResponse;
import org.junit.jupiter.api.Test;

import static com.rbkmoney.anapi.v2.model.ChargebackCategory.*;
import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createSearchChargebackAllResponse;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomBytes;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomString;
import static org.junit.jupiter.api.Assertions.*;

class StatChargebackToChargebackConverterTest {

    private static final StatChargebackToChargebackConverter converter = new StatChargebackToChargebackConverter();

    @Test
    void convert() {
        StatChargebackResponse magistaResponse = createSearchChargebackAllResponse();
        StatChargeback magistaChargeback = magistaResponse.getChargebacks().get(0);
        magistaChargeback.setContent(new Content()
                .setType(randomString(10))
                .setData(randomBytes(10)));
        Chargeback result = converter.convert(magistaChargeback);
        assertAll(
                () -> assertEquals(magistaChargeback.getAmount(), result.getBodyAmount()),
                () -> assertEquals(magistaChargeback.getCreatedAt(), result.getCreatedAt().toString()),
                () -> assertEquals(magistaChargeback.getChargebackId(), result.getChargebackId()),
                () -> assertEquals(magistaChargeback.getChargebackReason().getCode(),
                        result.getChargebackReason().getCode()),
                () -> assertArrayEquals(magistaChargeback.getContent().getData(), result.getContent().getData()),
                () -> assertEquals(magistaChargeback.getContent().getType(), result.getContent().getType()),
                () -> assertEquals(magistaChargeback.getCurrencyCode().getSymbolicCode(), result.getBodyCurrency())
        );
    }

    @Test
    void mapCategory() {
        assertAll(
                () -> assertEquals(AUTHORISATION, converter.mapCategory(InvoicePaymentChargebackCategory.authorisation(
                        new InvoicePaymentChargebackCategoryAuthorisation()))),
                () -> assertEquals(DISPUTE, converter.mapCategory(
                        InvoicePaymentChargebackCategory.dispute(new InvoicePaymentChargebackCategoryDispute()))),
                () -> assertEquals(FRAUD, converter.mapCategory(
                        InvoicePaymentChargebackCategory.fraud(new InvoicePaymentChargebackCategoryFraud()))),
                () -> assertEquals(PROCESSING_ERROR, converter.mapCategory(
                        InvoicePaymentChargebackCategory.processing_error(
                                new InvoicePaymentChargebackCategoryProcessingError()))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> converter.mapCategory(new InvoicePaymentChargebackCategory()))
        );
    }
}