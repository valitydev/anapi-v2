package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoicePaymentStatus;
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
        return switch (status) {
            case pending -> PENDING;
            case processed -> PROCESSED;
            case captured -> CAPTURED;
            case cancelled -> CANCELLED;
            case refunded -> REFUNDED;
            case failed -> FAILED;
            case charged_back -> CHARGEDBACK;
            default -> throw new IllegalArgumentException(
                    String.format("Payment status %s cannot be processed", status));

        };
    }
}
