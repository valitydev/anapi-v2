package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.magista.InvoicePaymentFlow;
import com.rbkmoney.magista.InvoicePaymentFlowInstant;
import com.rbkmoney.magista.InvoicePaymentPending;
import com.rbkmoney.magista.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.InvoicePaymentStatus;
import com.rbkmoney.magista.InvoiceStatus;
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

    public static StatChargebackResponse createSearchChargebackRequiredResponse() {
        return fillRequiredTBaseObject(new StatChargebackResponse(), StatChargebackResponse.class);
    }

    public static StatPaymentResponse createSearchPaymentAllResponse() {
        var payment = fillAllTBaseObject(new StatPayment(), StatPayment.class);
        var cart = fillAllTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillAllTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var instant = fillAllTBaseObject(new InvoicePaymentFlowInstant(), InvoicePaymentFlowInstant.class);
        var locationInfo = fillAllTBaseObject(new LocationInfo(), LocationInfo.class);
        var response = fillAllTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);

        return response.setPayments(
                List.of(payment
                        .setStatus(InvoicePaymentStatus
                                .pending(new InvoicePaymentPending()))
                        .setCart(cart.setLines(List.of(line)))
                        .setFlow(InvoicePaymentFlow
                                .instant(instant))
                        .setLocationInfo(locationInfo)));
    }

    public static StatChargebackResponse createSearchChargebackAllResponse() {
        var chargeback = fillAllTBaseObject(new StatChargeback(), StatChargeback.class);
        var stage = fillAllTBaseObject(new InvoicePaymentChargebackStage(), InvoicePaymentChargebackStage.class);
        var reason = fillAllTBaseObject(new InvoicePaymentChargebackReason(), InvoicePaymentChargebackReason.class);
        var status = fillAllTBaseObject(new InvoicePaymentChargebackStatus(), InvoicePaymentChargebackStatus.class);
        var response = fillAllTBaseObject(new StatChargebackResponse(), StatChargebackResponse.class);

        return response.setChargebacks(
                List.of(chargeback
                        .setStage(stage)
                        .setChargebackReason(reason)
                        .setChargebackStatus(status))
        );
    }

    public static StatRefundResponse createSearchRefundRequiredResponse() {
        return fillRequiredTBaseObject(new StatRefundResponse(), StatRefundResponse.class);
    }

    public static StatRefundResponse createsearchRefundAllResponse() {
        var refund = fillAllTBaseObject(new StatRefund(), StatRefund.class);
        var cart = fillAllTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillAllTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var cash = fillAllTBaseObject(new Cash(), Cash.class);
        var status = fillAllTBaseObject(new InvoicePaymentRefundStatus(), InvoicePaymentRefundStatus.class);
        var response = fillAllTBaseObject(new StatRefundResponse(), StatRefundResponse.class);

        return response.setRefunds(
                List.of(refund
                        .setCart(cart
                                .setLines(List.of(line.setPrice(cash))))
                        .setStatus(status))
        );
    }

    public static StatInvoiceResponse createSearchInvoiceRequiredResponse() {
        return fillRequiredTBaseObject(new StatInvoiceResponse(), StatInvoiceResponse.class);
    }

    public static StatInvoiceResponse createSearchInvoiceAllResponse() {
        var invoice = fillAllTBaseObject(new StatInvoice(), StatInvoice.class);
        var cart = fillAllTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillAllTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var cash = fillAllTBaseObject(new Cash(), Cash.class);
        var status = fillAllTBaseObject(new InvoiceStatus(), InvoiceStatus.class);
        var response = fillAllTBaseObject(new StatInvoiceResponse(), StatInvoiceResponse.class);

        return response.setInvoices(
                List.of(invoice
                        .setCart(cart
                                .setLines(List.of(line.setPrice(cash))))
                        .setStatus(status))
        );
    }

    public static StatPayoutResponse createSearchPayoutRequiredResponse() {
        return fillRequiredTBaseObject(new StatPayoutResponse(), StatPayoutResponse.class);
    }

    public static StatPayoutResponse createSearchPayoutAllResponse() {
        var payout = fillAllTBaseObject(new StatPayout(), StatPayout.class);
        var toolInfo = fillAllTBaseObject(new PayoutToolInfo(), PayoutToolInfo.class);
        var bank = fillAllTBaseObject(new RussianBankAccount(), RussianBankAccount.class);
        var status = fillAllTBaseObject(new PayoutStatus(), PayoutStatus.class);
        var response = fillAllTBaseObject(new StatPayoutResponse(), StatPayoutResponse.class);
        toolInfo.setRussianBankAccount(bank);
        return response.setPayouts(
                List.of(payout
                        .setPayoutToolInfo(toolInfo)
                        .setStatus(status))
        );
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
