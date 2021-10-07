package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.converter.search.request.*;
import com.rbkmoney.anapi.v2.security.AccessService;
import com.rbkmoney.anapi.v2.service.SearchService;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.api.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;
import static com.rbkmoney.anapi.v2.util.DeadlineUtil.checkDeadline;


@Slf4j
@PreAuthorize("hasAuthority('invoices:read')")
@Controller
@RequiredArgsConstructor
@SuppressWarnings("ParameterName")
public class SearchController
        implements PaymentsApi, ChargebacksApi, InvoicesApi, PayoutsApi, RefundsApi, InvoiceTemplatesApi {

    private final SearchService searchService;
    private final AccessService accessService;
    private final ParamsToPaymentSearchQueryConverter paymentSearchConverter;
    private final ParamsToChargebackSearchQueryConverter chargebackSearchConverter;
    private final ParamsToInvoiceSearchQueryConverter invoiceSearchConverter;
    private final ParamsToPayoutSearchQueryConverter payoutSearchConverter;
    private final ParamsToRefundSearchQueryConverter refundSearchConverter;
    private final ParamsToInvoiceTemplateSearchQueryConverter invoiceTempateSearchConverter;

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
                                                              @Valid String bankCardTokenProvider,
                                                              @Valid String bankCardPaymentSystem,
                                                              @Min(1L) @Valid Long paymentAmountFrom,
                                                              @Min(1L) @Valid Long paymentAmountTo,
                                                              @Valid List<String> excludedShops,
                                                              @Valid String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService
                .getAccessibleShops("searchPayments", partyID, merge(shopID, shopIDs), paymentInstitutionRealm);

        PaymentSearchQuery query = paymentSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                invoiceIDs,
                paymentStatus, paymentFlow,
                paymentMethod,
                paymentTerminalProvider,
                invoiceID,
                paymentID,
                externalID,
                payerEmail,
                payerIP,
                payerFingerprint,
                customerID,
                first6,
                last4,
                rrn,
                approvalCode,
                bankCardTokenProvider,
                bankCardPaymentSystem,
                paymentAmountFrom,
                paymentAmountTo,
                excludedShops,
                continuationToken);
        InlineResponse20010 response = searchService.findPayments(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

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
                                                                //Not used by magista
                                                                @Min(0L) @Valid @Deprecated Integer offset,
                                                                @Size(min = 1, max = 40) @Valid String invoiceID,
                                                                @Size(min = 1, max = 40) @Valid String paymentID,
                                                                @Size(min = 1, max = 40) @Valid String chargebackID,
                                                                @Valid List<String> chargebackStatuses,
                                                                @Valid List<String> chargebackStages,
                                                                @Valid List<String> chargebackCategories,
                                                                @Valid String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService
                .getAccessibleShops("searchChargebacks", partyID, merge(shopID, shopIDs), paymentInstitutionRealm);
        ChargebackSearchQuery query = chargebackSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                invoiceID,
                paymentID,
                chargebackID,
                chargebackStatuses,
                chargebackStages,
                chargebackCategories,
                continuationToken);
        InlineResponse2008 response = searchService
                .findChargebacks(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

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
                                                             //Not used by magista
                                                             @Valid @Deprecated List<String> excludedShops,
                                                             @Valid String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService
                .getAccessibleShops("searchInvoices", partyID, merge(shopID, shopIDs), paymentInstitutionRealm);
        InvoiceSearchQuery query = invoiceSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                invoiceIDs,
                invoiceStatus,
                invoiceID,
                externalID,
                invoiceAmountFrom,
                invoiceAmountTo,
                continuationToken);
        InlineResponse2009 response = searchService.findInvoices(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

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
                                                             //Not used by magista
                                                             @Min(0L) @Valid @Deprecated Integer offset,
                                                             @Size(min = 1, max = 40) @Valid String payoutID,
                                                             @Valid String payoutToolType,
                                                             //Not used by magista
                                                             @Valid @Deprecated List<String> excludedShops,
                                                             @Valid String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService
                .getAccessibleShops("searchPayouts", partyID, merge(shopID, shopIDs), paymentInstitutionRealm);
        PayoutSearchQuery query = payoutSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                payoutID,
                payoutToolType,
                continuationToken);
        InlineResponse20011 response = searchService.findPayouts(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

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
                                                             //Not used by magista
                                                             @Min(0L) @Valid @Deprecated Integer offset,
                                                             @Valid List<String> invoiceIDs,
                                                             @Size(min = 1, max = 40) @Valid String invoiceID,
                                                             @Size(min = 1, max = 40) @Valid String paymentID,
                                                             @Size(min = 1, max = 40) @Valid String refundID,
                                                             @Size(min = 1, max = 40) @Valid String externalID,
                                                             @Valid String refundStatus,
                                                             //Not used by magista
                                                             @Valid @Deprecated List<String> excludedShops,
                                                             @Valid String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService
                .getAccessibleShops("searchRefunds", partyID, merge(shopID, shopIDs), paymentInstitutionRealm);
        RefundSearchQuery query = refundSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                invoiceIDs,
                invoiceID,
                paymentID,
                refundID,
                externalID,
                refundStatus,
                continuationToken);
        InlineResponse20012 response = searchService.findRefunds(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20013> searchInvoiceTemplates(String xRequestID,
                                                                      @NotNull @Size(min = 1, max = 40) @Valid
                                                                              String partyID,
                                                                      @NotNull @Valid OffsetDateTime fromTime,
                                                                      @NotNull @Valid OffsetDateTime toTime,
                                                                      @NotNull @Min(1L) @Max(1000L) @Valid
                                                                              Integer limit,
                                                                      String xRequestDeadline,
                                                                      @Valid List<String> shopIDs,
                                                                      @Valid String paymentInstitutionRealm,
                                                                      @Valid String invoiceTemplateStatus,
                                                                      @Size(min = 1, max = 40) @Valid
                                                                              String invoiceTemplateID,
                                                                      @Valid String continuationToken,
                                                                      @Size(min = 1, max = 40) @Valid String name,
                                                                      @Size(min = 1, max = 40) @Valid String product,
                                                                      @Valid OffsetDateTime invoiceValidUntil) {
        log.info("-> Req: xRequestID={}", xRequestID);
        shopIDs = accessService
                .getAccessibleShops("searchInvoiceTemplates", partyID, shopIDs, paymentInstitutionRealm);
        checkDeadline(xRequestDeadline, xRequestID);
        InvoiceTemplateSearchQuery query = invoiceTempateSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                invoiceTemplateStatus,
                invoiceTemplateID,
                continuationToken,
                name,
                product,
                invoiceValidUntil);
        InlineResponse20013 response = searchService.findInvoiceTemplates(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }
}
