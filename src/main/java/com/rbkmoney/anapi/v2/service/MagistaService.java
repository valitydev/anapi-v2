package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.converter.magista.response.*;
import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.exception.MagistaException;
import com.rbkmoney.anapi.v2.model.*;
import com.rbkmoney.damsel.base.InvalidRequest;
import com.rbkmoney.magista.*;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MagistaService {

    private final MerchantStatisticsServiceSrv.Iface magistaClient;
    private final StatPaymentToPaymentSearchResultConverter paymentResponseConverter;
    private final StatChargebackToChargebackConverter chargebackResponseConverter;
    private final StatInvoiceToInvoiceConverter invoiceResponseConverter;
    private final StatPayoutToPayoutConverter payoutResponseConverter;
    private final StatRefundToRefundSearchResultConverter refundResponseConverter;
    private final StatInvoiceTemplateToInvoiceTemplateConverter invoiceTemplateResponseConverter;

    public InlineResponse2008 searchInvoices(InvoiceSearchQuery query) {
        try {
            StatInvoiceResponse magistaResponse = magistaClient.searchInvoices(query);
            return new InlineResponse2008()
                    .result(magistaResponse.getInvoices().stream()
                            .map(invoiceResponseConverter::convert)
                            .collect(Collectors.toList()))
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (BadContinuationToken e) {
            var message = String.format(
                    "Bad token exceeded, partyId=%s, invoiceIds=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getInvoiceIds());
            throw badContinuationTokenException(e, message);
        } catch (LimitExceeded e) {
            var message = String.format(
                    "Limit exceeded, partyId=%s, invoiceIds=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getInvoiceIds());
            throw limitExceededException(e, message);
        } catch (InvalidRequest e) {
            var message = String.format(
                    "Invalid request, partyId=%s, invoiceIds=%s, errors=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getInvoiceIds(),
                    String.join(", ", e.getErrors()));
            throw invalidRequestException(e, message);
        } catch (TException e) {
            throw new MagistaException(
                    String.format("Error while call magistaClient.searchInvoices, partyId=%s, invoiceIds=%s",
                            query.getCommonSearchQueryParams().getPartyId(), query.getInvoiceIds()),
                    e);
        }
    }

    public InlineResponse2009 searchPayments(PaymentSearchQuery query) {
        try {
            StatPaymentResponse magistaResponse = magistaClient.searchPayments(query);
            return new InlineResponse2009()
                    .result(magistaResponse.getPayments().stream()
                            .map(paymentResponseConverter::convert)
                            .collect(Collectors.toList()))
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (BadContinuationToken e) {
            var message = String.format(
                    "Bad token exceeded, partyId=%s, paymentId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getPaymentParams().getPaymentId());
            throw badContinuationTokenException(e, message);
        } catch (LimitExceeded e) {
            var message = String.format(
                    "Limit exceeded, partyId=%s, paymentId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getPaymentParams().getPaymentId());
            throw limitExceededException(e, message);
        } catch (InvalidRequest e) {
            var message = String.format(
                    "Invalid request, partyId=%s, paymentId=%s, errors=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getPaymentParams().getPaymentId(),
                    String.join(", ", e.getErrors()));
            throw invalidRequestException(e, message);
        } catch (TException e) {
            throw new MagistaException(
                    String.format("Error while call magistaClient.searchPayments, partyId=%s, paymentId=%s",
                            query.getCommonSearchQueryParams().getPartyId(), query.getPaymentParams().getPaymentId()),
                    e);
        }
    }

    public InlineResponse20010 searchRefunds(RefundSearchQuery query) {
        try {
            StatRefundResponse magistaResponse = magistaClient.searchRefunds(query);
            return new InlineResponse20010()
                    .result(magistaResponse.getRefunds().stream()
                            .map(refundResponseConverter::convert)
                            .collect(Collectors.toList()))
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (BadContinuationToken e) {
            var message = String.format(
                    "Bad token exceeded, partyId=%s, refundId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getRefundId());
            throw badContinuationTokenException(e, message);
        } catch (LimitExceeded e) {
            var message = String.format(
                    "Limit exceeded, partyId=%s, refundId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getRefundId());
            throw limitExceededException(e, message);
        } catch (InvalidRequest e) {
            var message = String.format(
                    "Invalid request, partyId=%s, refundId=%s, errors=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getRefundId(),
                    String.join(", ", e.getErrors()));
            throw invalidRequestException(e, message);
        } catch (TException e) {
            throw new MagistaException(
                    String.format("Error while call magistaClient.searchRefunds, partyId=%s, refundId=%s",
                            query.getCommonSearchQueryParams().getPartyId(), query.getRefundId()),
                    e);
        }
    }

    public InlineResponse20011 searchChargebacks(ChargebackSearchQuery query) {
        try {
            StatChargebackResponse magistaResponse = magistaClient.searchChargebacks(query);
            return new InlineResponse20011()
                    .result(magistaResponse.getChargebacks().stream()
                            .map(chargebackResponseConverter::convert)
                            .collect(Collectors.toList()))
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (BadContinuationToken e) {
            var message = String.format(
                    "Bad token exceeded, partyId=%s, chargebackId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getChargebackId());
            throw badContinuationTokenException(e, message);
        } catch (LimitExceeded e) {
            var message = String.format(
                    "Limit exceeded, partyId=%s, chargebackId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getChargebackId());
            throw limitExceededException(e, message);
        } catch (InvalidRequest e) {
            var message = String.format(
                    "Invalid request, partyId=%s, chargebackId=%s, errors=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getChargebackId(),
                    String.join(", ", e.getErrors()));
            throw invalidRequestException(e, message);
        } catch (TException e) {
            throw new MagistaException(
                    String.format("Error while call magistaClient.searchChargebacks, partyId=%s, chargebackId=%s",
                            query.getCommonSearchQueryParams().getPartyId(), query.getChargebackId()),
                    e);
        }
    }

    public InlineResponse20012 searchPayouts(PayoutSearchQuery query) {
        try {
            StatPayoutResponse magistaResponse = magistaClient.searchPayouts(query);
            return new InlineResponse20012()
                    .result(magistaResponse.getPayouts().stream()
                            .map(payoutResponseConverter::convert)
                            .collect(Collectors.toList()))
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (BadContinuationToken e) {
            var message = String.format(
                    "Bad token exceeded, partyId=%s, payoutId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getPayoutId());
            throw badContinuationTokenException(e, message);
        } catch (LimitExceeded e) {
            var message = String.format(
                    "Limit exceeded, partyId=%s, payoutId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getPayoutId());
            throw limitExceededException(e, message);
        } catch (InvalidRequest e) {
            var message = String.format(
                    "Invalid request, partyId=%s, payoutId=%s, errors=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getPayoutId(),
                    String.join(", ", e.getErrors()));
            throw invalidRequestException(e, message);
        } catch (TException e) {
            throw new MagistaException(
                    String.format("Error while call magistaClient.searchPayouts, partyId=%s, payoutId=%s",
                            query.getCommonSearchQueryParams().getPartyId(), query.getPayoutId()),
                    e);
        }
    }

    public InlineResponse20013 searchInvoiceTemplates(InvoiceTemplateSearchQuery query) {
        try {
            StatInvoiceTemplateResponse magistaResponse = magistaClient.searchInvoiceTemplates(query);
            return new InlineResponse20013()
                    .result(magistaResponse.getInvoiceTemplates().stream()
                            .map(invoiceTemplateResponseConverter::convert)
                            .collect(Collectors.toList()))
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (BadContinuationToken e) {
            var message = String.format(
                    "Bad token exceeded, partyId=%s, invoiceTemplateId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getInvoiceTemplateId());
            throw badContinuationTokenException(e, message);
        } catch (LimitExceeded e) {
            var message = String.format(
                    "Limit exceeded, partyId=%s, invoiceTemplateId=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getInvoiceTemplateId());
            throw limitExceededException(e, message);
        } catch (InvalidRequest e) {
            var message = String.format(
                    "Invalid request, partyId=%s, invoiceTemplateId=%s, errors=%s",
                    query.getCommonSearchQueryParams().getPartyId(),
                    query.getInvoiceTemplateId(),
                    String.join(", ", e.getErrors()));
            throw invalidRequestException(e, message);
        } catch (TException e) {
            throw new MagistaException(
                    String.format("Error while call magistaClient.searchInvoiceTemplates, " +
                                    "partyId=%s, invoiceTemplateId=%s",
                            query.getCommonSearchQueryParams().getPartyId(), query.getInvoiceTemplateId()),
                    e);
        }
    }

    private BadRequestException badContinuationTokenException(BadContinuationToken e, String message) {
        var error = new SearchRequestError()
                .code(SearchRequestError.CodeEnum.BADCONTINUATIONTOKEN)
                .message(message);
        return new BadRequestException(message, e, error);
    }

    private BadRequestException limitExceededException(LimitExceeded e, String message) {
        var error = new SearchRequestError()
                .code(SearchRequestError.CodeEnum.LIMITEXCEEDED)
                .message(message);
        return new BadRequestException(message, e, error);
    }

    private BadRequestException invalidRequestException(InvalidRequest e, String message) {
        var error = new SearchRequestError()
                .code(SearchRequestError.CodeEnum.INVALIDREQUEST)
                .message(message);
        return new BadRequestException(message, e, error);
    }
}
