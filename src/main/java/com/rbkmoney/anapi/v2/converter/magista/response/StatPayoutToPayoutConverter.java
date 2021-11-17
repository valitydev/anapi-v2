package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.*;
import com.rbkmoney.damsel.domain.CountryCode;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.PayoutStatus;
import com.rbkmoney.magista.StatPayout;
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
                .payoutToolDetails(mapPayoutToolDetails(payout.getPayoutToolInfo()))
                .shopID(payout.getShopId())
                .status(mapStatus(payout.getStatus()))
                .cancellationDetails(
                        payout.getStatus().isSetCancelled()
                                ? payout.getStatus().getCancelled().getDetails()
                                : null);
    }

    protected String mapStatus(PayoutStatus status) {
        try {
            var field = PayoutStatus._Fields.findByName(status.getSetField().getFieldName());
            return switch (field) {
                case UNPAID -> "Unpaid";
                case PAID -> "Paid";
                case CANCELLED -> "Cancelled";
                case CONFIRMED -> "Confirmed";
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Payout status %s cannot be processed", status));
        }
    }

    protected PayoutToolDetails mapPayoutToolDetails(PayoutToolInfo payoutToolInfo) {
        try {
            var field = PayoutToolInfo._Fields.findByName(payoutToolInfo.getSetField().getFieldName());
            switch (field) {
                case RUSSIAN_BANK_ACCOUNT -> {
                    var account = payoutToolInfo.getRussianBankAccount();
                    return new PayoutToolDetailsBankAccount()
                            .account(account.getAccount())
                            .bankBik(account.getBankBik())
                            .bankName(account.getBankName())
                            .bankPostAccount(account.getBankPostAccount())
                            .detailsType("PayoutToolDetailsBankAccount");
                }
                case INTERNATIONAL_BANK_ACCOUNT -> {
                    var account = payoutToolInfo.getInternationalBankAccount();
                    return new PayoutToolDetailsInternationalBankAccount()
                            .iban(account.getIban())
                            .number(account.getNumber())
                            .bankDetails(account.isSetBank()
                                    ? new InternationalBankDetails()
                                    .name(account.getBank().getName())
                                    .bic(account.getBank().getBic())
                                    .countryCode(mapCountryCode(account.getBank().getCountry()))
                                    .address(account.getBank().getAddress())
                                    .abartn(account.getBank().getAbaRtn())
                                    : null)
                            .correspondentBankAccount(
                                    mapInternationalCorrespondentBankAccount(account.getCorrespondentAccount()))
                            .detailsType("PayoutToolDetailsInternationalBankAccount");
                }
                case WALLET_INFO -> {
                    return new PayoutToolDetailsWalletInfo()
                            .walletID(payoutToolInfo.getWalletInfo().getWalletId())
                            .detailsType("PayoutToolDetailsWalletInfo");
                }
                case PAYMENT_INSTITUTION_ACCOUNT -> {
                    return new PayoutToolDetailsPaymentInstitutionAccount()
                            .detailsType("PayoutToolDetailsPaymentInstitutionAccount");
                }
                default -> throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("PayoutToolInfo %s cannot be processed", payoutToolInfo));
        }

    }

    protected String mapCountryCode(@Nullable CountryCode countryCode) {
        if (countryCode == null) {
            return null;
        }
        return countryCode.name();
    }

    protected InternationalCorrespondentBankAccount mapInternationalCorrespondentBankAccount(
            com.rbkmoney.damsel.domain.InternationalBankAccount account) {
        if (account == null) {
            return null;
        }
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
                .correspondentBankAccount(account.isSetCorrespondentAccount()
                        ? mapInternationalCorrespondentBankAccount(account.getCorrespondentAccount())
                        : null);
    }
}
