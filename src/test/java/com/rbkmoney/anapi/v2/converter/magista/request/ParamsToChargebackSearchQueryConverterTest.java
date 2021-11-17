package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.magista.ChargebackSearchQuery;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamsToChargebackSearchQueryConverterTest {

    private static final ParamsToChargebackSearchQueryConverter converter =
            new ParamsToChargebackSearchQueryConverter();

    @Test
    void convert() {
        ChargebackSearchQuery query = converter.convert("1",
                OffsetDateTime.MIN,
                OffsetDateTime.MAX,
                10,
                List.of("1", "2"),
                "1",
                "2",
                "3",
                List.of("pending", "accepted"),
                List.of("pre_arbitration"),
                List.of("fraud"),
                "test"
        );
        assertNotNull(query);
    }

    @Test
    void mapStage() {
        assertTrue(converter.mapStage("chargeback").isSetChargeback());
        assertTrue(converter.mapStage("pre_arbitration").isSetPreArbitration());
        assertTrue(converter.mapStage("arbitration").isSetArbitration());
        assertThrows(BadRequestException.class, () -> converter.mapStage("unexpected"));
    }

    @Test
    void mapStatus() {
        assertTrue(converter.mapStatus("pending").isSetPending());
        assertTrue(converter.mapStatus("accepted").isSetAccepted());
        assertTrue(converter.mapStatus("rejected").isSetRejected());
        assertTrue(converter.mapStatus("cancelled").isSetCancelled());
        assertThrows(BadRequestException.class, () -> converter.mapStatus("unexpected"));
    }

    @Test
    void mapCategory() {
        assertTrue(converter.mapCategory("fraud").isSetFraud());
        assertTrue(converter.mapCategory("dispute").isSetDispute());
        assertTrue(converter.mapCategory("authorisation").isSetAuthorisation());
        assertTrue(converter.mapCategory("processing_error").isSetProcessingError());
        assertThrows(BadRequestException.class, () -> converter.mapCategory("unexpected"));

    }
}