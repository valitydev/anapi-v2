package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatPayment;
import com.rbkmoney.openapi.anapi_v2.model.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class StatPaymentToPaymentSearchResultConverter {

    public PaymentSearchResult convert(StatPayment payment) {
        PaymentSearchResult result = new PaymentSearchResult()
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
                .id(payment.getId())
                .invoiceID(payment.getInvoiceId())
                .makeRecurrent(payment.isMakeRecurrent())
                .payer(getPayer(payment))
                .shopID(payment.getShopId())
                .shortID(payment.getShortId())
                .transactionInfo(payment.getAdditionalTransactionInfo() != null
                        ? new TransactionInfo()
                        .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                        .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        : null);
        fillPaymentStatusInfo(payment, result);
        return result;
    }

    private Payer getPayer(StatPayment payment) {
        var statPayer = payment.getPayer();
        Payer payer = new Payer();

        if (statPayer.isSetCustomer()) {
            return payer.payerType(Payer.PayerTypeEnum.CUSTOMERPAYER);
        }

        if (statPayer.isSetPaymentResource()) {
            return payer.payerType(Payer.PayerTypeEnum.PAYMENTRESOURCEPAYER);
        }

        if (statPayer.isSetRecurrent()) {
            return payer.payerType(Payer.PayerTypeEnum.RECURRENTPAYER);
        }

        throw new IllegalArgumentException(
                String.format("Payer %s cannot be processed", statPayer));
    }

    private void fillPaymentStatusInfo(StatPayment payment, PaymentSearchResult result) {
        var status = payment.getStatus();
        if (status.isSetCancelled()) {
            OffsetDateTime createdAt = status.getCancelled().getAt() != null
                    ? TypeUtil.stringToInstant(status.getCancelled().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.CANCELLED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetCaptured()) {
            OffsetDateTime createdAt = status.getCaptured().getAt() != null
                    ? TypeUtil.stringToInstant(status.getCaptured().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.CAPTURED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            OffsetDateTime createdAt = status.getFailed().getAt() != null
                    ? TypeUtil.stringToInstant(status.getFailed().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.FAILED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetPending()) {
            result.status(PaymentSearchResult.StatusEnum.PENDING);
            return;
        }

        if (status.isSetProcessed()) {
            OffsetDateTime createdAt = status.getProcessed().getAt() != null
                    ? TypeUtil.stringToInstant(status.getProcessed().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.PROCESSED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetRefunded()) {
            OffsetDateTime createdAt = status.getRefunded().getAt() != null
                    ? TypeUtil.stringToInstant(status.getRefunded().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.REFUNDED)
                    .createdAt(createdAt);
            return;
        }

        throw new IllegalArgumentException(
                String.format("Payment status %s cannot be processed", payment.getStatus()));
    }
}
