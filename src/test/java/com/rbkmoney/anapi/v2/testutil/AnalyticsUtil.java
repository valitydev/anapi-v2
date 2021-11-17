package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.damsel.analytics.AmountResponse;

public class AnalyticsUtil {

    public static AmountResponse createAveragePaymentRequiredResponse() {
        return MagistaUtil.fillRequiredTBaseObject(new AmountResponse(), AmountResponse.class);
    }

    public static AmountResponse createAveragePaymentAllResponse() {
        return createAveragePaymentRequiredResponse();
    }
}
