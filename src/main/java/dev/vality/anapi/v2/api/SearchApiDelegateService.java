package dev.vality.anapi.v2.api;

import dev.vality.anapi.v2.converter.magista.request.*;
import dev.vality.anapi.v2.model.*;
import dev.vality.anapi.v2.security.AccessData;
import dev.vality.anapi.v2.security.AccessService;
import dev.vality.anapi.v2.service.MagistaService;
import dev.vality.anapi.v2.util.ConverterUtil;
import dev.vality.anapi.v2.util.DeadlineUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
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
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchInvoices")
                        .partyId(partyID)
                        .shopIds(ConverterUtil.merge(shopID, shopIDs))
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2008 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2008();
        } else {
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
            response = magistaService.searchInvoices(query);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2009> searchPayments(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm, String invoiceID, List<String> invoiceIDs, String paymentID, String paymentStatus, String paymentFlow, String paymentMethod, String paymentTerminalProvider, String payerEmail, String payerIP, String payerFingerprint, String customerID, String first6, String last4, String rrn, String approvalCode, String bankCardTokenProvider, String bankCardPaymentSystem, Long paymentAmountFrom, Long paymentAmountTo, String externalID, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchPayments")
                        .partyId(partyID)
                        .shopIds(ConverterUtil.merge(shopID, shopIDs))
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2009 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2009();
        } else {
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
            response = magistaService.searchPayments(query);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20010> searchRefunds(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String invoiceID, List<String> invoiceIDs, String paymentID, String refundID, String refundStatus, String externalID, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchRefunds")
                        .partyId(partyID)
                        .shopIds(ConverterUtil.merge(shopID, shopIDs))
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse20010 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse20010();
        } else {
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
            response = magistaService.searchRefunds(query);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20011> searchChargebacks(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String invoiceID, String paymentID, String chargebackID, List<String> chargebackStatuses, List<String> chargebackStages, List<String> chargebackCategories, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchChargebacks")
                        .partyId(partyID)
                        .shopIds(ConverterUtil.merge(shopID, shopIDs))
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse20011 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse20011();
        } else {
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
            response = magistaService.searchChargebacks(query);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20012> searchPayouts(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, String shopID, List<String> shopIDs, String paymentInstitutionRealm, String payoutID, String payoutToolType, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchPayouts")
                        .partyId(partyID)
                        .shopIds(ConverterUtil.merge(shopID, shopIDs))
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse20012 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse20012();
        } else {
            var query = payoutSearchConverter.convert(
                    partyID,
                    fromTime,
                    toTime,
                    limit,
                    shopIDs,
                    payoutID,
                    payoutToolType,
                    continuationToken);
            response = magistaService.searchPayouts(query);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse20013> searchInvoiceTemplates(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, Integer limit, String xRequestDeadline, List<String> shopIDs, String paymentInstitutionRealm, String invoiceTemplateID, String invoiceTemplateStatus, String name, String product, OffsetDateTime invoiceValidUntil, String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchInvoiceTemplates")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        InlineResponse20013 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse20013();
        } else {
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
            response = magistaService.searchInvoiceTemplates(query);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }
}
