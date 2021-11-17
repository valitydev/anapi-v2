package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.*;
import com.rbkmoney.anapi.v2.util.MaskUtil;
import com.rbkmoney.damsel.domain.ClientInfo;
import com.rbkmoney.damsel.domain.ContactInfo;
import com.rbkmoney.damsel.domain.InvoicePaymentStatus;
import com.rbkmoney.damsel.domain.PaymentResourcePayer;
import com.rbkmoney.damsel.domain.RecurrentPayer;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.CustomerPayer;
import com.rbkmoney.magista.InvoicePaymentFlow;
import com.rbkmoney.magista.InvoicePaymentFlowInstant;
import com.rbkmoney.magista.Payer;
import com.rbkmoney.magista.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.rbkmoney.anapi.v2.model.PaymentSearchResult.StatusEnum.*;
import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.*;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomString;
import static org.junit.jupiter.api.Assertions.*;

class StatPaymentToPaymentSearchResultConverterTest {

    private static final StatPaymentToPaymentSearchResultConverter converter =
            new StatPaymentToPaymentSearchResultConverter();

    @Test
    void convert() {
        StatPaymentResponse magistaResponse = createSearchPaymentAllResponse();
        StatPayment magistaPayment = magistaResponse.getPayments().get(0);
        magistaPayment.setStatusChangedAt(TypeUtil.temporalToString(OffsetDateTime.now().toLocalDateTime()));
        magistaPayment.setAdditionalTransactionInfo(new AdditionalTransactionInfo()
                .setRrn(randomString(10))
                .setApprovalCode(randomString(10)));
        PaymentSearchResult result = converter.convert(magistaPayment);
        assertAll(
                () -> assertEquals(magistaPayment.getAmount(), result.getAmount()),
                () -> assertEquals(magistaPayment.getCreatedAt(), result.getCreatedAt().toString()),
                () -> assertEquals(magistaPayment.getCurrencySymbolicCode(), result.getCurrency()),
                () -> assertEquals(magistaPayment.getExternalId(), result.getExternalID()),
                () -> assertEquals(magistaPayment.getFee(), result.getFee()),
                () -> assertEquals(PaymentFlow.TypeEnum.PAYMENTFLOWINSTANT, result.getFlow().getType()),
                () -> assertEquals(magistaPayment.getLocationInfo().getCityGeoId(),
                        result.getGeoLocationInfo().getCityGeoID()),
                () -> assertEquals(magistaPayment.getLocationInfo().getCountryGeoId(),
                        result.getGeoLocationInfo().getCountryGeoID()),
                () -> assertEquals(magistaPayment.getStatusChangedAt(), result.getStatusChangedAt().toString()),
                () -> assertEquals(magistaPayment.getId(), result.getId()),
                () -> assertEquals(magistaPayment.getInvoiceId(), result.getInvoiceID()),
                () -> assertEquals(magistaPayment.isMakeRecurrent(), result.getMakeRecurrent()),
                () -> assertEquals(magistaPayment.getShopId(), result.getShopID()),
                () -> assertEquals(magistaPayment.getShortId(), result.getShortID()),
                () -> assertEquals(magistaPayment.getAdditionalTransactionInfo().getApprovalCode(),
                        result.getTransactionInfo().getApprovalCode()),
                () -> assertEquals(magistaPayment.getAdditionalTransactionInfo().getRrn(),
                        result.getTransactionInfo().getRrn())
        );
    }

