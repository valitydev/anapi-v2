package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.damsel.domain.CountryCode;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.PayoutStatus;
import com.rbkmoney.magista.StatPayout;
import com.rbkmoney.openapi.anapi_v2.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.time.ZoneOffset;

@Component
public class StatPayoutToPayoutConverter {

    public Payout convert(StatPayout payout) {
        return new Payout()
                .amount(payout.getAmount())
                .createdAt(TypeUtil.stringToInstant(payout.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(payout.getCurrencySymbolicCode())
                .fee(payout.getFee())
                .id(payout.getId())
                .payoutToolDetails(mapToPayoutToolDetails(payout.getPayoutToolInfo()))
                .shopID(payout.getShopId())
                .status(mapToPayoutStatus(payout.getStatus()))
                .cancellationDetails(
                        payout.getStatus().isSetCancelled()
                                ? payout.getStatus().getCancelled().getDetails()
                                : null);
    }

    private String mapToPayoutStatus(PayoutStatus status) {
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

    private PayoutToolDetails mapToPayoutToolDetails(PayoutToolInfo payoutToolInfo) {
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

    private String getCountryCode(@Nullable CountryCode countryCode) {
        if (countryCode == null) {
            return null;
        }
        return countryCode.name();
    }

    private InternationalCorrespondentBankAccount mapToInternationalCorrespondentBankAccount(
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
}
