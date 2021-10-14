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
                .geoLocationInfo(payment.isSetLocationInfo() ? new GeoLocationInfo()
                        .cityGeoID(payment.getLocationInfo().getCityGeoId())
                        .countryGeoID(payment.getLocationInfo().getCountryGeoId())
                        : null)
                .status(mapStatus(payment.getStatus()))
                .error(payment.getStatus().isSetFailed()
                        ? new PaymentError().code(payment.getStatus().getFailed().getFailure().getFailure().getCode())
                        : null)
                .statusChangedAt(payment.isSetStatusChangedAt()
                        ? TypeUtil.stringToInstant(payment.getStatusChangedAt()).atOffset(ZoneOffset.UTC) : null)
                .id(payment.getId())
                .invoiceID(payment.getInvoiceId())
                .makeRecurrent(payment.isMakeRecurrent())
                .payer(mapPayer(payment.getPayer()))
                .shopID(payment.getShopId())
                .shortID(payment.getShortId())
                .transactionInfo(payment.isSetAdditionalTransactionInfo()
                        ? new TransactionInfo()
                        .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                        .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        : null);
    }

    protected Payer mapPayer(com.rbkmoney.magista.Payer payer) {
        try {
            var field = com.rbkmoney.magista.Payer._Fields.findByName(payer.getSetField().getFieldName());
            return switch (field) {
                case CUSTOMER -> new Payer().payerType(Payer.PayerTypeEnum.CUSTOMERPAYER);
                case PAYMENT_RESOURCE -> new Payer().payerType(Payer.PayerTypeEnum.PAYMENTRESOURCEPAYER);
                case RECURRENT -> new Payer().payerType(Payer.PayerTypeEnum.RECURRENTPAYER);
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Payer %s cannot be processed", payer));
        }
    }

    protected PaymentSearchResult.StatusEnum mapStatus(InvoicePaymentStatus status) {
        try {
            var field = InvoicePaymentStatus._Fields.findByName(status.getSetField().getFieldName());
            return switch (field) {
                case PENDING -> PENDING;
                case PROCESSED -> PROCESSED;
                case CAPTURED -> CAPTURED;
                case CANCELLED -> CANCELLED;
                case REFUNDED -> REFUNDED;
                case FAILED -> FAILED;
                case CHARGED_BACK -> CHARGEDBACK;
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Payment status %s cannot be processed", status));
        }

    }
}
