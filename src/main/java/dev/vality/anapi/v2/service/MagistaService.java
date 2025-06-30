package dev.vality.anapi.v2.service;

import dev.vality.anapi.v2.converter.magista.response.*;
import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.exception.MagistaException;
import dev.vality.anapi.v2.model.*;
import dev.vality.damsel.base.InvalidRequest;
import dev.vality.magista.*;
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
    private final StatRefundToRefundSearchResultConverter refundResponseConverter;
    private final StatInvoiceTemplateToInvoiceTemplateConverter invoiceTemplateResponseConverter;

    public SearchInvoices200Response searchInvoices(InvoiceSearchQuery query) {
        try {
            StatInvoiceResponse magistaResponse = magistaClient.searchInvoices(query);
            return new SearchInvoices200Response()
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

    public SearchPayments200Response searchPayments(PaymentSearchQuery query) {
        try {
            StatPaymentResponse magistaResponse = magistaClient.searchPayments(query);
            return new SearchPayments200Response()
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

    public SearchRefunds200Response searchRefunds(RefundSearchQuery query) {
        try {
            StatRefundResponse magistaResponse = magistaClient.searchRefunds(query);
            return new SearchRefunds200Response()
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

    public SearchChargebacks200Response searchChargebacks(ChargebackSearchQuery query) {
        try {
            StatChargebackResponse magistaResponse = magistaClient.searchChargebacks(query);
            return new SearchChargebacks200Response()
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

    public SearchInvoiceTemplates200Response searchInvoiceTemplates(InvoiceTemplateSearchQuery query) {
        try {
            StatInvoiceTemplateResponse magistaResponse = magistaClient.searchInvoiceTemplates(query);
            return new SearchInvoiceTemplates200Response()
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
                .code(SearchRequestError.CodeEnum.BAD_CONTINUATION_TOKEN)
                .message(message);
        return new BadRequestException(message, e, error);
    }

    private BadRequestException limitExceededException(LimitExceeded e, String message) {
        var error = new SearchRequestError()
                .code(SearchRequestError.CodeEnum.LIMIT_EXCEEDED)
                .message(message);
        return new BadRequestException(message, e, error);
    }

    private BadRequestException invalidRequestException(InvalidRequest e, String message) {
        var error = new SearchRequestError()
                .code(SearchRequestError.CodeEnum.INVALID_REQUEST)
                .message(message);
        return new BadRequestException(message, e, error);
    }
}
