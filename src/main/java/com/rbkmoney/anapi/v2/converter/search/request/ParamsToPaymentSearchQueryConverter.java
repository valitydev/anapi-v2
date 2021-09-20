package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.PaymentParams;
import com.rbkmoney.magista.PaymentSearchQuery;
import com.rbkmoney.openapi.anapi_v2.model.BankCardPaymentSystem;
import com.rbkmoney.openapi.anapi_v2.model.BankCardTokenProvider;
import com.rbkmoney.openapi.anapi_v2.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;

@Component
public class ParamsToPaymentSearchQueryConverter {

    public PaymentSearchQuery convert(String partyID,
                                      OffsetDateTime fromTime,
                                      OffsetDateTime toTime,
                                      Integer limit,
                                      String shopID,
                                      List<String> shopIDs,
                                      String paymentInstitutionRealm,
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
        //TODO: Mapping for paymentInstitutionRealm
        PaymentSearchQuery query = new PaymentSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs),
                                continuationToken))
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

    private PaymentTool mapToPaymentTool(String paymentMethod) {
        var paymentTool = new PaymentTool();
        switch (paymentMethod) {
            case "bankCard" -> paymentTool.setBankCard(new BankCard());
            case "paymentTerminal" -> paymentTool.setPaymentTerminal(new PaymentTerminal());
            default -> throw new BadRequestException(
                    String.format("Payment method %s cannot be processed", paymentMethod));
        }

        return paymentTool;
    }

    private InvoicePaymentFlow mapToInvoicePaymentFlow(String paymentFlow) {
        var invoicePaymentFlow = new InvoicePaymentFlow();
        switch (paymentFlow) {
            case "instant" -> invoicePaymentFlow.setInstant(new InvoicePaymentFlowInstant());
            case "hold" -> invoicePaymentFlow.setHold(new InvoicePaymentFlowHold());
            default -> throw new BadRequestException(
                    String.format("Payment flow %s cannot be processed", paymentFlow));
        }
        return invoicePaymentFlow;
    }

    private InvoicePaymentStatus mapStatus(String paymentStatus) {
        var status = Enum.valueOf(PaymentStatus.StatusEnum.class, paymentStatus);
        var invoicePaymentStatus = new com.rbkmoney.damsel.domain.InvoicePaymentStatus();
        switch (status) {
            case PENDING -> invoicePaymentStatus.setPending(new InvoicePaymentPending());
            case PROCESSED -> invoicePaymentStatus.setProcessed(new InvoicePaymentProcessed());
            case CAPTURED -> invoicePaymentStatus.setCaptured(new InvoicePaymentCaptured());
            case CANCELLED -> invoicePaymentStatus.setCancelled(new InvoicePaymentCancelled());
            case REFUNDED -> invoicePaymentStatus.setRefunded(new InvoicePaymentRefunded());
            case FAILED -> invoicePaymentStatus.setFailed(new InvoicePaymentFailed());
            default -> throw new BadRequestException(
                    String.format("Payment status %s cannot be processed", paymentStatus));
        }
        return invoicePaymentStatus;
    }
}