    @Test
    void mapPayer() {
        Payer payer = new Payer();
        CustomerPayer customerPayer = new CustomerPayer();
        payer.setCustomer(customerPayer);
        PaymentTool tool = new PaymentTool();
        tool.setBankCard(new BankCard().setBin("1234")
                .setLastDigits("5678")
                .setBankName("Bank")
                .setToken("1111")
                .setPaymentSystemDeprecated(LegacyBankCardPaymentSystem.maestro)
                .setTokenProviderDeprecated(LegacyBankCardTokenProvider.applepay)
        );
        customerPayer.setPaymentTool(tool)
                .setCustomerId("1");
        var openapiCustomerPayer =
                (com.rbkmoney.anapi.v2.model.CustomerPayer) converter.mapPayer(payer);
        var paymentToolDetails = (PaymentToolDetailsBankCard) openapiCustomerPayer.getPaymentToolDetails();
        assertAll(
                () -> assertEquals("1", openapiCustomerPayer.getCustomerID()),
                () -> assertEquals("1111", openapiCustomerPayer.getPaymentToolToken()),
                () -> assertEquals("1234", paymentToolDetails.getBin()),
                () -> assertEquals("5678", paymentToolDetails.getLastDigits()),
                () -> assertEquals("maestro", paymentToolDetails.getPaymentSystem().getValue()),
                () -> assertEquals("applepay", paymentToolDetails.getTokenProvider().getValue())
        );


        payer = new Payer();
        var contactInfo = new ContactInfo()
                .setEmail("mail@mail.com")
                .setPhoneNumber("88005553535");
        PaymentResourcePayer resourcePayer = new PaymentResourcePayer();
        payer.setPaymentResource(resourcePayer);
        resourcePayer.setResource(new DisposablePaymentResource()
                        .setPaymentTool(tool)
                        .setPaymentSessionId("1111")
                        .setClientInfo(new ClientInfo()
                                .setFingerprint("print")
                                .setIpAddress("127.0.0.1")))
                .setContactInfo(contactInfo);
        var paymentResourcePayer =
                (com.rbkmoney.anapi.v2.model.PaymentResourcePayer) converter.mapPayer(payer);
        var resourcePayerPaymentToolDetails = (PaymentToolDetailsBankCard) paymentResourcePayer.getPaymentToolDetails();
        assertAll(
                () -> assertEquals("1111", paymentResourcePayer.getPaymentToolToken()),
                () -> assertEquals("1111", paymentResourcePayer.getPaymentSession()),
                () -> assertEquals("print", paymentResourcePayer.getClientInfo().getFingerprint()),
                () -> assertEquals("127.0.0.1", paymentResourcePayer.getClientInfo().getIp()),
                () -> assertEquals("mail@mail.com", paymentResourcePayer.getContactInfo().getEmail()),
                () -> assertEquals("88005553535", paymentResourcePayer.getContactInfo().getPhoneNumber()),
                () -> assertEquals("1234", resourcePayerPaymentToolDetails.getBin()),
                () -> assertEquals("5678", resourcePayerPaymentToolDetails.getLastDigits()),
                () -> assertEquals("maestro", resourcePayerPaymentToolDetails.getPaymentSystem().getValue()),
                () -> assertEquals("applepay", resourcePayerPaymentToolDetails.getTokenProvider().getValue())
        );

        payer = new Payer();
        RecurrentPayer recurrentPayer = new RecurrentPayer();
        payer.setRecurrent(recurrentPayer);
        recurrentPayer.setPaymentTool(tool)
                .setContactInfo(contactInfo)
                .setRecurrentParent(new RecurrentParentPayment()
                        .setPaymentId("123")
                        .setInvoiceId("456"));
        var openapiRecurrentPayer =
                (com.rbkmoney.anapi.v2.model.RecurrentPayer) converter.mapPayer(payer);
        var recurrentPayerPaymentToolDetails =
                (PaymentToolDetailsBankCard) paymentResourcePayer.getPaymentToolDetails();
        assertAll(
                () -> assertEquals("1111", openapiRecurrentPayer.getPaymentToolToken()),
                () -> assertEquals("123", openapiRecurrentPayer.getRecurrentParentPayment().getPaymentID()),
                () -> assertEquals("456", openapiRecurrentPayer.getRecurrentParentPayment().getInvoiceID()),
                () -> assertEquals("mail@mail.com", openapiRecurrentPayer.getContactInfo().getEmail()),
                () -> assertEquals("88005553535", openapiRecurrentPayer.getContactInfo().getPhoneNumber()),
                () -> assertEquals("1234", recurrentPayerPaymentToolDetails.getBin()),
                () -> assertEquals("5678", recurrentPayerPaymentToolDetails.getLastDigits()),
                () -> assertEquals("maestro", recurrentPayerPaymentToolDetails.getPaymentSystem().getValue()),
                () -> assertEquals("applepay", recurrentPayerPaymentToolDetails.getTokenProvider().getValue())
        );
    }

    @Test
    void mapStatus() {
        var status = InvoicePaymentStatus.pending(new InvoicePaymentPending());
        assertEquals(PENDING, converter.mapStatus(status));

        status = InvoicePaymentStatus.processed(new InvoicePaymentProcessed());
        assertEquals(PROCESSED, converter.mapStatus(status));

        status = InvoicePaymentStatus.captured(new InvoicePaymentCaptured());
        assertEquals(CAPTURED, converter.mapStatus(status));

        status = InvoicePaymentStatus.cancelled(new InvoicePaymentCancelled());
        assertEquals(CANCELLED, converter.mapStatus(status));

        status = InvoicePaymentStatus.refunded(new InvoicePaymentRefunded());
        assertEquals(REFUNDED, converter.mapStatus(status));

        status = InvoicePaymentStatus.failed(new InvoicePaymentFailed());
        assertEquals(FAILED, converter.mapStatus(status));

        status = InvoicePaymentStatus.charged_back(new InvoicePaymentChargedBack());
        assertEquals(CHARGEDBACK, converter.mapStatus(status));

        assertThrows(IllegalArgumentException.class, () -> converter.mapStatus(new InvoicePaymentStatus()));
    }

