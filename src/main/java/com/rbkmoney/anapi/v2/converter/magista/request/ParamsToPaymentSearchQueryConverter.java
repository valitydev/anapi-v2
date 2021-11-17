package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.model.PaymentStatus;
import com.rbkmoney.damsel.domain.LegacyBankCardPaymentSystem;
import com.rbkmoney.damsel.domain.LegacyBankCardTokenProvider;
import com.rbkmoney.damsel.domain.LegacyTerminalPaymentProvider;
import com.rbkmoney.magista.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;
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
                                      String bankCardTokenProvider,
                                      String bankCardPaymentSystem,
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
                .setPaymentTool(paymentMethod != null ? mapPaymentTool(paymentMethod) : null)
                .setPaymentFlow(paymentFlow != null ? mapInvoicePaymentFlow(paymentFlow) : null)
                .setPaymentTerminalProvider(
                        paymentTerminalProvider != null ? mapTerminalProvider(paymentTerminalProvider) : null)
                .setPaymentTokenProvider(
                        bankCardTokenProvider != null ? mapTokenProvider(bankCardTokenProvider) : null)
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
                        ? mapPaymentSystem(bankCardPaymentSystem) : null);
        if (paymentAmountFrom != null) {
            paymentParams.setPaymentAmountFrom(paymentAmountFrom);
        }
        if (paymentAmountTo != null) {
            paymentParams.setPaymentAmountTo(paymentAmountTo);
        }
        query.setPaymentParams(paymentParams);
        return query;
    }

    protected LegacyTerminalPaymentProvider mapTerminalProvider(String provider) {
        try {
            return LegacyTerminalPaymentProvider.valueOf(provider);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Terminal provider %s cannot be processed", provider));
        }
    }

    protected LegacyBankCardTokenProvider mapTokenProvider(String provider) {
        try {
            return LegacyBankCardTokenProvider.valueOf(provider);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Token provider %s cannot be processed", provider));
        }
    }

    protected LegacyBankCardPaymentSystem mapPaymentSystem(String system) {
        try {
            return LegacyBankCardPaymentSystem.valueOf(system);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Payment system %s cannot be processed", system));
        }
    }

    protected PaymentToolType mapPaymentTool(String paymentMethod) {
        return switch (paymentMethod) {
            case "bankCard" -> bank_card;
            case "paymentTerminal" -> payment_terminal;
            default -> throw new BadRequestException(
                    String.format("Payment method %s cannot be processed", paymentMethod));
        };
    }

    protected com.rbkmoney.magista.InvoicePaymentFlowType mapInvoicePaymentFlow(String paymentFlow) {
        try {
            return InvoicePaymentFlowType.valueOf(paymentFlow);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Payment flow %s cannot be processed", paymentFlow));
        }
    }

    protected InvoicePaymentStatus mapStatus(String paymentStatus) {
        try {
            var status = PaymentStatus.StatusEnum.fromValue(paymentStatus);
            return switch (status) {
                case PENDING -> pending;
                case PROCESSED -> processed;
                case CAPTURED -> captured;
                case CANCELLED -> cancelled;
                case REFUNDED -> refunded;
                case FAILED -> failed;
                case CHARGEDBACK -> charged_back;
            };
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Payment status %s cannot be processed", paymentStatus));
        }
    }
}
