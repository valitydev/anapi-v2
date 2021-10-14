package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.bouncer.ctx.ContextFragment;
import com.rbkmoney.bouncer.decisions.Judgement;
import com.rbkmoney.bouncer.decisions.Resolution;
import com.rbkmoney.bouncer.decisions.ResolutionAllowed;
import com.rbkmoney.damsel.domain.InvoicePaymentRefundStatus;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.InvoiceStatus;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.damsel.geo_ip.LocationInfo;
import com.rbkmoney.geck.serializer.kit.mock.FieldHandler;
import com.rbkmoney.geck.serializer.kit.mock.MockMode;
import com.rbkmoney.geck.serializer.kit.mock.MockTBaseProcessor;
import com.rbkmoney.geck.serializer.kit.tbase.TBaseHandler;
import com.rbkmoney.magista.InvoicePaymentFlow;
import com.rbkmoney.magista.InvoicePaymentFlowInstant;
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
    
    static {
        mockRequiredTBaseProcessor = new MockTBaseProcessor(MockMode.REQUIRED_ONLY, 15, 1);
        Map.Entry<FieldHandler, String[]> timeFields = Map.entry(
                structHandler -> structHandler.value(Instant.now().toString()),
                new String[] {"created_at", "at", "due", "status_changed_at"}
        );
        mockRequiredTBaseProcessor.addFieldHandler(timeFields.getKey(), timeFields.getValue());
    }

    public static StatPaymentResponse createSearchPaymentRequiredResponse() {
        return fillRequiredTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);
    }

    public static StatChargebackResponse createSearchChargebackRequiredResponse() {
        return fillRequiredTBaseObject(new StatChargebackResponse(), StatChargebackResponse.class);
    }

    public static StatPaymentResponse createSearchPaymentAllResponse() {
        var payment = fillRequiredTBaseObject(new StatPayment(), StatPayment.class);
        var status = new InvoicePaymentStatus();
        status.setPending(new InvoicePaymentPending());
        var cart = fillRequiredTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillRequiredTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var instant = fillRequiredTBaseObject(new InvoicePaymentFlowInstant(), InvoicePaymentFlowInstant.class);
        var locationInfo = fillRequiredTBaseObject(new LocationInfo(), LocationInfo.class);
        var response = fillRequiredTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);

        return response.setPayments(
                List.of(payment
                        .setStatus(status)
                        .setCart(cart.setLines(List.of(line)))
                        .setFlow(InvoicePaymentFlow
                                .instant(instant))
                        .setLocationInfo(locationInfo)));
    }

    public static StatChargebackResponse createSearchChargebackAllResponse() {
        var chargeback = fillRequiredTBaseObject(new StatChargeback(), StatChargeback.class);
        var stage = fillRequiredTBaseObject(new InvoicePaymentChargebackStage(), InvoicePaymentChargebackStage.class);
        var reason =
                fillRequiredTBaseObject(new InvoicePaymentChargebackReason(), InvoicePaymentChargebackReason.class);
        var status =
                fillRequiredTBaseObject(new InvoicePaymentChargebackStatus(), InvoicePaymentChargebackStatus.class);
        var response = fillRequiredTBaseObject(new StatChargebackResponse(), StatChargebackResponse.class);

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

    public static StatRefundResponse createSearchRefundAllResponse() {
        var refund = fillRequiredTBaseObject(new StatRefund(), StatRefund.class);
        var cart = fillRequiredTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillRequiredTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var cash = fillRequiredTBaseObject(new Cash(), Cash.class);
        var status = fillRequiredTBaseObject(new InvoicePaymentRefundStatus(), InvoicePaymentRefundStatus.class);
        var response = fillRequiredTBaseObject(new StatRefundResponse(), StatRefundResponse.class);

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
        var invoice = fillRequiredTBaseObject(new StatInvoice(), StatInvoice.class);
        var cart = fillRequiredTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = fillRequiredTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var cash = fillRequiredTBaseObject(new Cash(), Cash.class);
        var status = fillRequiredTBaseObject(new InvoiceStatus(),
                InvoiceStatus.class);
        var response = fillRequiredTBaseObject(new StatInvoiceResponse(), StatInvoiceResponse.class);

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
        var payout = fillRequiredTBaseObject(new StatPayout(), StatPayout.class);
        var toolInfo = fillRequiredTBaseObject(new PayoutToolInfo(), PayoutToolInfo.class);
        var bank = fillRequiredTBaseObject(new RussianBankAccount(), RussianBankAccount.class);
        var status = fillRequiredTBaseObject(new PayoutStatus(), PayoutStatus.class);
        var response = fillRequiredTBaseObject(new StatPayoutResponse(), StatPayoutResponse.class);
        toolInfo.setRussianBankAccount(bank);
        return response.setPayouts(
                List.of(payout
                        .setPayoutToolInfo(toolInfo)
                        .setStatus(status))
        );
    }

    public static ContextFragment createContextFragment() {
        return fillRequiredTBaseObject(new ContextFragment(), ContextFragment.class);
    }

    public static Judgement createJudgementAllowed() {
        Resolution resolution = new Resolution();
        resolution.setAllowed(new ResolutionAllowed());
        return new Judgement().setResolution(resolution);
    }

    @SneakyThrows
    public static <T extends TBase> T fillRequiredTBaseObject(T tbase, Class<T> type) {
        return mockRequiredTBaseProcessor.process(tbase, new TBaseHandler<>(type));
    }
}
