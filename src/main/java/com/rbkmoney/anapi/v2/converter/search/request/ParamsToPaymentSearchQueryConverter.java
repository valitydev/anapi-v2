package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.damsel.domain.LegacyBankCardPaymentSystem;
import com.rbkmoney.damsel.domain.LegacyBankCardTokenProvider;
import com.rbkmoney.damsel.domain.LegacyTerminalPaymentProvider;
import com.rbkmoney.magista.PaymentParams;
import com.rbkmoney.magista.PaymentSearchQuery;
import com.rbkmoney.openapi.anapi_v2.model.BankCardPaymentSystem;
import com.rbkmoney.openapi.anapi_v2.model.BankCardTokenProvider;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.CommonUtil.merge;
import static com.rbkmoney.anapi.v2.util.DamselUtil.*;

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
//TODO: clarify mapping for paymentInstitutionRealm
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
                .setPaymentStatus(paymentStatus != null ? getStatus(paymentStatus) : null)
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
}
