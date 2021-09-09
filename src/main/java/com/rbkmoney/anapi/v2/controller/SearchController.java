package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.service.SearchService;
import com.rbkmoney.anapi.v2.util.DamselUtil;
import com.rbkmoney.damsel.domain.LegacyBankCardPaymentSystem;
import com.rbkmoney.damsel.domain.LegacyBankCardTokenProvider;
import com.rbkmoney.damsel.domain.LegacyTerminalPaymentProvider;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.api.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rbkmoney.anapi.v2.util.CommonUtil.merge;
import static com.rbkmoney.anapi.v2.util.DamselUtil.*;

@Controller
@RequiredArgsConstructor
public class SearchController implements PaymentsApi, ChargebacksApi, InvoicesApi, PayoutsApi, RefundsApi {

    private final SearchService searchService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return PaymentsApi.super.getRequest();
    }

    @Override
    public ResponseEntity<InlineResponse20010> searchPayments(String xRequestID,
                                                              @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                              @NotNull @Valid OffsetDateTime fromTime,
                                                              @NotNull @Valid OffsetDateTime toTime,
                                                              @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                              String xRequestDeadline,
                                                              @Size(min = 1, max = 40) @Valid String shopID,
                                                              @Valid List<String> shopIDs,
                                                              @Valid String paymentInstitutionRealm,
                                                              @Valid List<String> invoiceIDs,
                                                              @Valid String paymentStatus, @Valid String paymentFlow,
                                                              @Valid String paymentMethod,
                                                              @Valid String paymentTerminalProvider,
                                                              @Size(min = 1, max = 40) @Valid String invoiceID,
                                                              @Size(min = 1, max = 40) @Valid String paymentID,
                                                              @Size(min = 1, max = 40) @Valid String externalID,
                                                              @Size(max = 100) @Email @Valid String payerEmail,
                                                              @Size(max = 45) @Valid String payerIP,
                                                              @Size(max = 1000) @Valid String payerFingerprint,
                                                              @Size(min = 1, max = 40) @Valid String customerID,
                                                              @Pattern(regexp = "^\\d{6}$") @Valid String first6,
                                                              @Pattern(regexp = "^\\d{4}$") @Valid String last4,
                                                              @Pattern(regexp = "^[a-zA-Z0-9]{12}$") @Valid String rrn,
                                                              @Size(min = 1, max = 40) @Valid String approvalCode,
                                                              @Valid BankCardTokenProvider bankCardTokenProvider,
                                                              @Valid BankCardPaymentSystem bankCardPaymentSystem,
                                                              @Min(1L) @Valid Long paymentAmountFrom,
                                                              @Min(1L) @Valid Long paymentAmountTo,
                                                              @Valid List<String> excludedShops,
                                                              @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xRequestID, xRequestDeadline
        PaymentSearchQuery query = new PaymentSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs), continuationToken))
                .setPaymentParams(
                        new PaymentParams()
                                .setPaymentTool(mapToPaymentTool(paymentMethod))
                                .setPaymentFlow(mapToInvoicePaymentFlow(paymentFlow))
                                .setPaymentTerminalProvider(
                                        LegacyTerminalPaymentProvider.valueOf(paymentTerminalProvider))
                                .setPaymentTokenProvider(
                                        LegacyBankCardTokenProvider.valueOf(bankCardTokenProvider.getValue()))
                                .setPaymentAmountFrom(paymentAmountFrom)
                                .setPaymentAmountTo(paymentAmountTo)
                                .setPaymentEmail(payerEmail)
                                .setPaymentApprovalCode(approvalCode)
                                .setPaymentCustomerId(customerID)
                                .setPaymentFingerprint(payerFingerprint)
                                .setPaymentFirst6(first6)
                                .setPaymentLast4(last4)
                                .setPaymentId(paymentID)
                                .setPaymentIp(payerIP)
                                .setPaymentRrn(rrn)
                                .setPaymentStatus(getStatus(paymentStatus))
                                .setPaymentSystem(LegacyBankCardPaymentSystem.valueOf(bankCardPaymentSystem.getValue()))
                )
                .setExcludedShopIds(excludedShops)
                .setExternalId(externalID)
                .setInvoiceIds(merge(invoiceID, invoiceIDs));
        return ResponseEntity.ok(searchService.findPayments(query));
    }

    @GetMapping(
            value = "/chargebacks",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse2008> searchChargebacks(String xRequestID,
                                                                @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                                @NotNull @Valid OffsetDateTime fromTime,
                                                                @NotNull @Valid OffsetDateTime toTime,
                                                                @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                                String xRequestDeadline,
                                                                @Size(min = 1, max = 40) @Valid String shopID,
                                                                @Valid List<String> shopIDs,
                                                                @Valid String paymentInstitutionRealm,
                                                                @Min(0L) @Valid Integer offset,
                                                                @Size(min = 1, max = 40) @Valid String invoiceID,
                                                                @Size(min = 1, max = 40) @Valid String paymentID,
                                                                @Size(min = 1, max = 40) @Valid String chargebackID,
                                                                @Valid List<String> chargebackStatuses,
                                                                @Valid List<String> chargebackStages,
                                                                @Valid List<String> chargebackCategories,
                                                                @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xRequestID, xRequestDeadline, offset
        ChargebackSearchQuery query = new ChargebackSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs), continuationToken))
                .setInvoiceIds(List.of(invoiceID))
                .setPaymentId(paymentID)
                .setChargebackId(chargebackID)
                .setChargebackStatuses(chargebackStatuses.stream()
                        .map(DamselUtil::mapToDamselStatus)
                        .collect(Collectors.toList())
                )
                .setChargebackStages(chargebackStages.stream()
                        .map(DamselUtil::mapToDamselStage)
                        .collect(Collectors.toList())
                )
                .setChargebackCategories(chargebackCategories.stream()
                        .map(DamselUtil::mapToDamselCategory)
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(searchService.findChargebacks(query));
    }

    @GetMapping(
            value = "/invoices",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse2009> searchInvoices(String xRequestID,
                                                             @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                             @NotNull @Valid OffsetDateTime fromTime,
                                                             @NotNull @Valid OffsetDateTime toTime,
                                                             @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                             String xRequestDeadline,
                                                             @Size(min = 1, max = 40) @Valid String shopID,
                                                             @Valid List<String> shopIDs,
                                                             @Valid String paymentInstitutionRealm,
                                                             @Valid List<String> invoiceIDs,
                                                             @Valid String invoiceStatus,
                                                             @Size(min = 1, max = 40) @Valid String invoiceID,
                                                             @Size(min = 1, max = 40) @Valid String externalID,
                                                             @Min(1L) @Valid Long invoiceAmountFrom,
                                                             @Min(1L) @Valid Long invoiceAmountTo,
                                                             @Valid List<String> excludedShops,
                                                             @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xRequestID, xRequestDeadline, excludedShops
        InvoiceSearchQuery query = new InvoiceSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs), continuationToken))
                .setPaymentParams(
                        new PaymentParams()
                                .setPaymentAmountFrom(invoiceAmountFrom)
                                .setPaymentAmountTo(invoiceAmountTo)
                                .setPaymentStatus(getStatus(invoiceStatus))

                )
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID);
        return ResponseEntity.ok(searchService.findInvoices(query));
    }

    @GetMapping(
            value = "/payouts",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse20011> searchPayouts(String xRequestID,
                                                             @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                             @NotNull @Valid OffsetDateTime fromTime,
                                                             @NotNull @Valid OffsetDateTime toTime,
                                                             @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                             String xRequestDeadline,
                                                             @Size(min = 1, max = 40) @Valid String shopID,
                                                             @Valid List<String> shopIDs,
                                                             @Valid String paymentInstitutionRealm,
                                                             @Min(0L) @Valid Integer offset,
                                                             @Size(min = 1, max = 40) @Valid String payoutID,
                                                             @Valid String payoutToolType,
                                                             @Valid List<String> excludedShops,
                                                             @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xRequestID, xRequestDeadline, excludedShops,
        //offset + setStatuses
        PayoutSearchQuery query = new PayoutSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs), continuationToken))
                .setPayoutId(payoutID)
                .setPayoutType(mapToDamselPayoutToolInfo(payoutToolType));
        return ResponseEntity.ok(searchService.findPayouts(query));
    }

    @GetMapping(
            value = "/refunds",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse20012> searchRefunds(String xRequestID,
                                                             @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                             @NotNull @Valid OffsetDateTime fromTime,
                                                             @NotNull @Valid OffsetDateTime toTime,
                                                             @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                             String xRequestDeadline,
                                                             @Size(min = 1, max = 40) @Valid String shopID,
                                                             @Valid List<String> shopIDs,
                                                             @Valid String paymentInstitutionRealm,
                                                             @Min(0L) @Valid Integer offset,
                                                             @Valid List<String> invoiceIDs,
                                                             @Size(min = 1, max = 40) @Valid String invoiceID,
                                                             @Size(min = 1, max = 40) @Valid String paymentID,
                                                             @Size(min = 1, max = 40) @Valid String refundID,
                                                             @Size(min = 1, max = 40) @Valid String externalID,
                                                             @Valid String refundStatus,
                                                             @Valid List<String> excludedShops,
                                                             @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xRequestID, xRequestDeadline, excludedShops, offset
        RefundSearchQuery query = new RefundSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs), continuationToken))
                .setRefundStatus(getRefundStatus(refundStatus))
                .setInvoiceIds(merge(invoiceID, invoiceIDs))
                .setExternalId(externalID)
                .setPaymentId(paymentID)
                .setRefundId(refundID);
        return ResponseEntity.ok(searchService.findRefunds(query));
    }
}
