package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.damsel.domain.InvoicePaymentChargebackCategory;
import com.rbkmoney.magista.InvoiceStatus;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackCategory;
import com.rbkmoney.openapi.anapi_v2.model.Invoice;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenApiUtil {

    public static ChargebackCategory mapToCategory(InvoicePaymentChargebackCategory chargebackCategory) {
        if (chargebackCategory.isSetAuthorisation()) {
            return ChargebackCategory.AUTHORISATION;
        }

        if (chargebackCategory.isSetDispute()) {
            return ChargebackCategory.DISPUTE;
        }

        if (chargebackCategory.isSetFraud()) {
            return ChargebackCategory.FRAUD;
        }

        if (chargebackCategory.isSetProcessingError()) {
            return ChargebackCategory.PROCESSING_ERROR;
        }

        return null;
    }

    public static Invoice.StatusEnum mapToStatus(InvoiceStatus status) {
        if (status.isSetFulfilled()) {
            return Invoice.StatusEnum.FULFILLED;
        }

        if (status.isSetPaid()) {
            return Invoice.StatusEnum.PAID;
        }

        if (status.isSetUnpaid()) {
            return Invoice.StatusEnum.UNPAID;
        }

        if (status.isSetCancelled()) {
            return Invoice.StatusEnum.CANCELLED;
        }

        throw new IllegalArgumentException("");
    }
}
