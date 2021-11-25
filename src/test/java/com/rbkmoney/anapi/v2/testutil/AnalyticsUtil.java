package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.damsel.analytics.AmountResponse;

import static com.rbkmoney.anapi.v2.testutil.DamselUtil.fillRequiredTBaseObject;

public class AnalyticsUtil {

    public static AmountResponse createAveragePaymentRequiredResponse() {
        return fillRequiredTBaseObject(new AmountResponse(), AmountResponse.class);
    }

    public static AmountResponse createAveragePaymentAllResponse() {
        return createAveragePaymentRequiredResponse();
    }
}