    @Test
    void mapFlow() {
        InvoicePaymentFlow flow = InvoicePaymentFlow.instant(new InvoicePaymentFlowInstant());
        PaymentFlow instantFlow = converter.mapFlow(flow);
        assertEquals(PaymentFlow.TypeEnum.PAYMENTFLOWINSTANT, instantFlow.getType());

        InvoicePaymentFlow magistaFlow = createInvoicePaymentFlowHold();
        var holdFlow = (PaymentFlowHold) converter.mapFlow(magistaFlow);
        assertAll(
                () -> assertEquals(PaymentFlow.TypeEnum.PAYMENTFLOWHOLD, holdFlow.getType()),
                () -> assertEquals(magistaFlow.getHold().getHeldUntil(), holdFlow.getHeldUntil().toString()),
                () -> assertEquals(magistaFlow.getHold().getOnHoldExpiration().name(),
                        holdFlow.getOnHoldExpiration().getValue())
        );
    }

    @Test
    void getPaymentToolToken() {
        assertAll(
                () -> assertEquals("1111",
                        converter.getPaymentToolToken(PaymentTool.bank_card(new BankCard().setToken("1111")))),
                () -> assertEquals("1111",
                        converter.getPaymentToolToken(
                                PaymentTool.digital_wallet(new DigitalWallet().setToken("1111")))),
                () -> assertNull(converter.getPaymentToolToken(PaymentTool.payment_terminal(new PaymentTerminal()))),
                () -> assertNull(converter.getPaymentToolToken(PaymentTool.crypto_currency(new CryptoCurrencyRef()))),
                () -> assertNull(converter.getPaymentToolToken(
                        PaymentTool.crypto_currency_deprecated(LegacyCryptoCurrency.bitcoin)))
        );
    }

    @Test
    void mapPaymentToolDetails() {
        var tool = createBankCardPaymentTool();
        var expectedCardDetails = tool.getBankCard();
        var actualCardDetails = (PaymentToolDetailsBankCard) converter.mapPaymentToolDetails(tool);
        assertAll(
                () -> assertEquals(expectedCardDetails.getLastDigits(), actualCardDetails.getLastDigits()),
                () -> assertEquals(expectedCardDetails.getBin(), actualCardDetails.getBin()),
                () -> assertEquals(expectedCardDetails.getTokenProviderDeprecated().name(),
                        actualCardDetails.getTokenProvider().getValue()),
                () -> assertEquals(expectedCardDetails.getPaymentSystemDeprecated().name(),
                        actualCardDetails.getPaymentSystem().getValue()),
                () -> assertTrue(actualCardDetails.getCardNumberMask().startsWith(expectedCardDetails.getBin())),
                () -> assertTrue(actualCardDetails.getCardNumberMask().endsWith(expectedCardDetails.getLastDigits()))
        );

        tool = createPaymentTerminalPaymentTool();
        var expectedTerminalDetails = tool.getPaymentTerminal();
        var actualTerminalDetails = (PaymentToolDetailsPaymentTerminal) converter.mapPaymentToolDetails(tool);
        assertEquals(expectedTerminalDetails.getTerminalTypeDeprecated().name(),
                actualTerminalDetails.getProvider().getValue());

        tool = createMobileCommercePaymentTool();
        var expectedMobileDetails = tool.getMobileCommerce();
        var actualMobileDetails = (PaymentToolDetailsMobileCommerce) converter.mapPaymentToolDetails(tool);
        assertEquals(MaskUtil.constructPhoneNumber(expectedMobileDetails.getPhone()),
                actualMobileDetails.getPhoneNumber());

        tool = createLegacyCryptoCurrencyPaymentTool();
        var expectedLegacyCryptoDetails = tool.getCryptoCurrencyDeprecated();
        var actualLegacyCryptoDetails = (PaymentToolDetailsCryptoWallet) converter.mapPaymentToolDetails(tool);
        assertEquals(expectedLegacyCryptoDetails.name(),
                actualLegacyCryptoDetails.getCryptoCurrency().getValue());

        assertThrows(IllegalArgumentException.class,
                () -> converter.mapPaymentToolDetails(createCryptoCurrencyPaymentTool()));


    }
}