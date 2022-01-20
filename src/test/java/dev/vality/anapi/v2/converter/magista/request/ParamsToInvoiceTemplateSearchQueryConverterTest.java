package dev.vality.anapi.v2.converter.magista.request;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.magista.InvoiceTemplateSearchQuery;
import dev.vality.magista.InvoiceTemplateStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParamsToInvoiceTemplateSearchQueryConverterTest {

    private static final ParamsToInvoiceTemplateSearchQueryConverter converter =
            new ParamsToInvoiceTemplateSearchQueryConverter();

    @Test
    void convert() {
        InvoiceTemplateSearchQuery query = converter.convert("1",
                OffsetDateTime.MIN,
                OffsetDateTime.MAX,
                10,
                List.of("1", "2"),
                "created",
                "1",
                "test",
                "name",
                "sugar",
                OffsetDateTime.MAX);
        assertNotNull(query);
    }

    @Test
    void mapStatus() {

        for (InvoiceTemplateStatus status : InvoiceTemplateStatus.values()) {
            assertEquals(status, converter.mapStatus(status.name()));
        }

        assertThrows(BadRequestException.class, () -> converter.mapStatus("unexpected"));
    }
}