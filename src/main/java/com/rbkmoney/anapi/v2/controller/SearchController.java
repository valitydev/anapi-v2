package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.service.SearchService;
import com.rbkmoney.damsel.domain.BankCard;
import com.rbkmoney.damsel.domain.InvoicePaymentCancelled;
import com.rbkmoney.damsel.domain.InvoicePaymentCaptured;
import com.rbkmoney.damsel.domain.InvoicePaymentFailed;
import com.rbkmoney.damsel.domain.InvoicePaymentFlow;
import com.rbkmoney.damsel.domain.InvoicePaymentFlowHold;
import com.rbkmoney.damsel.domain.InvoicePaymentFlowInstant;
import com.rbkmoney.damsel.domain.InvoicePaymentPending;
import com.rbkmoney.damsel.domain.InvoicePaymentProcessed;
import com.rbkmoney.damsel.domain.InvoicePaymentRefunded;
import com.rbkmoney.damsel.domain.PaymentTerminal;
import com.rbkmoney.damsel.domain.PaymentTool;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.api.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        //TODO: clearify mapping for paymentInstitutionRealm, xRequestID, xRequestDeadline
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

    private PaymentTool mapToPaymentTool(String paymentMethod) {
        var paymentTool = new PaymentTool();
        switch (paymentMethod) {
            case "bankCard" -> paymentTool.setBankCard(new BankCard());
            case "paymentTerminal" -> paymentTool.setPaymentTerminal(new PaymentTerminal());
            default -> throw new IllegalArgumentException("");
        }

        return paymentTool;
    }

    private List<String> merge(@Nullable String id, @Nullable List<String> ids) {
        if (id != null) {
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(id);
        }
        return ids;
    }

    private InvoicePaymentFlow mapToInvoicePaymentFlow(String paymentFlow) {
        var invoicePaymentFlow = new InvoicePaymentFlow();
        switch (paymentFlow) {
            case "instant" -> invoicePaymentFlow.setInstant(new InvoicePaymentFlowInstant());
            case "hold" -> invoicePaymentFlow.setHold(new InvoicePaymentFlowHold());
            default -> throw new IllegalArgumentException("");
        }
        return invoicePaymentFlow;
    }

    private CommonSearchQueryParams fillCommonParams(OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit,
                                                     String partyId, List<String> shopIDs, String continuationToken) {
        return new CommonSearchQueryParams()
                .setContinuationToken(continuationToken)
                .setFromTime(TypeUtil.temporalToString(fromTime.toLocalDateTime()))
                .setToTime(TypeUtil.temporalToString(toTime.toLocalDateTime()))
                .setLimit(limit)
                .setPartyId(partyId)
                .setShopIds(shopIDs);
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
        ChargebackSearchQuery query = new ChargebackSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setPaymentId(paymentID)
                .setChargebackId(chargebackID)
                .setChargebackCategories(chargebackCategories.stream()
                        .map(this::mapToDamselCategory)
                        .collect(Collectors.toList()))
                .setChargebackStatuses(chargebackStatuses.stream()
                        .map(this::mapToDamselStatus)
                        .collect(Collectors.toList())
                )
                .setChargebackStages(chargebackStages.stream()
                        .map(this::mapToDamselStage)
                        .collect(Collectors.toList())
                );
        return ResponseEntity.ok(searchService.findChargebacks(query));
    }

    private InvoicePaymentChargebackStage mapToDamselStage(String stage) {
        var damselStage = new InvoicePaymentChargebackStage();
        switch (stage) {
            case "chargeback" -> damselStage.setChargeback(new InvoicePaymentChargebackStageChargeback());
            case "pre_arbitration" -> damselStage.setPreArbitration(new InvoicePaymentChargebackStagePreArbitration());
            case "arbitration" -> damselStage.setArbitration(new InvoicePaymentChargebackStageArbitration());
            default -> throw new IllegalArgumentException("");
        }

        return damselStage;
    }

    private InvoicePaymentChargebackStatus mapToDamselStatus(String status) {
        var damselStatus = new InvoicePaymentChargebackStatus();
        switch (status) {
            case "pending" -> damselStatus.setPending(new InvoicePaymentChargebackPending());
            case "accepted" -> damselStatus.setAccepted(new InvoicePaymentChargebackAccepted());
            case "rejected" -> damselStatus.setRejected(new InvoicePaymentChargebackRejected());
            case "cancelled" -> damselStatus.setCancelled(new InvoicePaymentChargebackCancelled());
            default -> throw new IllegalArgumentException("");
        }

        return damselStatus;
    }

    private InvoicePaymentChargebackCategory mapToDamselCategory(String category) {
        var damselCategory = new InvoicePaymentChargebackCategory();
        switch (category) {
            case "fraud" -> damselCategory.setFraud(new InvoicePaymentChargebackCategoryFraud());
            case "dispute" -> damselCategory.setDispute(new InvoicePaymentChargebackCategoryDispute());
            case "authorisation" -> damselCategory
                    .setAuthorisation(new InvoicePaymentChargebackCategoryAuthorisation());
            case "processing_error" -> damselCategory
                    .setProcessingError(new InvoicePaymentChargebackCategoryProcessingError());
            default -> throw new IllegalArgumentException("");
        }

        return damselCategory;
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
        InvoiceSearchQuery query = new InvoiceSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setPaymentParams(
                        new PaymentParams()
                                .setPaymentAmountFrom(invoiceAmountFrom)
                                .setPaymentAmountTo(invoiceAmountTo)
                                .setPaymentId(invoiceID)
                                .setPaymentStatus(getStatus(invoiceStatus))

                )
                .setInvoiceIds(invoiceIDs)
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
