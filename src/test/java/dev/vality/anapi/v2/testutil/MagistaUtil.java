package dev.vality.anapi.v2.testutil;

import dev.vality.damsel.base.Content;
import dev.vality.damsel.domain.InvoicePaymentRefundStatus;
import dev.vality.damsel.domain.InvoicePaymentStatus;
import dev.vality.damsel.domain.InvoiceStatus;
import dev.vality.damsel.domain.*;
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

@UtilityClass
public class MagistaUtil {

    public static StatPaymentResponse createSearchPaymentRequiredResponse() {
        return DamselUtil.fillRequiredTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);
    }

    public static StatChargebackResponse createSearchChargebackRequiredResponse() {
        return DamselUtil.fillRequiredTBaseObject(new StatChargebackResponse(), StatChargebackResponse.class);
    }

    public static StatPaymentResponse createSearchPaymentAllResponse() {
        var payer = DamselUtil.fillRequiredTBaseObject(new CustomerPayer(), CustomerPayer.class);
        payer.setPaymentTool(createBankCardPaymentTool())
                .setCustomerId(RandomUtil.randomString(3));
        var payment = DamselUtil.fillRequiredTBaseObject(new StatPayment(), StatPayment.class);
        var status = new InvoicePaymentStatus();
        status.setPending(new InvoicePaymentPending());
        var cart = DamselUtil.fillRequiredTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = DamselUtil.fillRequiredTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var instant = DamselUtil.fillRequiredTBaseObject(new InvoicePaymentFlowInstant(),
                InvoicePaymentFlowInstant.class);
        var response = DamselUtil.fillRequiredTBaseObject(new StatPaymentResponse(), StatPaymentResponse.class);

        return response.setPayments(
                List.of(payment
                        .setStatus(status)
                        .setCart(cart.setLines(List.of(line)))
                        .setFlow(InvoicePaymentFlow
                                .instant(instant))
                        .setPayer(Payer.customer(payer))));
    }

    public static StatChargebackResponse createSearchChargebackAllResponse() {
        var chargeback = DamselUtil.fillRequiredTBaseObject(new StatChargeback(), StatChargeback.class);
        var stage = DamselUtil.fillRequiredTBaseObject(new InvoicePaymentChargebackStage(),
                InvoicePaymentChargebackStage.class);
        var reason =
                DamselUtil.fillRequiredTBaseObject(new InvoicePaymentChargebackReason(),
                        InvoicePaymentChargebackReason.class);
        var status =
                DamselUtil.fillRequiredTBaseObject(new InvoicePaymentChargebackStatus(),
                        InvoicePaymentChargebackStatus.class);
        var response = DamselUtil.fillRequiredTBaseObject(new StatChargebackResponse(),
                StatChargebackResponse.class);

        return response.setChargebacks(
                List.of(chargeback
                        .setStage(stage)
                        .setChargebackReason(reason)
                        .setChargebackStatus(status))
        );
    }

    public static StatRefundResponse createSearchRefundRequiredResponse() {
        return DamselUtil.fillRequiredTBaseObject(new StatRefundResponse(), StatRefundResponse.class);
    }

    public static StatRefundResponse createSearchRefundAllResponse() {
        var refund = DamselUtil.fillRequiredTBaseObject(new StatRefund(), StatRefund.class);
        var cart = DamselUtil.fillRequiredTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = DamselUtil.fillRequiredTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var cash = DamselUtil.fillRequiredTBaseObject(new Cash(), Cash.class);
        var status = DamselUtil.fillRequiredTBaseObject(new InvoicePaymentRefundStatus(),
                InvoicePaymentRefundStatus.class);
        var response = DamselUtil.fillRequiredTBaseObject(new StatRefundResponse(), StatRefundResponse.class);

        return response.setRefunds(
                List.of(refund
                        .setCart(cart
                                .setLines(List.of(line.setPrice(cash))))
                        .setStatus(status))
        );
    }

    public static StatInvoiceResponse createSearchInvoiceRequiredResponse() {
        return DamselUtil.fillRequiredTBaseObject(new StatInvoiceResponse(), StatInvoiceResponse.class);
    }

    public static StatInvoiceResponse createSearchInvoiceAllResponse() {
        var invoice = DamselUtil.fillRequiredTBaseObject(new StatInvoice(), StatInvoice.class);
        var cart = DamselUtil.fillRequiredTBaseObject(new InvoiceCart(), InvoiceCart.class);
        var line = DamselUtil.fillRequiredTBaseObject(new InvoiceLine(), InvoiceLine.class);
        var cash = DamselUtil.fillRequiredTBaseObject(new Cash(), Cash.class);
        var status = DamselUtil.fillRequiredTBaseObject(new InvoiceStatus(),
                InvoiceStatus.class);
        var response = DamselUtil.fillRequiredTBaseObject(new StatInvoiceResponse(), StatInvoiceResponse.class);

        return response.setInvoices(
                List.of(invoice
                        .setCart(cart
                                .setLines(List.of(line.setPrice(cash))))
                        .setStatus(status))
        );
    }

    public static StatPayoutResponse createSearchPayoutRequiredResponse() {
        return DamselUtil.fillRequiredTBaseObject(new StatPayoutResponse(), StatPayoutResponse.class);
    }

    public static StatPayoutResponse createSearchPayoutAllResponse() {
        var payout = DamselUtil.fillRequiredTBaseObject(new StatPayout(), StatPayout.class);
        var toolInfo = DamselUtil.fillRequiredTBaseObject(new PayoutToolInfo(), PayoutToolInfo.class);
        var bank = DamselUtil.fillRequiredTBaseObject(new RussianBankAccount(), RussianBankAccount.class);
        var status = DamselUtil.fillRequiredTBaseObject(new PayoutStatus(), PayoutStatus.class);
        var response = DamselUtil.fillRequiredTBaseObject(new StatPayoutResponse(), StatPayoutResponse.class);
        toolInfo.setRussianBankAccount(bank);
        return response.setPayouts(
                List.of(payout
                        .setPayoutToolInfo(toolInfo)
                        .setStatus(status))
        );
    }

    public static StatInvoiceTemplateResponse createSearchInvoiceTemplateAllResponse() {
        var invoiceTemplate = DamselUtil.fillRequiredTBaseObject(new StatInvoiceTemplate(), StatInvoiceTemplate.class);
        var cash = DamselUtil.fillRequiredTBaseObject(new Cash(), Cash.class);
        var context = DamselUtil.fillRequiredTBaseObject(new Content(), Content.class);
        var response = DamselUtil.fillRequiredTBaseObject(new StatInvoiceTemplateResponse(),
                StatInvoiceTemplateResponse.class);

        return response.setInvoiceTemplates(
                List.of(invoiceTemplate
                        .setDescription(RandomUtil.randomString(10))
                        .setDetails(InvoiceTemplateDetails.cart(
                                new InvoiceCart()
                                        .setLines(List.of(
                                                        new InvoiceLine()
                                                                .setPrice(cash)
                                                                .setProduct(RandomUtil.randomString(10))
                                                                .setQuantity(RandomUtil.randomInt(1, 1000))
                                                                .setMetadata(new HashMap<>())
                                                )
                                        )))
                        .setContext(context)
                        .setName(RandomUtil.randomString(10))
                        .setInvoiceTemplateStatus(InvoiceTemplateStatus.created)
                        .setInvoiceTemplateCreatedAt(Instant.now().toString())
                )
        );
    }

    public static StatInvoiceTemplateResponse createSearchInvoiceTemplateRequiredResponse() {
        return DamselUtil.fillRequiredTBaseObject(new StatInvoiceTemplateResponse(), StatInvoiceTemplateResponse.class);
    }

    public static InvoicePaymentFlow createInvoicePaymentFlowHold() {
        return InvoicePaymentFlow.hold(
                DamselUtil.fillRequiredTBaseObject(new InvoicePaymentFlowHold(), InvoicePaymentFlowHold.class));
    }

    public static PaymentTool createBankCardPaymentTool() {
        return PaymentTool.bank_card(new BankCard().setBin(RandomUtil.randomString(4))
                .setLastDigits(RandomUtil.randomString(4))
                .setBankName(RandomUtil.randomString(4))
                .setToken(RandomUtil.randomString(4))
                .setPaymentSystem(new PaymentSystemRef("maestro"))
                .setPaymentToken(new BankCardTokenServiceRef("applepay")));
    }

    public static PaymentTool createPaymentTerminalPaymentTool() {
        return PaymentTool.payment_terminal(new PaymentTerminal()
                .setPaymentService(new PaymentServiceRef("alipay")));
    }

    public static PaymentTool createMobileCommercePaymentTool() {
        return PaymentTool.mobile_commerce(new MobileCommerce().setOperator(new MobileOperatorRef("mts"))
                .setPhone(new MobilePhone()
                        .setCc("7")
                        .setCtn("1234567890")));
    }

    public static PaymentTool createCryptoCurrencyPaymentTool() {
        return PaymentTool.crypto_currency(new CryptoCurrencyRef()
                .setId(RandomUtil.randomString(1)));
    }

    public static PaymentTool createLegacyCryptoCurrencyPaymentTool() {
        return PaymentTool.crypto_currency(new CryptoCurrencyRef("bitcoin"));
    }

}
