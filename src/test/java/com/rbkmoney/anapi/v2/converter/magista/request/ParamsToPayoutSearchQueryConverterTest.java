package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.PayoutSearchQuery;
import com.rbkmoney.magista.PayoutToolType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamsToPayoutSearchQueryConverterTest {

    private static final ParamsToPayoutSearchQueryConverter converter = new ParamsToPayoutSearchQueryConverter();

    @Test
    void convert() {
        PayoutSearchQuery query = converter.convert("1",
                OffsetDateTime.MIN,
                OffsetDateTime.MAX,
                10,
                List.of("1", "2", "3"),
                "1",
                "Wallet",
                "test");
        assertNotNull(query);
    }

    @Test
    void mapPayoutToolType() {
        assertEquals(PayoutToolType.payout_account, converter.mapPayoutToolType("PayoutAccount"));
        assertEquals(PayoutToolType.wallet, converter.mapPayoutToolType("Wallet"));
        assertEquals(PayoutToolType.payment_institution_account,
                converter.mapPayoutToolType("PaymentInstitutionAccount"));
        assertThrows(BadRequestException.class, () -> converter.mapPayoutToolType("unexpected"));
    }
}