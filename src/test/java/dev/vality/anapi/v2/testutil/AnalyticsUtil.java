package dev.vality.anapi.v2.testutil;

import dev.vality.damsel.analytics.AmountResponse;

import static dev.vality.anapi.v2.testutil.DamselUtil.fillRequiredTBaseObject;

public class AnalyticsUtil {

    public static AmountResponse createAveragePaymentRequiredResponse() {
        return fillRequiredTBaseObject(new AmountResponse(), AmountResponse.class);
    }

    public static AmountResponse createAveragePaymentAllResponse() {
        return createAveragePaymentRequiredResponse();
    }
}
