package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.converter.search.response.StatPaymentToPaymentSearchResultConverter;
import com.rbkmoney.anapi.v2.util.OpenApiUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.rbkmoney.anapi.v2.util.OpenApiUtil.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MerchantStatisticsServiceSrv.Iface magistaClient;

    private final StatPaymentToPaymentSearchResultConverter paymentResponseConverter;

    @Async
    @SneakyThrows
    public Future<InlineResponse20010> findPayments(PaymentSearchQuery query) {
        StatPaymentResponse magistaResponse = magistaClient.searchPayments(query);
        return new AsyncResult<>(new InlineResponse20010()
                .result(magistaResponse.getPayments().stream()
                        .map(paymentResponseConverter::convert)
                        .collect(Collectors.toList()))
                .continuationToken(magistaResponse.getContinuationToken()));
    }

    @Async
    @SneakyThrows
    public Future<InlineResponse2008> findChargebacks(ChargebackSearchQuery query) {
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
                            .category(mapToChargebackCategory(chargeback.getChargebackReason().getCategory()))
                            .code(chargeback.getChargebackReason().getCode()) : null)
                    .content(chargeback.getContent() != null
                            ? new Content().data(chargeback.getContent().getData())
                            .type(chargeback.getContent().getType()) : null)
                    .bodyCurrency(chargeback.getCurrencyCode().getSymbolicCode());
            results.add(result);
        }
        return new AsyncResult<>(new InlineResponse2008()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken()));
    }


    @Async
    @SneakyThrows
    public Future<InlineResponse2009> findInvoices(InvoiceSearchQuery query) {
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
        return new AsyncResult<>(new InlineResponse2009()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken()));
    }

    @Async
    @SneakyThrows
    public Future<InlineResponse20011> findPayouts(PayoutSearchQuery query) {
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
        return new AsyncResult<>(new InlineResponse20011()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken()));
    }

    @Async
    @SneakyThrows
    public Future<InlineResponse20012> findRefunds(RefundSearchQuery query) {
        StatRefundResponse magistaResponse = magistaClient.searchRefunds(query);
        List<RefundSearchResult> results = new ArrayList<>(magistaResponse.getRefundsSize());
        for (StatRefund refund : magistaResponse.getRefunds()) {
            RefundSearchResult result = new RefundSearchResult()
                    .amount(refund.getAmount())
                    .createdAt(TypeUtil.stringToInstant(refund.getCreatedAt()).atOffset(ZoneOffset.UTC))
                    .currency(refund.getCurrencySymbolicCode())
                    .id(refund.getId())
                    .shopID(refund.getShopId())
                    .status(refund.getStatus() != null ? mapToRefundStatus(refund.getStatus()) : null)
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
        return new AsyncResult<>(new InlineResponse20012()
                .result(results)
                .continuationToken(magistaResponse.getContinuationToken()));
    }

}
