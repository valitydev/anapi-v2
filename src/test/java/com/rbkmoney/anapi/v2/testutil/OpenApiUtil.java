package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.anapi.v2.model.ChargebackCategory;
import com.rbkmoney.anapi.v2.model.ChargebackStage;
import com.rbkmoney.anapi.v2.model.ChargebackStatus;
import com.rbkmoney.anapi.v2.model.PaymentStatus;
import com.rbkmoney.damsel.domain.PaymentInstitutionRealm;
import com.rbkmoney.damsel.merch_stat.TerminalPaymentProvider;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomIntegerAsString;

@UtilityClass
public class OpenApiUtil {

    public static MultiValueMap<String, String> getSearchRequiredParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("partyID", randomIntegerAsString(1, 1000));
        params.add("fromTime", "2007-12-03T10:15:30+01:00");
        params.add("toTime", "2020-12-03T10:15:30+01:00");
        params.add("limit", randomIntegerAsString(1, 40));
        return params;
    }

    public static MultiValueMap<String, String> getSearchPaymentAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomIntegerAsString(1, 10));
        params.add("shopIDs", randomIntegerAsString(11, 20));
        params.add("shopIDs", randomIntegerAsString(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("invoiceIDs", randomIntegerAsString(1, 10));
        params.add("invoiceIDs", randomIntegerAsString(11, 20));
        params.add("paymentStatus", PaymentStatus.StatusEnum.PENDING.getValue());
        params.add("paymentFlow", "instant");
        params.add("paymentMethod", "paymentTerminal");
        params.add("paymentTerminalProvider", TerminalPaymentProvider.alipay.name());
        params.add("invoiceID", randomIntegerAsString(1, 1000));
        params.add("paymentID", randomIntegerAsString(1, 1000));
        params.add("externalID", randomIntegerAsString(1, 1000));
        params.add("payerEmail", "payer@mail.com");
        params.add("payerIP", "0.0.0.0");
        params.add("payerFingerprint", "iamveryunique");
        params.add("first6", randomIntegerAsString(100000, 999999));
        params.add("last4", randomIntegerAsString(1000, 9999));
        params.add("rrn", "123456789010");
        params.add("approvalCode", "QWERTY");
        params.add("bankCardTokenProvider", "applepay");
        params.add("bankCardPaymentSystem", "mastercard");
        params.add("paymentAmountFrom", randomIntegerAsString(1, 9999));
        params.add("paymentAmountTo", randomIntegerAsString(9999, 999999));
        params.add("excludedShops", randomIntegerAsString(1, 10));
        params.add("excludedShops", randomIntegerAsString(11, 20));
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchChargebackAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomIntegerAsString(1, 10));
        params.add("shopIDs", randomIntegerAsString(11, 20));
        params.add("shopIDs", randomIntegerAsString(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("offset", randomIntegerAsString(1, 10));
        params.add("invoiceID", randomIntegerAsString(1, 1000));
        params.add("paymentID", randomIntegerAsString(1, 1000));
        params.add("chargebackID", randomIntegerAsString(1, 1000));
        params.add("chargebackStatuses", ChargebackStatus.PENDING.getValue());
        params.add("chargebackStages", ChargebackStage.CHARGEBACK.getValue());
        params.add("chargebackCategories", ChargebackCategory.AUTHORISATION.getValue());
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchRefundAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomIntegerAsString(1, 10));
        params.add("shopIDs", randomIntegerAsString(11, 20));
        params.add("shopIDs", randomIntegerAsString(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("offset", randomIntegerAsString(1, 10));
        params.add("invoiceIDs", randomIntegerAsString(1, 10));
        params.add("invoiceIDs", randomIntegerAsString(11, 20));
        params.add("invoiceID", randomIntegerAsString(1, 1000));
        params.add("paymentID", randomIntegerAsString(1, 1000));
        params.add("refundID", randomIntegerAsString(1, 1000));
        params.add("externalID", randomIntegerAsString(1, 1000));
        params.add("refundStatus", "pending");
        params.add("excludedShops", randomIntegerAsString(1, 10));
        params.add("excludedShops", randomIntegerAsString(11, 20));
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchInvoiceAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomIntegerAsString(1, 10));
        params.add("shopIDs", randomIntegerAsString(11, 20));
        params.add("shopIDs", randomIntegerAsString(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("invoiceIDs", randomIntegerAsString(1, 10));
        params.add("invoiceIDs", randomIntegerAsString(11, 20));
        params.add("invoiceStatus", "paid");
        params.add("invoiceID", randomIntegerAsString(1, 1000));
        params.add("externalID", randomIntegerAsString(1, 1000));
        params.add("invoiceAmountFrom", randomIntegerAsString(1, 1000));
        params.add("invoiceAmountTo", randomIntegerAsString(1, 1000));
        params.add("excludedShops", randomIntegerAsString(1, 10));
        params.add("excludedShops", randomIntegerAsString(11, 20));
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getSearchPayoutAllParams() {
        MultiValueMap<String, String> params = getSearchRequiredParams();
        params.add("shopID", randomIntegerAsString(1, 10));
        params.add("shopIDs", randomIntegerAsString(11, 20));
        params.add("shopIDs", randomIntegerAsString(21, 30));
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        params.add("offset", randomIntegerAsString(1, 10));
        params.add("payoutID", randomIntegerAsString(1, 1000));
        params.add("payoutToolType", "PayoutAccount");
        params.add("excludedShops", randomIntegerAsString(1, 10));
        params.add("excludedShops", randomIntegerAsString(11, 20));
        params.add("continuationToken", "test");
        return params;
    }

    public static MultiValueMap<String, String> getAnalyticsRequiredParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("partyID", randomIntegerAsString(1, 1000));
        params.add("fromTime", "2007-12-03T10:15:30+01:00");
        params.add("toTime", "2020-12-03T10:15:30+01:00");
        return params;
    }

    public static MultiValueMap<String, String> getAnalyticsAllParams() {
        MultiValueMap<String, String> params = getAnalyticsRequiredParams();
        params.add("shopIDs", "{1,3}");
        params.add("excludeShopIDs", "{2}");
        params.add("paymentInstitutionRealm", PaymentInstitutionRealm.live.name());
        return params;
    }
}
