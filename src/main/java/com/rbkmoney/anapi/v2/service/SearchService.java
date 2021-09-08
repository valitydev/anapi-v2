package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.model.Payer;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MerchantStatisticsServiceSrv.Iface magistaClient;

    public InlineResponse20010 findPayments(PaymentSearchQuery searchQuery) {
        try {
            StatPaymentResponse magistaResponse = magistaClient.searchPayments(searchQuery);
            List<PaymentSearchResult> results = new ArrayList<>(magistaResponse.getPaymentsSize());
            for (StatPayment payment : magistaResponse.getPayments()) {

                PaymentSearchResult result = new PaymentSearchResult()
                        .amount(payment.getAmount())
                        .createdAt(TypeUtil.stringToInstant(payment.getCreatedAt()).atOffset(ZoneOffset.UTC))
                        .currency(payment.getCurrencySymbolicCode())
                        .externalID(payment.getExternalId())
                        .fee(payment.getFee())
                        .flow(new PaymentFlow()
                                .type(payment.getFlow().isSetHold() ? PaymentFlow.TypeEnum.PAYMENTFLOWHOLD :
                                        PaymentFlow.TypeEnum.PAYMENTFLOWINSTANT))
                        .geoLocationInfo(new GeoLocationInfo()
                                .cityGeoID(payment.getLocationInfo().getCityGeoId())
                                .countryGeoID(payment.getLocationInfo().getCountryGeoId()))
                        .id(payment.getId())
                        .invoiceID(payment.getInvoiceId())
                        .makeRecurrent(payment.isMakeRecurrent())
                        .payer(getPayer(payment))
                        .shopID(payment.getShopId())
                        .shortID(payment.getShortId())
                        .status(getStatus(payment.getStatus()))
                        .statusChangedAt(TypeUtil.stringToInstant(getAt(payment.getStatus()))
                                .atOffset(ZoneOffset.UTC))
                        .transactionInfo(new TransactionInfo()
                                .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                                .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        );
                results.add(result);
            }

            return new InlineResponse20010()
                    .result(results)
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (TException e) {
            e.printStackTrace();
        }
        //TODO: Error processing;
        return null;
    }

    public InlineResponse2008 findChargebacks(ChargebackSearchQuery query) {
        return null;
    }

    public InlineResponse2009 findInvoices(InvoiceSearchQuery query) {
        return null;
    }

    public InlineResponse20011 findPayouts(PayoutSearchQuery query) {
        return null;
    }

    public InlineResponse20012 findRefunds(RefundSearchQuery query) {
        return null;
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

        return null;
    }

    private PaymentSearchResult.StatusEnum getStatus(InvoicePaymentStatus status) {
        if (status.isSetCancelled()) {
            return PaymentSearchResult.StatusEnum.CANCELLED;
        }

        if (status.isSetCaptured()) {
            return PaymentSearchResult.StatusEnum.CAPTURED;
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            return PaymentSearchResult.StatusEnum.PROCESSED;
        }

        if (status.isSetPending()) {
            return PaymentSearchResult.StatusEnum.PENDING;
        }

        if (status.isSetProcessed()) {
            return PaymentSearchResult.StatusEnum.PROCESSED;
        }

        if (status.isSetRefunded()) {
            return PaymentSearchResult.StatusEnum.REFUNDED;
        }

        throw new IllegalArgumentException("");

    }

    private String getAt(InvoicePaymentStatus status) {
        if (status.isSetCancelled()) {
            return status.getCancelled().getAt();
        }

        if (status.isSetCaptured()) {
            return status.getCaptured().getAt();
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            return status.getFailed().getAt();
        }

        if (status.isSetProcessed()) {
            return status.getProcessed().getAt();
        }

        if (status.isSetRefunded()) {
            return status.getRefunded().getAt();
        }

        return null;

    }
}
