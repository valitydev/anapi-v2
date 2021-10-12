package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatPayment;
import com.rbkmoney.openapi.anapi_v2.model.*;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

import static com.rbkmoney.openapi.anapi_v2.model.PaymentSearchResult.StatusEnum.*;

@Component
public class StatPaymentToPaymentSearchResultConverter {

    public PaymentSearchResult convert(StatPayment payment) {
        return new PaymentSearchResult()
                .amount(payment.getAmount())
                .createdAt(TypeUtil.stringToInstant(payment.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(payment.getCurrencySymbolicCode())
                .externalID(payment.getExternalId())
                .fee(payment.getFee())
                .flow(new PaymentFlow()
                        .type(payment.getFlow().isSetHold() ? PaymentFlow.TypeEnum.PAYMENTFLOWHOLD :
                                PaymentFlow.TypeEnum.PAYMENTFLOWINSTANT))
                .geoLocationInfo(payment.getLocationInfo() != null ? new GeoLocationInfo()
                        .cityGeoID(payment.getLocationInfo().getCityGeoId())
                        .countryGeoID(payment.getLocationInfo().getCountryGeoId())
                        : null)
                .status(mapStatus(payment.getStatus()))
                .error(payment.getStatus().isSetFailed()
                        ? new PaymentError().code(payment.getStatus().getFailed().getFailure().getFailure().getCode())
                        : null)
                .statusChangedAt(payment.getStatusChangedAt() != null
                        ? TypeUtil.stringToInstant(payment.getStatusChangedAt()).atOffset(ZoneOffset.UTC) : null)
                .id(payment.getId())
                .invoiceID(payment.getInvoiceId())
                .makeRecurrent(payment.isMakeRecurrent())
                .payer(mapPayer(payment.getPayer()))
                .shopID(payment.getShopId())
                .shortID(payment.getShortId())
                .transactionInfo(payment.getAdditionalTransactionInfo() != null
                        ? new TransactionInfo()
                        .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                        .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        : null);
    }

    protected Payer mapPayer(com.rbkmoney.magista.Payer payer) {

        if (payer.isSetCustomer()) {
            return new Payer().payerType(Payer.PayerTypeEnum.CUSTOMERPAYER);
        }

        if (payer.isSetPaymentResource()) {
            return new Payer().payerType(Payer.PayerTypeEnum.PAYMENTRESOURCEPAYER);
        }

        if (payer.isSetRecurrent()) {
            return new Payer().payerType(Payer.PayerTypeEnum.RECURRENTPAYER);
        }

        throw new IllegalArgumentException(
                String.format("Payer %s cannot be processed", payer));
    }

    protected PaymentSearchResult.StatusEnum mapStatus(InvoicePaymentStatus status) {
        if (status.isSetPending()) {
            return PENDING;
        }
        if (status.isSetProcessed()) {
            return PROCESSED;
        }
        if (status.isSetCaptured()) {
            return CAPTURED;
        }
        if (status.isSetCancelled()) {
            return CANCELLED;
        }
        if (status.isSetRefunded()) {
            return REFUNDED;
        }
        if (status.isSetFailed()) {
            return FAILED;
        }
        if (status.isSetChargedBack()) {
            return CHARGEDBACK;
        }
        throw new IllegalArgumentException(
                String.format("Payment status %s cannot be processed", status));

    }
}
