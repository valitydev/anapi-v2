package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.converter.search.response.*;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MerchantStatisticsServiceSrv.Iface magistaClient;
    private final StatPaymentToPaymentSearchResultConverter paymentResponseConverter;
    private final StatChargebackToChargebackConverter chargebackResponseConverter;
    private final StatInvoiceToInvoiceConverter invoiceResponseConverter;
    private final StatPayoutToPayoutConverter payoutResponseConverter;
    private final StatRefundToRefundSearchResultConverter refundResponseConverter;

    @SneakyThrows
    public InlineResponse20010 findPayments(PaymentSearchQuery query) {
        StatPaymentResponse magistaResponse = magistaClient.searchPayments(query);
        return new InlineResponse20010()
                .result(magistaResponse.getPayments().stream()
                        .map(paymentResponseConverter::convert)
                        .collect(Collectors.toList()))
                .continuationToken(magistaResponse.getContinuationToken());
    }

    @SneakyThrows
    public InlineResponse2008 findChargebacks(ChargebackSearchQuery query) {
        StatChargebackResponse magistaResponse = magistaClient.searchChargebacks(query);
        return new InlineResponse2008()
                .result(magistaResponse.getChargebacks().stream()
                        .map(chargebackResponseConverter::convert)
                        .collect(Collectors.toList()))
                .continuationToken(magistaResponse.getContinuationToken());
    }


    @SneakyThrows
    public InlineResponse2009 findInvoices(InvoiceSearchQuery query) {
        StatInvoiceResponse magistaResponse = magistaClient.searchInvoices(query);
        return new InlineResponse2009()
                .result(magistaResponse.getInvoices().stream()
                        .map(invoiceResponseConverter::convert)
                        .collect(Collectors.toList()))
                .continuationToken(magistaResponse.getContinuationToken());
    }

    @SneakyThrows
    public InlineResponse20011 findPayouts(PayoutSearchQuery query) {
        StatPayoutResponse magistaResponse = magistaClient.searchPayouts(query);
        return new InlineResponse20011()
                .result(magistaResponse.getPayouts().stream()
                        .map(payoutResponseConverter::convert)
                        .collect(Collectors.toList()))
                .continuationToken(magistaResponse.getContinuationToken());
    }

    @SneakyThrows
    public InlineResponse20012 findRefunds(RefundSearchQuery query) {
        StatRefundResponse magistaResponse = magistaClient.searchRefunds(query);
        return new InlineResponse20012()
                .result(magistaResponse.getRefunds().stream()
                        .map(refundResponseConverter::convert)
                        .collect(Collectors.toList()))
                .continuationToken(magistaResponse.getContinuationToken());
    }

}
