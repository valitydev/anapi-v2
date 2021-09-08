package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.service.SearchService;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.magista.PaymentParams;
import com.rbkmoney.magista.PaymentSearchQuery;
import com.rbkmoney.openapi.anapi_v2.api.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SearchController implements PaymentsApi, ChargebacksApi, InvoicesApi, PayoutsApi, RefundsApi {

    private final SearchService searchService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return PaymentsApi.super.getRequest();
    }

    @GetMapping(
            value = "/payments",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse20010> searchPayments(String xRequestID,
                                                              String partyID,
                                                              OffsetDateTime fromTime,
                                                              OffsetDateTime toTime,
                                                              Integer limit,
                                                              String xRequestDeadline,
                                                              String shopID,
                                                              List<String> shopIDs,
                                                              String paymentInstitutionRealm,
                                                              List<String> invoiceIDs,
                                                              String paymentStatus,
                                                              String paymentFlow,
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
        PaymentSearchQuery query = new PaymentSearchQuery()
                .setCommonSearchQueryParams(
                        new CommonSearchQueryParams()
                                .setContinuationToken(continuationToken)
                                .setFromTime(TypeUtil.temporalToString(fromTime.toLocalDateTime()))
                                .setToTime(TypeUtil.temporalToString(toTime.toLocalDateTime()))
                                .setLimit(limit)
                                .setPartyId(partyID)
                                .setShopIds(shopIDs)
                )
                .setPaymentParams(
                        new PaymentParams()
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
                                .setPaymentSystem(LegacyBankCardPaymentSystem.valueOf(bankCardPaymentSystem.getValue())
                                )
                )
                .setExcludedShopIds(excludedShops)
                .setExternalId(externalID)
                .setInvoiceIds(invoiceIDs);
        return ResponseEntity.ok(searchService.search(query));
    }

    private com.rbkmoney.damsel.domain.InvoicePaymentStatus getStatus(String paymentStatus) {
        var status = Enum.valueOf(PaymentStatus.StatusEnum.class, paymentStatus);
        var invoicePaymentStatus = new com.rbkmoney.damsel.domain.InvoicePaymentStatus();
        switch (status) {
            case PENDING -> invoicePaymentStatus.setPending(new InvoicePaymentPending());
            case PROCESSED -> invoicePaymentStatus.setProcessed(new InvoicePaymentProcessed());
            case CAPTURED -> invoicePaymentStatus.setCaptured(new InvoicePaymentCaptured());
            case CANCELLED -> invoicePaymentStatus.setCancelled(new InvoicePaymentCancelled());
            case REFUNDED -> invoicePaymentStatus.setRefunded(new InvoicePaymentRefunded());
            case FAILED -> invoicePaymentStatus.setFailed(new InvoicePaymentFailed());
        }
        return invoicePaymentStatus;
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
        return ChargebacksApi.super
                .searchChargebacks(xRequestID, partyID, fromTime, toTime, limit, xRequestDeadline, shopID, shopIDs,
                        paymentInstitutionRealm, offset, invoiceID, paymentID, chargebackID, chargebackStatuses,
                        chargebackStages, chargebackCategories, continuationToken);
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
        return InvoicesApi.super
                .searchInvoices(xRequestID, partyID, fromTime, toTime, limit, xRequestDeadline, shopID, shopIDs,
                        paymentInstitutionRealm, invoiceIDs, invoiceStatus, invoiceID, externalID, invoiceAmountFrom,
                        invoiceAmountTo, excludedShops, continuationToken);
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
        return PayoutsApi.super
                .searchPayouts(xRequestID, partyID, fromTime, toTime, limit, xRequestDeadline, shopID, shopIDs,
                        paymentInstitutionRealm, offset, payoutID, payoutToolType, excludedShops, continuationToken);
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
        return RefundsApi.super
                .searchRefunds(xRequestID, partyID, fromTime, toTime, limit, xRequestDeadline, shopID, shopIDs,
                        paymentInstitutionRealm, offset, invoiceIDs, invoiceID, paymentID, refundID, externalID,
                        refundStatus, excludedShops, continuationToken);
    }
}
