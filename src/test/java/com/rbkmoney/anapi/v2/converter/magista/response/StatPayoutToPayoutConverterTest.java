package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.*;
import com.rbkmoney.damsel.domain.InternationalBankAccount;
import com.rbkmoney.damsel.domain.InternationalBankDetails;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.*;
import org.junit.jupiter.api.Test;

import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createSearchPayoutAllResponse;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomString;
import static org.junit.jupiter.api.Assertions.*;

class StatPayoutToPayoutConverterTest {

    private static final StatPayoutToPayoutConverter converter = new StatPayoutToPayoutConverter();

    @Test
    void convert() {
        StatPayoutResponse magistaResponse = createSearchPayoutAllResponse();
        StatPayout magistaPayout = magistaResponse.getPayouts().get(0);
        Payout result = converter.convert(magistaPayout);
        assertAll(
                () -> assertEquals(magistaPayout.getAmount(), result.getAmount()),
                () -> assertEquals(magistaPayout.getCreatedAt(), result.getCreatedAt().toString()),
                () -> assertEquals(magistaPayout.getCurrencySymbolicCode(), result.getCurrency()),
                () -> assertEquals(magistaPayout.getFee(), result.getFee()),
                () -> assertEquals(magistaPayout.getId(), result.getId()),
                () -> assertEquals(magistaPayout.getShopId(), result.getShopID())
        );

    }

    @Test
    void mapPayoutStatus() {
        assertAll(
                () -> assertEquals("Cancelled", converter.mapStatus(PayoutStatus.cancelled(new PayoutCancelled()))),
                () -> assertEquals("Paid", converter.mapStatus(PayoutStatus.paid(new PayoutPaid()))),
                () -> assertEquals("Confirmed", converter.mapStatus(PayoutStatus.confirmed(new PayoutConfirmed()))),
                () -> assertEquals("Unpaid", converter.mapStatus(PayoutStatus.unpaid(new PayoutUnpaid()))),
                () -> assertThrows(IllegalArgumentException.class, () -> converter.mapStatus(new PayoutStatus()))
        );
    }

    @Test
    void mapPayoutToolDetails() {
        //RussianBankAccount
        PayoutToolInfo toolInfo = new PayoutToolInfo();
        toolInfo.setRussianBankAccount(new RussianBankAccount()
                .setAccount(randomString(10))
                .setBankBik(randomString(10))
                .setBankName(randomString(10))
                .setBankPostAccount(randomString(10)));

        PayoutToolDetailsBankAccount actualRussianBankAccount =
                (PayoutToolDetailsBankAccount) converter.mapPayoutToolDetails(toolInfo);
        RussianBankAccount expectedRussianBankAccount = toolInfo.getRussianBankAccount();
        assertAll(
                () -> assertEquals(expectedRussianBankAccount.getAccount(), actualRussianBankAccount.getAccount()),
                () -> assertEquals(expectedRussianBankAccount.getBankBik(), actualRussianBankAccount.getBankBik()),
                () -> assertEquals(expectedRussianBankAccount.getBankName(), actualRussianBankAccount.getBankName()),
                () -> assertEquals(expectedRussianBankAccount.getBankPostAccount(),
                        actualRussianBankAccount.getBankPostAccount())
        );

        //WalletInfo
        toolInfo = new PayoutToolInfo();
        toolInfo.setWalletInfo(new WalletInfo()
                .setWalletId(randomString(10)));

        PayoutToolDetailsWalletInfo walletActual =
                (PayoutToolDetailsWalletInfo) converter.mapPayoutToolDetails(toolInfo);
        WalletInfo walletExpected = toolInfo.getWalletInfo();
        assertEquals(walletExpected.getWalletId(), walletActual.getWalletID());

        //PaymentInstitutionAccount
        toolInfo = new PayoutToolInfo();
        toolInfo.setPaymentInstitutionAccount(new PaymentInstitutionAccount());

        PayoutToolDetailsPaymentInstitutionAccount actualPaymentInstitutionAccount =
                (PayoutToolDetailsPaymentInstitutionAccount) converter.mapPayoutToolDetails(toolInfo);
        PaymentInstitutionAccount expectedPaymentInstitutionAccount = toolInfo.getPaymentInstitutionAccount();
        assertNotNull(expectedPaymentInstitutionAccount);

        //InternationalBankAccount
        toolInfo = new PayoutToolInfo();
        toolInfo.setInternationalBankAccount(new InternationalBankAccount()
                .setAccountHolder(randomString(10))
                .setIban(randomString(10))
                .setNumber(randomString(10))
                .setBank(new InternationalBankDetails()
                        .setName(randomString(10))
                        .setAbaRtn(randomString(10))
                        .setAddress(randomString(10))
                        .setBic(randomString(10))
                        .setCountry(CountryCode.ABW)));

        PayoutToolDetailsInternationalBankAccount actualInternationalBankAccount =
                (PayoutToolDetailsInternationalBankAccount) converter.mapPayoutToolDetails(toolInfo);
        InternationalBankAccount expectedInternationalBankAccount = toolInfo.getInternationalBankAccount();

        assertAll(
                () -> assertEquals(expectedInternationalBankAccount.getIban(),
                        actualInternationalBankAccount.getIban()),
                () -> assertEquals(expectedInternationalBankAccount.getNumber(),
                        actualInternationalBankAccount.getNumber()),
                () -> assertEquals(expectedInternationalBankAccount.getBank().getAbaRtn(),
                        actualInternationalBankAccount.getBankDetails().getAbartn()),
                () -> assertEquals(expectedInternationalBankAccount.getBank().getAddress(),
                        actualInternationalBankAccount.getBankDetails().getAddress()),
                () -> assertEquals(expectedInternationalBankAccount.getBank().getBic(),
                        actualInternationalBankAccount.getBankDetails().getBic()),
                () -> assertEquals(expectedInternationalBankAccount.getBank().getName(),
                        actualInternationalBankAccount.getBankDetails().getName()),
                () -> assertEquals(expectedInternationalBankAccount.getBank().getCountry().name(),
                        actualInternationalBankAccount.getBankDetails().getCountryCode()),
                //tested via mapInternationalCorrespondentBankAccount test
                () -> assertNull(actualInternationalBankAccount.getCorrespondentBankAccount())
        );

        //Some missing type
        assertThrows(IllegalArgumentException.class, () -> converter.mapPayoutToolDetails(new PayoutToolInfo()));

    }

