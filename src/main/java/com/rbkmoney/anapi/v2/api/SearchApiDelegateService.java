package com.rbkmoney.anapi.v2.api;

import com.rbkmoney.anapi.v2.converter.magista.request.*;
import com.rbkmoney.anapi.v2.model.*;
import com.rbkmoney.anapi.v2.security.AccessService;
import com.rbkmoney.anapi.v2.service.MagistaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.merge;
import static com.rbkmoney.anapi.v2.util.DeadlineUtil.checkDeadline;

@Service
@PreAuthorize("hasAuthority('invoices:read')")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"ParameterName", "LineLength"})
public class SearchApiDelegateService implements SearchApiDelegate {

    private final MagistaService magistaService;
    private final AccessService accessService;
    private final ParamsToInvoiceSearchQueryConverter invoiceSearchConverter;
    private final ParamsToPaymentSearchQueryConverter paymentSearchConverter;
    private final ParamsToRefundSearchQueryConverter refundSearchConverter;
    private final ParamsToChargebackSearchQueryConverter chargebackSearchConverter;
    private final ParamsToPayoutSearchQueryConverter payoutSearchConverter;
    private final ParamsToInvoiceTemplateSearchQueryConverter invoiceTemplateSearchConverter;

    @Override
    public ResponseEntity<InlineResponse2008> searchInvoices(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String invoiceID, List<String> invoiceIDs, String invoiceStatus, Long invoiceAmountFrom, Long invoiceAmountTo, String externalID, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getAccessibleShops(
                "SearchInvoices",
                partyID,
                merge(shopID, shopIDs),
                paymentInstitutionRealm);
        var query = invoiceSearchConverter.convert(
                partyID,
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
        var response = magistaService.searchInvoices(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2009> searchPayments(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm, String invoiceID, List<String> invoiceIDs, String paymentID, String paymentStatus, String paymentFlow, String paymentMethod, String paymentTerminalProvider, String payerEmail, String payerIP, String payerFingerprint, String customerID, String first6, String last4, String rrn, String approvalCode, String bankCardTokenProvider, String bankCardPaymentSystem, Long paymentAmountFrom, Long paymentAmountTo, String externalID, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getAccessibleShops(
                "SearchPayments",
                partyID,
                merge(shopID, shopIDs),
                paymentInstitutionRealm);
        var query = paymentSearchConverter.convert(
                partyID,
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
                excludeShopIDs,
                continuationToken);
        var response = magistaService.searchPayments(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20010> searchRefunds(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String invoiceID, List<String> invoiceIDs, String paymentID, String refundID, String refundStatus, String externalID, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getAccessibleShops(
                "SearchRefunds",
                partyID,
                merge(shopID, shopIDs),
                paymentInstitutionRealm);
        var query = refundSearchConverter.convert(
                partyID,
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
        var response = magistaService.searchRefunds(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20011> searchChargebacks(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String invoiceID, String paymentID, String chargebackID, List<String> chargebackStatuses, List<String> chargebackStages, List<String> chargebackCategories, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getAccessibleShops(
                "SearchChargebacks",
                partyID,
                merge(shopID, shopIDs),
                paymentInstitutionRealm);
        var query = chargebackSearchConverter.convert(
                partyID,
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
        var response = magistaService.searchChargebacks(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20012> searchPayouts(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String payoutID, String payoutToolType, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getAccessibleShops(
                "SearchPayouts",
                partyID,
                merge(shopID, shopIDs),
                paymentInstitutionRealm);
        var query = payoutSearchConverter.convert(
                partyID,
                fromTime,
                toTime,
                limit,
                shopIDs,
                payoutID,
                payoutToolType,
                continuationToken);
        var response = magistaService.searchPayouts(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20013> searchInvoiceTemplates(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, List<String> shopIDs, String paymentInstitutionRealm, String invoiceTemplateID, String invoiceTemplateStatus, String name, String product, OffsetDateTime invoiceValidUntil, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        shopIDs = accessService.getAccessibleShops(
                "SearchInvoiceTemplates",
                partyID,
                shopIDs,
                paymentInstitutionRealm);
        checkDeadline(xRequestDeadline, xRequestID);
        var query = invoiceTemplateSearchConverter.convert(
                partyID,
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
        var response = magistaService.searchInvoiceTemplates(query);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }
}
