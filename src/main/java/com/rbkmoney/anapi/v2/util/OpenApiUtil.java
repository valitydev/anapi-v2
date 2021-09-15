package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.damsel.domain.CountryCode;
import com.rbkmoney.damsel.domain.InvoicePaymentChargebackCategory;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoicePaymentRefundStatus;
import com.rbkmoney.magista.InvoiceStatus;
import com.rbkmoney.magista.PayoutStatus;
import com.rbkmoney.magista.StatPayment;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class OpenApiUtil {

    public static ChargebackCategory mapToChargebackCategory(InvoicePaymentChargebackCategory chargebackCategory) {
        if (chargebackCategory.isSetAuthorisation()) {
            //TODO: Is it a typo? Could be fixed? (authorization)
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

        throw new IllegalArgumentException(
                String.format("Chargeback category %s cannot be processed", chargebackCategory));
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

        throw new IllegalArgumentException(
                String.format("Invoice status %s cannot be processed", status));
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

        throw new IllegalArgumentException(
                String.format("Payout status %s cannot be processed", status));
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
                            .countryCode(getCountryCode(account.getBank().getCountry()))
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

        throw new IllegalArgumentException(
                String.format("PayoutToolInfo %s cannot be processed", payoutToolInfo));

    }

    private static String getCountryCode(@Nullable CountryCode countryCode) {
        if (countryCode == null) {
            return null;
        }

        return countryCode.name();
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

        throw new IllegalArgumentException(
                String.format("Refund status %s cannot be processed", status));
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

        throw new IllegalArgumentException(
                String.format("Payer %s cannot be processed", statPayer));
    }

    public static void fillPaymentStatusInfo(StatPayment payment, PaymentSearchResult result) {
        var status = payment.getStatus();
        if (status.isSetCancelled()) {
            OffsetDateTime createdAt = status.getCancelled().getAt() != null
                    ? TypeUtil.stringToInstant(status.getCancelled().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.CANCELLED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetCaptured()) {
            OffsetDateTime createdAt = status.getCaptured().getAt() != null
                    ? TypeUtil.stringToInstant(status.getCaptured().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.CAPTURED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            OffsetDateTime createdAt = status.getFailed().getAt() != null
                    ? TypeUtil.stringToInstant(status.getFailed().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.FAILED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetPending()) {
            result.status(PaymentSearchResult.StatusEnum.PENDING);
            return;
        }

        if (status.isSetProcessed()) {
            OffsetDateTime createdAt = status.getProcessed().getAt() != null
                    ? TypeUtil.stringToInstant(status.getProcessed().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.PROCESSED)
                    .createdAt(createdAt);
            return;
        }

        if (status.isSetRefunded()) {
            OffsetDateTime createdAt = status.getRefunded().getAt() != null
                    ? TypeUtil.stringToInstant(status.getRefunded().getAt()).atOffset(ZoneOffset.UTC)
                    : null;
            result.status(PaymentSearchResult.StatusEnum.REFUNDED)
                    .createdAt(createdAt);
            return;
        }

        throw new IllegalArgumentException(
                String.format("Payment status %s cannot be processed", payment.getStatus()));
    }
}
