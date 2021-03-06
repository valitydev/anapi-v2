package dev.vality.anapi.v2.converter.magista.request;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.model.PaymentStatus;
import dev.vality.anapi.v2.util.ConverterUtil;
import dev.vality.damsel.domain.BankCardTokenServiceRef;
import dev.vality.damsel.domain.PaymentServiceRef;
import dev.vality.damsel.domain.PaymentSystemRef;
import dev.vality.magista.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static dev.vality.magista.InvoicePaymentStatus.*;
import static dev.vality.magista.PaymentToolType.bank_card;
import static dev.vality.magista.PaymentToolType.payment_terminal;

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
        List<String> invoiceIds = ConverterUtil.merge(invoiceID, invoiceIDs);
        PaymentSearchQuery query = new PaymentSearchQuery()
                .setCommonSearchQueryParams(
                        ConverterUtil.fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setExcludedShopIds(excludedShops)
                .setExternalId(externalID)
                .setInvoiceIds(invoiceIds.isEmpty() ? null : invoiceIds);

        PaymentParams paymentParams = new PaymentParams()
                .setPaymentTool(paymentMethod != null ? mapPaymentTool(paymentMethod) : null)
                .setPaymentFlow(paymentFlow != null ? mapInvoicePaymentFlow(paymentFlow) : null)
                .setPaymentTerminalProvider(
                        paymentTerminalProvider != null ? new PaymentServiceRef(paymentTerminalProvider) : null)
                .setPaymentTokenProvider(
                        bankCardTokenProvider != null ? new BankCardTokenServiceRef(bankCardTokenProvider) : null)
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
                .setPaymentSystem(bankCardPaymentSystem != null ? new PaymentSystemRef(bankCardPaymentSystem) : null);
        if (paymentAmountFrom != null) {
            paymentParams.setPaymentAmountFrom(paymentAmountFrom);
        }
        if (paymentAmountTo != null) {
            paymentParams.setPaymentAmountTo(paymentAmountTo);
        }
        query.setPaymentParams(paymentParams);
        return query;
    }

    protected PaymentToolType mapPaymentTool(String paymentMethod) {
        return switch (paymentMethod) {
            case "bankCard" -> bank_card;
            case "paymentTerminal" -> payment_terminal;
            default -> throw new BadRequestException(
                    String.format("Payment method %s cannot be processed", paymentMethod));
        };
    }

    protected dev.vality.magista.InvoicePaymentFlowType mapInvoicePaymentFlow(String paymentFlow) {
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
