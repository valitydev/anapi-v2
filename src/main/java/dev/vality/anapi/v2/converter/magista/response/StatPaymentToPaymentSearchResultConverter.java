package dev.vality.anapi.v2.converter.magista.response;

import dev.vality.anapi.v2.model.ClientInfo;
import dev.vality.anapi.v2.model.ContactInfo;
import dev.vality.anapi.v2.model.CryptoCurrency;
import dev.vality.anapi.v2.model.CustomerPayer;
import dev.vality.anapi.v2.model.Payer;
import dev.vality.anapi.v2.model.PaymentResourcePayer;
import dev.vality.anapi.v2.model.RecurrentPayer;
import dev.vality.anapi.v2.model.TransactionInfo;
import dev.vality.anapi.v2.util.MaskUtil;
import dev.vality.damsel.domain.*;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.magista.InvoicePaymentFlow;
import dev.vality.magista.StatPayment;
import dev.vality.anapi.v2.model.*;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

import static dev.vality.anapi.v2.model.PaymentSearchResult.StatusEnum.*;

@Component
public class StatPaymentToPaymentSearchResultConverter {

    public PaymentSearchResult convert(StatPayment payment) {
        return new PaymentSearchResult()
                .amount(payment.getAmount())
                .createdAt(TypeUtil.stringToInstant(payment.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(payment.getCurrencySymbolicCode())
                .externalID(payment.getExternalId())
                .fee(payment.getFee())
                .flow(mapFlow(payment.getFlow()))
                .status(mapStatus(payment.getStatus()))
                .error(payment.getStatus().isSetFailed()
                        ? mapError(payment.getStatus().getFailed().getFailure())
                        : null)
                .statusChangedAt(payment.isSetStatusChangedAt()
                        ? TypeUtil.stringToInstant(payment.getStatusChangedAt()).atOffset(ZoneOffset.UTC) : null)
                .id(payment.getId())
                .invoiceID(payment.getInvoiceId())
                .makeRecurrent(payment.isMakeRecurrent())
                .payer(mapPayer(payment.getPayer()))
                .shopID(payment.getShopId())
                .shortID(payment.getShortId())
                .transactionInfo(payment.isSetAdditionalTransactionInfo()
                        ? new TransactionInfo()
                        .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                        .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        : null);
    }

    protected PaymentFlow mapFlow(InvoicePaymentFlow flow) {
        if (flow.isSetHold()) {
            var hold = flow.getHold();
            return new PaymentFlowHold()
                    .heldUntil(TypeUtil.stringToInstant(hold.getHeldUntil()).atOffset(ZoneOffset.UTC))
                    .onHoldExpiration(
                            PaymentFlowHold.OnHoldExpirationEnum.fromValue(hold.getOnHoldExpiration().name()))
                    .type(PaymentFlow.TypeEnum.PAYMENTFLOWHOLD);
        } else {
            return new PaymentFlowInstant()
                    .type(PaymentFlow.TypeEnum.PAYMENTFLOWINSTANT);
        }
    }

    protected Payer mapPayer(dev.vality.magista.Payer payer) {
        try {
            var field = dev.vality.magista.Payer._Fields.findByName(payer.getSetField().getFieldName());
            switch (field) {
                case CUSTOMER -> {
                    var customer = payer.getCustomer();
                    var paymentTool = customer.getPaymentTool();
                    return new CustomerPayer()
                            .customerID(customer.getCustomerId())
                            .paymentToolDetails(mapPaymentToolDetails(paymentTool))
                            .paymentToolToken(getPaymentToolToken(paymentTool));
                }
                case PAYMENT_RESOURCE -> {
                    var resource = payer.getPaymentResource();
                    var clientInfo = resource.getResource().getClientInfo();
                    var contactInfo = resource.getContactInfo();
                    var paymentTool = resource.getResource().getPaymentTool();
                    return new PaymentResourcePayer()
                            .paymentToolDetails(mapPaymentToolDetails(paymentTool))
                            .paymentToolToken(getPaymentToolToken(paymentTool))
                            .paymentSession(resource.getResource().getPaymentSessionId())
                            .clientInfo(resource.getResource().isSetClientInfo()
                                    ? new ClientInfo()
                                    .fingerprint(clientInfo.getFingerprint())
                                    .ip(clientInfo.getIpAddress())
                                    : null)
                            .contactInfo(new ContactInfo()
                                    .email(contactInfo.getEmail())
                                    .phoneNumber(contactInfo.getPhoneNumber()));
                }
                case RECURRENT -> {
                    var recurrent = payer.getRecurrent();
                    var contactInfo = recurrent.getContactInfo();
                    var paymentTool = recurrent.getPaymentTool();
                    return new RecurrentPayer()
                            .paymentToolDetails(mapPaymentToolDetails(paymentTool))
                            .paymentToolToken(getPaymentToolToken(paymentTool))
                            .contactInfo(new ContactInfo()
                                    .email(contactInfo.getEmail())
                                    .phoneNumber(contactInfo.getPhoneNumber()))
                            .recurrentParentPayment(new PaymentRecurrentParent()
                                    .paymentID(recurrent.getRecurrentParent().getPaymentId())
                                    .invoiceID(recurrent.getRecurrentParent().getInvoiceId()));
                }
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Payer %s cannot be processed", payer));
        }
    }

    protected String getPaymentToolToken(PaymentTool paymentTool) {
        var field = dev.vality.damsel.domain.PaymentTool._Fields.findByName(paymentTool.getSetField().getFieldName());
        return switch (field) {
            case BANK_CARD -> paymentTool.getBankCard().getToken();
            case DIGITAL_WALLET -> paymentTool.getDigitalWallet().getToken();
            default -> null;
        };
    }

    protected PaymentToolDetails mapPaymentToolDetails(PaymentTool paymentTool) {
        var field = dev.vality.damsel.domain.PaymentTool._Fields.findByName(paymentTool.getSetField().getFieldName());
        switch (field) {
            case BANK_CARD -> {
                var card = paymentTool.getBankCard();
                return new PaymentToolDetailsBankCard()
                        .bin(card.getBin())
                        .paymentSystem(card.isSetPaymentSystemDeprecated()
                                ? BankCardPaymentSystem.fromValue(card.getPaymentSystemDeprecated().name())
                                : null)
                        .cardNumberMask(MaskUtil.constructCardNumber(card))
                        .lastDigits(card.getLastDigits())
                        .tokenProvider(card.isSetTokenProviderDeprecated()
                                ? BankCardTokenProvider.fromValue(card.getTokenProviderDeprecated().name())
                                : null);
            }
            case PAYMENT_TERMINAL -> {
                var terminal = paymentTool.getPaymentTerminal();
                return new PaymentToolDetailsPaymentTerminal()
                        .provider(terminal.isSetTerminalTypeDeprecated()
                                ? PaymentTerminalProvider.fromValue(terminal.getTerminalTypeDeprecated().name())
                                : null);
            }
            case MOBILE_COMMERCE -> {
                var mobile = paymentTool.getMobileCommerce();
                return new PaymentToolDetailsMobileCommerce()
                        .phoneNumber(MaskUtil.constructPhoneNumber(mobile.getPhone()));
            }
            case CRYPTO_CURRENCY_DEPRECATED -> {
                var cryptoCurrency = paymentTool.getCryptoCurrencyDeprecated();
                return new PaymentToolDetailsCryptoWallet()
                        .cryptoCurrency(CryptoCurrency.fromValue(cryptoCurrency.name()));
            }
            default -> throw new IllegalArgumentException();
        }
    }

    protected PaymentSearchResult.StatusEnum mapStatus(InvoicePaymentStatus status) {
        try {
            var field = InvoicePaymentStatus._Fields.findByName(status.getSetField().getFieldName());
            return switch (field) {
                case PENDING -> PENDING;
                case PROCESSED -> PROCESSED;
                case CAPTURED -> CAPTURED;
                case CANCELLED -> CANCELLED;
                case REFUNDED -> REFUNDED;
                case FAILED -> FAILED;
                case CHARGED_BACK -> CHARGEDBACK;
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Payment status %s cannot be processed", status));
        }

    }

    protected PaymentError mapError(OperationFailure failure) {
        var error = new PaymentError();
        switch (failure.getSetField()) {
            case FAILURE -> {
                error.setCode(failure.getFailure().getCode());
                if (failure.getFailure().isSetSub()) {
                    SubFailure subFailure = failure.getFailure().getSub();
                    error.setSubError(createSubError(subFailure));
                }
            }
            case OPERATION_TIMEOUT -> error.setCode("timeout");
            default -> throw new IllegalArgumentException("Unknown failure: " + failure.getSetField().getFieldName());
        }

        return error;
    }

    private SubError createSubError(SubFailure subFailure) {
        SubError subError = new SubError();
        subError.setCode(subFailure.getCode());

        if (subFailure.isSetSub()) {
            subError.setSubError(createSubError(subFailure.getSub()));
        }
        return subError;
    }
}
