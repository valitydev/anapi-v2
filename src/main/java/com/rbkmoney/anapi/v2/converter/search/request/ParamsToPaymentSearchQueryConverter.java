package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.damsel.domain.LegacyBankCardPaymentSystem;
import com.rbkmoney.damsel.domain.LegacyBankCardTokenProvider;
import com.rbkmoney.damsel.domain.LegacyTerminalPaymentProvider;
import com.rbkmoney.magista.InvoicePaymentStatus;
import com.rbkmoney.magista.PaymentParams;
import com.rbkmoney.magista.PaymentSearchQuery;
import com.rbkmoney.magista.PaymentToolType;
import com.rbkmoney.openapi.anapi_v2.model.BankCardPaymentSystem;
import com.rbkmoney.openapi.anapi_v2.model.BankCardTokenProvider;
import com.rbkmoney.openapi.anapi_v2.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;
import static com.rbkmoney.magista.InvoicePaymentFlowType.hold;
import static com.rbkmoney.magista.InvoicePaymentFlowType.instant;
import static com.rbkmoney.magista.InvoicePaymentStatus.*;
import static com.rbkmoney.magista.PaymentToolType.bank_card;
import static com.rbkmoney.magista.PaymentToolType.payment_terminal;

@Component
public class ParamsToPaymentSearchQueryConverter {

    public PaymentSearchQuery convert(String partyID,
                                      OffsetDateTime fromTime,
                                      OffsetDateTime toTime,
                                      Integer limit,
                                      List<String> shopIDs,
                                      List<String> invoiceIDs,
                                      String paymentStatus, String paymentFlow,
                                      String paymentMethod,
                                      String paymentTerminalProvider,
                                      String invoiceID,
                                      String paymentID,
                                      String externalID,
                                      String payerEmail,
                                      String payerIP,
                                      String payerFingerprint,
                                      String customerID,
                                      String first6,
                                      String last4,
                                      String rrn,
                                      String approvalCode,
                                      BankCardTokenProvider bankCardTokenProvider,
                                      BankCardPaymentSystem bankCardPaymentSystem,
                                      Long paymentAmountFrom,
                                      Long paymentAmountTo,
                                      List<String> excludedShops,
                                      String continuationToken) {
        PaymentSearchQuery query = new PaymentSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setExcludedShopIds(excludedShops)
                .setExternalId(externalID)
                .setInvoiceIds(merge(invoiceID, invoiceIDs));

        PaymentParams paymentParams = new PaymentParams()
                .setPaymentTool(paymentMethod != null ? mapToPaymentTool(paymentMethod) : null)
                .setPaymentFlow(paymentFlow != null ? mapToInvoicePaymentFlow(paymentFlow) : null)
                .setPaymentTerminalProvider(
                        paymentTerminalProvider != null
                                ? LegacyTerminalPaymentProvider.valueOf(paymentTerminalProvider) :
                                null)
                .setPaymentTokenProvider(
                        bankCardTokenProvider != null
                                ?
                                LegacyBankCardTokenProvider
                                        .valueOf(bankCardTokenProvider.getValue()) :
                                null)
                .setPaymentEmail(payerEmail)
                .setPaymentApprovalCode(approvalCode)
                .setPaymentCustomerId(customerID)
                .setPaymentFingerprint(payerFingerprint)
                .setPaymentFirst6(first6)
                .setPaymentLast4(last4)
                .setPaymentId(paymentID)
                .setPaymentIp(payerIP)
                .setPaymentRrn(rrn)
                .setPaymentStatus(paymentStatus != null ? mapStatus(paymentStatus) : null)
                .setPaymentSystem(bankCardPaymentSystem != null
                        ? LegacyBankCardPaymentSystem.valueOf(bankCardPaymentSystem.getValue()) :
                        null);
        if (paymentAmountFrom != null) {
            paymentParams.setPaymentAmountFrom(paymentAmountFrom);
        }
        if (paymentAmountTo != null) {
            paymentParams.setPaymentAmountTo(paymentAmountTo);
        }
        query.setPaymentParams(paymentParams);
        return query;
    }

    private PaymentToolType mapToPaymentTool(String paymentMethod) {
        return switch (paymentMethod) {
            case "bankCard" -> bank_card;
            case "paymentTerminal" -> payment_terminal;
            default -> throw new BadRequestException(
                    String.format("Payment method %s cannot be processed", paymentMethod));
        };
    }

    private com.rbkmoney.magista.InvoicePaymentFlowType mapToInvoicePaymentFlow(String paymentFlow) {
        return switch (paymentFlow) {
            case "instant" -> instant;
            case "hold" -> hold;
            default -> throw new BadRequestException(
                    String.format("Payment flow %s cannot be processed", paymentFlow));
        };
    }

    private InvoicePaymentStatus mapStatus(String paymentStatus) {
        var status = Enum.valueOf(PaymentStatus.StatusEnum.class, paymentStatus);
        return switch (status) {
            case PENDING -> pending;
            case PROCESSED -> processed;
            case CAPTURED -> captured;
            case CANCELLED -> cancelled;
            case REFUNDED -> refunded;
            case FAILED -> failed;
            default -> throw new BadRequestException(
                    String.format("Payment status %s cannot be processed", paymentStatus));
        };
    }
}
