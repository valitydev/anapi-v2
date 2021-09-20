package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.damsel.domain.PaymentInstitutionRealm;
import com.rbkmoney.magista.TerminalPaymentProvider;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomInteger;

@UtilityClass
public class OpenApiUtil {

    public static MultiValueMap<String, String> getSearchRequiredParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("partyID", randomInteger(1, 1000));
        params.add("fromTime", "2007-12-03T10:15:30+01:00");
        params.add("toTime", "2020-12-03T10:15:30+01:00");
        params.add("limit", randomInteger(1, 40));
        return params;
    }

    public static MultiValueMap<String, String> getSearchPaymentAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomInteger(1, 10));
        params.add("shopIDs", randomInteger(11, 20));
        params.add("shopIDs", randomInteger(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("invoiceIDs", randomInteger(1, 10));
        params.add("invoiceIDs", randomInteger(11, 20));
        params.add("paymentStatus", PaymentStatus.StatusEnum.PENDING.name());
        params.add("paymentFlow", "instant");
        params.add("paymentMethod", "paymentTerminal");
        params.add("paymentTerminalProvider", TerminalPaymentProvider.alipay.name());
        params.add("invoiceID", randomInteger(1, 1000));
        params.add("paymentID", randomInteger(1, 1000));
        params.add("externalID", randomInteger(1, 1000));
        params.add("payerEmail", "payer@mail.com");
        params.add("payerIP", "0.0.0.0");
        params.add("payerFingerprint", "iamveryunique");
        params.add("first6", randomInteger(100000, 999999));
        params.add("last4", randomInteger(1000, 9999));
        params.add("rrn", "123456789010");
        params.add("approvalCode", "QWERTY");
        params.add("bankCardTokenProvider", BankCardTokenProvider.APPLEPAY.name());
        params.add("bankCardPaymentSystem", BankCardPaymentSystem.MASTERCARD.name());
        params.add("paymentAmountFrom", randomInteger(1, 9999));
        params.add("paymentAmountTo", randomInteger(9999, 999999));
        params.add("excludedShops", randomInteger(1, 10));
        params.add("excludedShops", randomInteger(11, 20));
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchChargebackAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomInteger(1, 10));
        params.add("shopIDs", randomInteger(11, 20));
        params.add("shopIDs", randomInteger(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("offset", randomInteger(1, 10));
        params.add("invoiceID", randomInteger(1, 1000));
        params.add("paymentID", randomInteger(1, 1000));
        params.add("chargebackID", randomInteger(1, 1000));
        params.add("chargebackStatuses", ChargebackStatus.PENDING.name());
        params.add("chargebackStages", ChargebackStage.CHARGEBACK.name());
        params.add("chargebackCategories", ChargebackCategory.AUTHORISATION.name());
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchRefundAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomInteger(1, 10));
        params.add("shopIDs", randomInteger(11, 20));
        params.add("shopIDs", randomInteger(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("offset", randomInteger(1, 10));
        params.add("invoiceIDs", randomInteger(1, 10));
        params.add("invoiceIDs", randomInteger(11, 20));
        params.add("invoiceID", randomInteger(1, 1000));
        params.add("paymentID", randomInteger(1, 1000));
        params.add("refundID", randomInteger(1, 1000));
        params.add("externalID", randomInteger(1, 1000));
        params.add("refundStatus", RefundStatus.StatusEnum.PENDING.name());
        params.add("excludedShops", randomInteger(1, 10));
        params.add("excludedShops", randomInteger(11, 20));
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchInvoiceAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomInteger(1, 10));
        params.add("shopIDs", randomInteger(11, 20));
        params.add("shopIDs", randomInteger(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("invoiceIDs", randomInteger(1, 10));
        params.add("invoiceIDs", randomInteger(11, 20));
        params.add("invoiceStatus", Invoice.StatusEnum.PAID.name());
        params.add("invoiceID", randomInteger(1, 1000));
        params.add("externalID", randomInteger(1, 1000));
        params.add("invoiceAmountFrom", randomInteger(1, 1000));
        params.add("invoiceAmountTo", randomInteger(1, 1000));
        params.add("excludedShops", randomInteger(1, 10));
        params.add("excludedShops", randomInteger(11, 20));
        params.add("continuationToken", "test");

        return params;
    }
}
