package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.util.OpenApiUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.anapi.v2.util.OpenApiUtil.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MerchantStatisticsServiceSrv.Iface magistaClient;

    @SneakyThrows
    public InlineResponse20010 findPayments(PaymentSearchQuery query) {
        StatPaymentResponse magistaResponse = magistaClient.searchPayments(query);
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
            fillStatusInfo(payment, result);
            results.add(result);
        }
        return new InlineResponse20010()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken());
    }

    private void fillStatusInfo(StatPayment payment, PaymentSearchResult result) {
        var status = payment.getStatus();
        if (status.isSetCancelled()) {
            OffsetDateTime createdAt = status.getCancelled().getAt() != null
                    ? TypeUtil.stringToInstant(status.getCancelled().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.CANCELLED)
                    .createdAt(createdAt);
        }

        if (status.isSetCaptured()) {
            OffsetDateTime createdAt = status.getCaptured().getAt() != null
                    ? TypeUtil.stringToInstant(status.getCaptured().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.CAPTURED)
                    .createdAt(createdAt);
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
        }

        if (status.isSetPending()) {
            result.status(PaymentSearchResult.StatusEnum.PENDING);
        }

        if (status.isSetProcessed()) {
            OffsetDateTime createdAt = status.getProcessed().getAt() != null
                    ? TypeUtil.stringToInstant(status.getProcessed().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.PROCESSED)
                    .createdAt(createdAt);
        }

        if (status.isSetRefunded()) {
            OffsetDateTime createdAt = status.getRefunded().getAt() != null
                    ? TypeUtil.stringToInstant(status.getRefunded().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.REFUNDED)
                    .createdAt(createdAt);
        }

        throw new IllegalArgumentException("");
    }

    @SneakyThrows
    public InlineResponse2008 findChargebacks(ChargebackSearchQuery query) {
        StatChargebackResponse magistaResponse = magistaClient.searchChargebacks(query);
        List<Chargeback> results = new ArrayList<>(magistaResponse.getChargebacksSize());
        for (StatChargeback chargeback : magistaResponse.getChargebacks()) {
            Chargeback result = new Chargeback()
                    .bodyAmount(chargeback.getAmount())
                    .createdAt(TypeUtil.stringToInstant(chargeback.getCreatedAt()).atOffset(ZoneOffset.UTC))
                    .chargebackId(chargeback.getChargebackId())
                    .fee(chargeback.getFee())
                    .chargebackReason(chargeback.getChargebackReason() != null
                            ? new ChargebackReason()
                            .category(mapToCategory(chargeback.getChargebackReason().getCategory()))
                            .code(chargeback.getChargebackReason().getCode()) : null)
                    .content(chargeback.getContent() != null
                            ? new Content().data(chargeback.getContent().getData())
                            .type(chargeback.getContent().getType()) : null)
                    .bodyCurrency(chargeback.getCurrencyCode().getSymbolicCode());
            results.add(result);
        }
        return new InlineResponse2008()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken());
    }


    @SneakyThrows
    public InlineResponse2009 findInvoices(InvoiceSearchQuery query) {
        StatInvoiceResponse magistaResponse = magistaClient.searchInvoices(query);
        List<Invoice> results = new ArrayList<>(magistaResponse.getInvoicesSize());
        for (StatInvoice invoice : magistaResponse.getInvoices()) {
            Invoice result = new Invoice()
                    .amount(invoice.getAmount())
                    .createdAt(TypeUtil.stringToInstant(invoice.getCreatedAt()).atOffset(ZoneOffset.UTC))
                    .currency(invoice.getCurrencySymbolicCode())
                    .externalID(invoice.getExternalId())
                    .cart(invoice.getCart() != null
                            ? invoice.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                                    .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                                    .price(invoiceLine.getPrice().getAmount())
                                    .product(invoiceLine.getProduct())
                            //.getTaxMode()
                    ).collect(Collectors.toList()) : null)
                    .description(invoice.getDescription())
                    .dueDate(TypeUtil.stringToInstant(invoice.getDue()).atOffset(ZoneOffset.UTC))
                    .id(invoice.getId())
                    .product(invoice.getProduct())
                    //.reason()
                    .shopID(invoice.getShopId())
                    .status(OpenApiUtil.mapToInvoiceStatus(invoice.getStatus()));
            results.add(result);
        }
        return new InlineResponse2009()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken());
    }

    @SneakyThrows
    public InlineResponse20011 findPayouts(PayoutSearchQuery query) {
        StatPayoutResponse magistaResponse = magistaClient.searchPayouts(query);
        List<Payout> results = new ArrayList<>(magistaResponse.getPayoutsSize());
        for (StatPayout payout : magistaResponse.getPayouts()) {
            Payout result = new Payout()
                    .amount(payout.getAmount())
                    .createdAt(TypeUtil.stringToInstant(payout.getCreatedAt()).atOffset(ZoneOffset.UTC))
                    .currency(payout.getCurrencySymbolicCode())
                    .fee(payout.getFee())
                    .id(payout.getId())
                    .payoutToolDetails(mapToPayoutToolDetails(payout.getPayoutToolInfo()))
                    .shopID(payout.getShopId())
                    .status(OpenApiUtil.mapToPayoutStatus(payout.getStatus()))
                    .cancellationDetails(
                            payout.getStatus().isSetCancelled() ? payout.getStatus().getCancelled().getDetails() :
                                    null);
            results.add(result);
        }
        return new InlineResponse20011()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken());
    }

    @SneakyThrows
    public InlineResponse20012 findRefunds(RefundSearchQuery query) {
        StatRefundResponse magistaResponse = magistaClient.searchRefunds(query);
        List<RefundSearchResult> results = new ArrayList<>(magistaResponse.getRefundsSize());
        for (StatRefund refund : magistaResponse.getRefunds()) {
            RefundSearchResult result = new RefundSearchResult()
                    .amount(refund.getAmount())
                    .createdAt(TypeUtil.stringToInstant(refund.getCreatedAt()).atOffset(ZoneOffset.UTC))
                    .currency(refund.getCurrencySymbolicCode())
                    .id(refund.getId())
                    .shopID(refund.getShopId())
                    .status(mapToRefundStatus(refund.getStatus()))
                    .externalID(refund.getExternalId())
                    .error(refund.getStatus().isSetFailed()
                            && refund.getStatus().getFailed().getFailure().isSetFailure()
                            ? new RefundStatusError()
                            .code(refund.getStatus().getFailed().getFailure().getFailure().getCode())
                            .message(refund.getStatus().getFailed().getFailure().getFailure().getReason())
                            : null)
                    .invoiceID(refund.getInvoiceId())
                    .paymentID(refund.getPaymentId())
                    .reason(refund.getReason());
            results.add(result);
        }
        return new InlineResponse20012()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken());
    }

}
