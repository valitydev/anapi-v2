package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.damsel.domain.InvoiceCart;
import com.rbkmoney.damsel.domain.InvoiceLine;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.magista.*;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.thrift.TBase;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MagistaUtil {

    private static final MockTBaseProcessor mockRequiredTBaseProcessor;
    private static final MockTBaseProcessor mockFullTBaseProcessor;

    static {
        mockRequiredTBaseProcessor = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 15, 1);
        Map.Entry<FieldHandler, String[]> timeFields = Map.entry(
                structHandler -> structHandler.value(Instant.now().toString()),
                new String[] {"created_at", "at", "due"}
        );
        mockRequiredTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());

        mockFullTBaseProcessor = new MockTBaseProcessor(MockMode.ALL, 15, 1);
        mockFullTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());
    }

    public static StatPaymentResponse createSearchPaymentRequiredResponse() {
        return fillRequiredTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);
    }

    public static StatPaymentResponse createSearchPaymentAllResponse() {
        var payment = fillAllTBaseObject(new StatPayment(), StatPayment.class);
        var cart = fillAllTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillAllTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var instant = fillAllTBaseObject(new InvoicePaymentFlowInstant(), InvoicePaymentFlowInstant.class);
        var locationInfo = fillAllTBaseObject(new LocationInfo(), LocationInfo.class);
        var response = fillAllTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);

        response.setPayments(
                List.of(payment
                        .setStatus(InvoicePaymentStatus
                                .pending(new InvoicePaymentPending()))
                        .setCart(cart.setLines(List.of(line)))
                        .setFlow(InvoicePaymentFlow
                                .instant(instant))
                        .setLocationInfo(locationInfo)));

        return response;
    }

    @SneakyThrows
    public static <T extends TBase> T fillRequiredTBaseObject(T tbase, Class<T> type) {
        return mockRequiredTBaseProcessor.process(tbase, new TBaseHandler<>(type));
    }

    @SneakyThrows
    public static <T extends TBase> T fillAllTBaseObject(T tbase, Class<T> type) {
        return mockFullTBaseProcessor.process(tbase, new TBaseHandler<>(type));
    }
}
