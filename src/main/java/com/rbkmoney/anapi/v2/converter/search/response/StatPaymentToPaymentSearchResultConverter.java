package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatPayment;
import com.rbkmoney.openapi.anapi_v2.model.GeoLocationInfo;
import com.rbkmoney.openapi.anapi_v2.model.PaymentFlow;
import com.rbkmoney.openapi.anapi_v2.model.PaymentSearchResult;
import com.rbkmoney.openapi.anapi_v2.model.TransactionInfo;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

import static com.rbkmoney.anapi.v2.util.OpenApiUtil.fillPaymentStatusInfo;
import static com.rbkmoney.anapi.v2.util.OpenApiUtil.getPayer;

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
}