    @Test
    void mapCountryCode() {
        CountryCode countryCode = CountryCode.ABH;
        assertEquals("ABH", converter.mapCountryCode(countryCode));
        assertNull(converter.mapCountryCode(null));
    }

    @Test
    void mapInternationalCorrespondentBankAccount() {
        InternationalBankAccount expected = new InternationalBankAccount()
                .setAccountHolder(randomString(10))
                .setIban(randomString(10))
                .setNumber(randomString(10))
                .setBank(new InternationalBankDetails()
                        .setName(randomString(10))
                        .setAbaRtn(randomString(10))
                        .setAddress(randomString(10))
                        .setBic(randomString(10))
                        .setCountry(CountryCode.ABW))
                .setCorrespondentAccount(new InternationalBankAccount()
                        .setAccountHolder(randomString(5))
                        .setIban(randomString(5))
                        .setNumber(randomString(5))
                        .setBank(new InternationalBankDetails()
                                .setName(randomString(5))
                                .setAbaRtn(randomString(5))
                                .setAddress(randomString(5))
                                .setBic(randomString(5))
                                .setCountry(CountryCode.RUS)));

        InternationalCorrespondentBankAccount actual = converter.mapInternationalCorrespondentBankAccount(expected);

        assertAll(
                () -> assertEquals(expected.getIban(),
                        actual.getIban()),
                () -> assertEquals(expected.getNumber(),
                        actual.getNumber()),
                () -> assertEquals(expected.getBank().getAbaRtn(),
                        actual.getBankDetails().getAbartn()),
                () -> assertEquals(expected.getBank().getAddress(),
                        actual.getBankDetails().getAddress()),
                () -> assertEquals(expected.getBank().getBic(),
                        actual.getBankDetails().getBic()),
                () -> assertEquals(expected.getBank().getName(),
                        actual.getBankDetails().getName()),
                () -> assertEquals(expected.getBank().getCountry().name(),
                        actual.getBankDetails().getCountryCode()),
                () -> assertEquals(expected.getCorrespondentAccount().getIban(),
                        actual.getCorrespondentBankAccount().getIban()),
                () -> assertEquals(expected.getCorrespondentAccount().getNumber(),
                        actual.getCorrespondentBankAccount().getNumber()),
                () -> assertEquals(expected.getCorrespondentAccount().getBank().getAbaRtn(),
                        actual.getCorrespondentBankAccount().getBankDetails().getAbartn()),
                () -> assertEquals(expected.getCorrespondentAccount().getBank().getAddress(),
                        actual.getCorrespondentBankAccount().getBankDetails().getAddress()),
                () -> assertEquals(expected.getCorrespondentAccount().getBank().getBic(),
                        actual.getCorrespondentBankAccount().getBankDetails().getBic()),
                () -> assertEquals(expected.getCorrespondentAccount().getBank().getName(),
                        actual.getCorrespondentBankAccount().getBankDetails().getName()),
                () -> assertEquals(expected.getCorrespondentAccount().getBank().getCountry().name(),
                        actual.getCorrespondentBankAccount().getBankDetails().getCountryCode()),
                () -> assertNull(actual.getCorrespondentBankAccount().getCorrespondentBankAccount())
        );
    }
}