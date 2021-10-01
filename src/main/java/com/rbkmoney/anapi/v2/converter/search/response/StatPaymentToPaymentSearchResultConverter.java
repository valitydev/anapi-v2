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
                .payer(getPayer(payment))
                .shopID(payment.getShopId())
                .shortID(payment.getShortId())
                .transactionInfo(payment.getAdditionalTransactionInfo() != null
                        ? new TransactionInfo()
                        .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                        .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        : null);
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

    private PaymentSearchResult.StatusEnum mapStatus(InvoicePaymentStatus status) {
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
