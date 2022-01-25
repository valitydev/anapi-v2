package dev.vality.anapi.v2.converter.magista.response;

import dev.vality.anapi.v2.model.InvoiceLineTaxVAT;
import dev.vality.anapi.v2.model.InvoiceTemplate;
import dev.vality.anapi.v2.model.InvoiceTemplateCart;
import dev.vality.anapi.v2.testutil.DamselUtil;
import dev.vality.anapi.v2.testutil.MagistaUtil;
import dev.vality.anapi.v2.testutil.RandomUtil;
import dev.vality.damsel.domain.*;
import dev.vality.damsel.msgpack.Value;
import dev.vality.magista.InvoiceTemplateStatus;
import dev.vality.magista.StatInvoiceTemplate;
import dev.vality.magista.StatInvoiceTemplateResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatInvoiceTemplateToInvoiceTemplateConverterTest {

    private static final StatInvoiceTemplateToInvoiceTemplateConverter converter =
            new StatInvoiceTemplateToInvoiceTemplateConverter();

    private static CashRange createCashRange() {
        return new CashRange()
                .setLower(CashBound
                        .inclusive(new Cash()
                                .setAmount(RandomUtil.randomInt(1, 1000000))
                                .setCurrency(new CurrencyRef().setSymbolicCode("RUB"))))
                .setUpper(CashBound
                        .inclusive(new Cash()
                                .setAmount(RandomUtil.randomInt(1, 1000000))));
    }

    @Test
    void convert() {
        StatInvoiceTemplateResponse magistaResponse = MagistaUtil.createSearchInvoiceTemplateAllResponse();
        StatInvoiceTemplate magistaInvoiceTemplate = magistaResponse.getInvoiceTemplates().get(0);
        InvoiceTemplate result = converter.convert(magistaInvoiceTemplate);
        assertAll(
                () -> assertEquals(magistaInvoiceTemplate.getDescription(), result.getDescription()),
                () -> assertEquals(magistaInvoiceTemplate.getInvoiceTemplateId(), result.getInvoiceTemplateId()),
                () -> assertEquals(magistaInvoiceTemplate.getInvoiceTemplateCreatedAt(),
                        result.getInvoiceTemplateCreatedAt().toString()),
                () -> assertEquals(magistaInvoiceTemplate.getInvoiceValidUntil(),
                        result.getInvoiceValidUntil().toString()),
                () -> assertEquals(magistaInvoiceTemplate.getEventCreatedAt(),
                        result.getEventCreatedAt().toString()),
                () -> assertEquals(magistaInvoiceTemplate.getName(),
                        result.getName()),
                () -> assertEquals(magistaInvoiceTemplate.getProduct(),
                        result.getProduct()),
                () -> assertEquals(magistaInvoiceTemplate.getShopId(),
                        result.getShopID())
        );
    }

    @Test
    void mapDetails() {
        InvoiceTemplateDetails details = InvoiceTemplateDetails.cart(
                new InvoiceCart()
                        .setLines(List.of(
                                        new InvoiceLine()
                                                .setPrice(DamselUtil.fillRequiredTBaseObject(new Cash(), Cash.class))
                                                .setProduct(RandomUtil.randomString(10))
                                                .setQuantity(RandomUtil.randomInt(1, 1000))
                                                .setMetadata(new HashMap<>())
                                )
                        ));

        var actual = ((InvoiceTemplateCart) converter.mapDetails(details)).getCart().get(0);
        var expected = details.getCart().getLines().get(0);

        assertAll(
                () -> assertEquals(expected.getPrice().getAmount(), actual.getPrice()),
                () -> assertEquals(expected.getPrice().getAmount() * expected.getQuantity(), actual.getCost()),
                () -> assertEquals(expected.getProduct(), actual.getProduct()),
                () -> assertEquals(expected.getQuantity(), actual.getQuantity())
        );


        details = InvoiceTemplateDetails.product(
                new InvoiceTemplateProduct()
                        .setMetadata(new HashMap<>())
                        .setPrice(InvoiceTemplateProductPrice.unlim(new InvoiceTemplateCostUnlimited()))
                        .setProduct(RandomUtil.randomString(10)));

        var actualProduct = (dev.vality.anapi.v2.model.InvoiceTemplateProduct) converter.mapDetails(details);
        var expectedProduct = details.getProduct();

        assertAll(
                () -> assertEquals("unlim", actualProduct.getPrice().getCostType()),
                () -> assertEquals(expectedProduct.getProduct(), actualProduct.getProduct())
        );

        assertThrows(IllegalArgumentException.class, () -> converter.mapDetails(new InvoiceTemplateDetails()));

    }

    @Test
    void mapPrice() {
        assertEquals("fixed",
                converter.mapPrice(InvoiceTemplateProductPrice.fixed(
                        DamselUtil.fillRequiredTBaseObject(new Cash(), Cash.class)))
                        .getCostType());
        assertEquals("range",
                converter.mapPrice(
                                InvoiceTemplateProductPrice.range(createCashRange()))
                        .getCostType());
        assertEquals("unlim",
                converter.mapPrice(InvoiceTemplateProductPrice.unlim(
                                DamselUtil.fillRequiredTBaseObject(new InvoiceTemplateCostUnlimited(),
                                        InvoiceTemplateCostUnlimited.class)))
                        .getCostType());

        assertThrows(IllegalArgumentException.class, () -> converter.mapPrice(new InvoiceTemplateProductPrice()));
    }

    @Test
    void mapCash() {
        Cash cash = new Cash()
                .setAmount(RandomUtil.randomInt(1, 10000))
                .setCurrency(new CurrencyRef().setSymbolicCode("RUB"));
        var actualCash = converter.mapCash(cash);
        assertAll(
                () -> assertEquals(cash.getCurrency().getSymbolicCode(), actualCash.getCurrency()),
                () -> assertEquals(cash.getAmount(), actualCash.getAmount())
        );
    }

    @Test
    void mapCashRange() {
        CashRange range = createCashRange();
        var lowerInclusive = range.getLower().getInclusive();
        var upperInclusive = range.getUpper().getInclusive();
        var actualRange = converter.mapCashRange(range);
        assertAll(
                () -> assertEquals(lowerInclusive.getAmount(), actualRange.getLowerBound()),
                () -> assertEquals(lowerInclusive.getCurrency().getSymbolicCode(), actualRange.getCurrency()),
                () -> assertEquals(upperInclusive.getAmount(), actualRange.getUpperBound())
        );
    }

    @Test
    void mapTaxMode() {
        assertNull(converter.mapTaxMode(Map.of()));
        String taxMode = "10%";
        Map<String, Value> metadata = new HashMap<>();
        metadata.put("TaxMode", Value.str(taxMode));
        assertEquals(taxMode, ((InvoiceLineTaxVAT) converter.mapTaxMode(metadata)).getRate().getValue());
    }

    @Test
    void mapStatus() {
        for (InvoiceTemplateStatus status : InvoiceTemplateStatus.values()) {
            assertEquals(InvoiceTemplate.InvoiceTemplateStatusEnum.fromValue(status.name()),
                    converter.mapStatus(status));
        }
    }
}