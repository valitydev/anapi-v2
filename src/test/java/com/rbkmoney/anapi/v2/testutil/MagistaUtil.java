package com.rbkmoney.anapi.v2.testutil;

import dev.vality.bouncer.ctx.ContextFragment;
import dev.vality.bouncer.decisions.Judgement;
import dev.vality.bouncer.decisions.Resolution;
import dev.vality.bouncer.decisions.ResolutionAllowed;
import dev.vality.damsel.base.Content;
import dev.vality.damsel.domain.InvoicePaymentRefundStatus;
import dev.vality.damsel.domain.InvoicePaymentStatus;
import dev.vality.damsel.domain.InvoiceStatus;
import dev.vality.damsel.domain.*;
import dev.vality.damsel.geo_ip.LocationInfo;
import dev.vality.magista.CustomerPayer;
import dev.vality.magista.InvoicePaymentFlow;
import dev.vality.magista.InvoicePaymentFlowHold;
import dev.vality.magista.InvoicePaymentFlowInstant;
import dev.vality.magista.Payer;
import dev.vality.magista.*;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static com.rbkmoney.anapi.v2.testutil.DamselUtil.fillRequiredTBaseObject;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomInt;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomString;

@UtilityClass
public class MagistaUtil {

    public static StatPaymentResponse createSearchPaymentRequiredResponse() {
        return fillRequiredTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);
    }

    public static StatChargebackResponse createSearchChargebackRequiredResponse() {
        return fillRequiredTBaseObject(new StatChargebackResponse(), StatChargebackResponse.class);
    }

    public static StatPaymentResponse createSearchPaymentAllResponse() {
        var payer = fillRequiredTBaseObject(new CustomerPayer(), CustomerPayer.class);
        payer.setPaymentTool(createBankCardPaymentTool())
                .setCustomerId(randomString(3));
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
                        .setPayer(Payer.customer(payer))
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

    public static StatInvoiceTemplateResponse createSearchInvoiceTemplateAllResponse() {
        var invoiceTemplate = fillRequiredTBaseObject(new StatInvoiceTemplate(), StatInvoiceTemplate.class);
        var cash = fillRequiredTBaseObject(new Cash(), Cash.class);
        var context = fillRequiredTBaseObject(new Content(), Content.class);
        var response = fillRequiredTBaseObject(new StatInvoiceTemplateResponse(), StatInvoiceTemplateResponse.class);

        return response.setInvoiceTemplates(
                List.of(invoiceTemplate
                        .setDescription(randomString(10))
                        .setDetails(InvoiceTemplateDetails.cart(
                                new InvoiceCart()
                                        .setLines(List.of(
                                                        new InvoiceLine()
                                                                .setPrice(cash)
                                                                .setProduct(randomString(10))
                                                                .setQuantity(randomInt(1, 1000))
                                                                .setMetadata(new HashMap<>())
                                                )
                                        )))
                        .setContext(context)
                        .setName(randomString(10))
                        .setInvoiceTemplateStatus(InvoiceTemplateStatus.created)
                        .setInvoiceTemplateCreatedAt(Instant.now().toString())
                )
        );
    }

    public static StatInvoiceTemplateResponse createSearchInvoiceTemplateRequiredResponse() {
        return fillRequiredTBaseObject(new StatInvoiceTemplateResponse(), StatInvoiceTemplateResponse.class);
    }

    public static ContextFragment createContextFragment() {
        return fillRequiredTBaseObject(new ContextFragment(), ContextFragment.class);
    }

    public static Judgement createJudgementAllowed() {
        Resolution resolution = new Resolution();
        resolution.setAllowed(new ResolutionAllowed());
        return new Judgement().setResolution(resolution);
    }

    public static InvoicePaymentFlow createInvoicePaymentFlowHold() {
        return InvoicePaymentFlow.hold(
                fillRequiredTBaseObject(new InvoicePaymentFlowHold(), InvoicePaymentFlowHold.class));
    }

    public static PaymentTool createBankCardPaymentTool() {
        return PaymentTool.bank_card(new BankCard().setBin(randomString(4))
                .setLastDigits(randomString(4))
                .setBankName(randomString(4))
                .setToken(randomString(4))
                .setPaymentSystemDeprecated(LegacyBankCardPaymentSystem.maestro)
                .setTokenProviderDeprecated(LegacyBankCardTokenProvider.applepay)
        );
    }

    public static PaymentTool createPaymentTerminalPaymentTool() {
        return PaymentTool.payment_terminal(new PaymentTerminal()
                .setTerminalTypeDeprecated(LegacyTerminalPaymentProvider.alipay));
    }

    public static PaymentTool createMobileCommercePaymentTool() {
        return PaymentTool.mobile_commerce(new MobileCommerce()
                .setOperatorDeprecated(LegacyMobileOperator.mts)
                .setPhone(new MobilePhone()
                        .setCc("7")
                        .setCtn("1234567890")));
    }

    public static PaymentTool createCryptoCurrencyPaymentTool() {
        return PaymentTool.crypto_currency(new CryptoCurrencyRef()
                .setId(randomString(1)));
    }

    public static PaymentTool createLegacyCryptoCurrencyPaymentTool() {
        return PaymentTool.crypto_currency_deprecated(LegacyCryptoCurrency.bitcoin);
    }

}
