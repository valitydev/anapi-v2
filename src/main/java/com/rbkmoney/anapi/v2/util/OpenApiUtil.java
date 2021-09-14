package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.damsel.domain.InvoicePaymentChargebackCategory;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.magista.InvoiceStatus;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.model.Payer;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenApiUtil {

    public static ChargebackCategory mapToCategory(InvoicePaymentChargebackCategory chargebackCategory) {
        if (chargebackCategory.isSetAuthorisation()) {
            return ChargebackCategory.AUTHORISATION;
        }

        if (chargebackCategory.isSetDispute()) {
            return ChargebackCategory.DISPUTE;
        }

        if (chargebackCategory.isSetFraud()) {
            return ChargebackCategory.FRAUD;
        }

        if (chargebackCategory.isSetProcessingError()) {
            return ChargebackCategory.PROCESSING_ERROR;
        }

        return null;
    }

    public static Invoice.StatusEnum mapToInvoiceStatus(InvoiceStatus status) {
        if (status.isSetFulfilled()) {
            return Invoice.StatusEnum.FULFILLED;
        }

        if (status.isSetPaid()) {
            return Invoice.StatusEnum.PAID;
        }

        if (status.isSetUnpaid()) {
            return Invoice.StatusEnum.UNPAID;
        }

        if (status.isSetCancelled()) {
            return Invoice.StatusEnum.CANCELLED;
        }

        throw new IllegalArgumentException("");
    }

    public static String mapToPayoutStatus(PayoutStatus status) {
        if (status.isSetCancelled()) {
            return "Cancelled";
        }

        if (status.isSetPaid()) {
            return "Paid";
        }

        if (status.isSetConfirmed()) {
            return "Confirmed";
        }

        if (status.isSetUnpaid()) {
            return "Unpaid";
        }

        throw new IllegalArgumentException("");
    }

    public static PayoutToolDetails mapToPayoutToolDetails(PayoutToolInfo payoutToolInfo) {
        if (payoutToolInfo.isSetRussianBankAccount()) {
            var account = payoutToolInfo.getRussianBankAccount();
            return new PayoutToolDetailsBankAccount()
                    .account(account.getAccount())
                    .bankBik(account.getBankBik())
                    .bankName(account.getBankName())
                    .bankPostAccount(account.getBankPostAccount())
                    .detailsType("PayoutToolDetailsBankAccount");
        }

        if (payoutToolInfo.isSetInternationalBankAccount()) {
            var account = payoutToolInfo.getInternationalBankAccount();
            return new PayoutToolDetailsInternationalBankAccount()
                    .iban(account.getIban())
                    .number(account.getNumber())
                    .bankDetails(account.getBank() != null
                            ? new InternationalBankDetails()
                            .name(account.getBank().getName())
                            .bic(account.getBank().getBic())
                            .countryCode(account.getBank().getCountry() != null
                                    ? account.getBank().getCountry().name() : null)
                            .address(account.getBank().getAddress())
                            .abartn(account.getBank().getAbaRtn())
                            : null)
                    .correspondentBankAccount(mapToInternationalCorrespondentBankAccount(account))
                    .detailsType("PayoutToolDetailsInternationalBankAccount");
        }

        if (payoutToolInfo.isSetPaymentInstitutionAccount()) {
            return new PayoutToolDetailsPaymentInstitutionAccount()
                    .detailsType("PayoutToolDetailsPaymentInstitutionAccount");
        }

        if (payoutToolInfo.isSetWalletInfo()) {
            return new PayoutToolDetailsWalletInfo()
                    .walletID(payoutToolInfo.getWalletInfo().getWalletId())
                    .detailsType("PayoutToolDetailsWalletInfo");
        }

        throw new IllegalArgumentException("");

    }

    public static InternationalCorrespondentBankAccount mapToInternationalCorrespondentBankAccount(
            com.rbkmoney.damsel.domain.InternationalBankAccount account) {
        var details = account.getBank();
        return new InternationalCorrespondentBankAccount()
                .bankDetails(details != null
                        ? new InternationalBankDetails()
                        .name(details.getName())
                        .bic(details.getBic())
                        .countryCode(details.getCountry().name())
                        .address(details.getAddress())
                        .abartn(details.getAbaRtn())
                        : null)
                .iban(account.getIban())
                .number(account.getNumber())
                .correspondentBankAccount(account.getCorrespondentAccount() != null
                        ? mapToInternationalCorrespondentBankAccount(account.getCorrespondentAccount())
                        : null);
    }

    public static RefundSearchResult.StatusEnum mapToRefundStatus(InvoicePaymentRefundStatus status) {
        if (status.isSetPending()) {
            return RefundSearchResult.StatusEnum.PENDING;
        }

        if (status.isSetFailed()) {
            return RefundSearchResult.StatusEnum.FAILED;
        }

        if (status.isSetSucceeded()) {
            return RefundSearchResult.StatusEnum.SUCCEEDED;
        }

        throw new IllegalArgumentException("");
    }

    public static Payer getPayer(StatPayment payment) {
        var statPayer = payment.getPayer();
        Payer payer = new Payer();

        if (statPayer.isSetCustomer()) {
            return payer.payerType(Payer.PayerTypeEnum.CUSTOMERPAYER);
        }

        if (statPayer.isSetPaymentResource()) {
            return payer.payerType(Payer.PayerTypeEnum.PAYMENTRESOURCEPAYER);
        }

        if (statPayer.isSetRecurrent()) {
            return payer.payerType(Payer.PayerTypeEnum.RECURRENTPAYER);
        }

        return null;
    }

    public static PaymentSearchResult.StatusEnum mapToPaymentStatus(InvoicePaymentStatus status) {
        if (status.isSetCancelled()) {
            return PaymentSearchResult.StatusEnum.CANCELLED;
        }

        if (status.isSetCaptured()) {
            return PaymentSearchResult.StatusEnum.CAPTURED;
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            return PaymentSearchResult.StatusEnum.PROCESSED;
        }

        if (status.isSetPending()) {
            return PaymentSearchResult.StatusEnum.PENDING;
        }

        if (status.isSetProcessed()) {
            return PaymentSearchResult.StatusEnum.PROCESSED;
        }

        if (status.isSetRefunded()) {
            return PaymentSearchResult.StatusEnum.REFUNDED;
        }

        throw new IllegalArgumentException("");

    }

    public static String getAt(InvoicePaymentStatus status) {
        if (status.isSetCancelled()) {
            return status.getCancelled().getAt();
        }

        if (status.isSetCaptured()) {
            return status.getCaptured().getAt();
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            return status.getFailed().getAt();
        }

        if (status.isSetProcessed()) {
            return status.getProcessed().getAt();
        }

        if (status.isSetRefunded()) {
            return status.getRefunded().getAt();
        }

        return null;

    }
}
