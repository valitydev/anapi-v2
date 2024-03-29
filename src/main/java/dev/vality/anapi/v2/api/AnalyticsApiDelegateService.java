package dev.vality.anapi.v2.api;

import dev.vality.anapi.v2.model.*;
import dev.vality.anapi.v2.security.AccessData;
import dev.vality.anapi.v2.security.AccessService;
import dev.vality.anapi.v2.service.AnalyticsService;
import dev.vality.anapi.v2.util.DeadlineUtil;
import dev.vality.damsel.analytics.FilterRequest;
import dev.vality.damsel.analytics.MerchantFilter;
import dev.vality.damsel.analytics.SplitFilterRequest;
import dev.vality.damsel.analytics.SplitUnit;
import dev.vality.damsel.analytics.TimeFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"ParameterName", "LineLength"})
public class AnalyticsApiDelegateService implements AnalyticsApiDelegate {

    private final AccessService accessService;
    private final AnalyticsService analyticsService;

    @Override
    public ResponseEntity<InlineResponse200> getAveragePayment(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetAveragePayment")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse200 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse200();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getAveragePayment(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse200> getCreditingsAmount(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetCreditingsAmount")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse200 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse200();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getCreditingsAmount(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse200> getCurrentBalances(String xRequestID, String partyID, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetCurrentBalances")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse200 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse200();
        } else {
            var merchantFilter = getMerchantFilter(partyID, shopIDs, excludeShopIDs);
            response = analyticsService.getCurrentBalances(merchantFilter);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2007> getCurrentShopBalances(String xRequestID, String partyID, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetCurrentShopBalances")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2007 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2007();
        } else {
            var merchantFilter = getMerchantFilter(partyID, shopIDs, excludeShopIDs);
            response = analyticsService.getCurrentShopBalances(merchantFilter);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse200> getPaymentsAmount(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsAmount")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse200 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse200();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getPaymentsAmount(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2001> getPaymentsCount(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsCount")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2001 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2001();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getPaymentsCount(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2002> getPaymentsErrorDistribution(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsErrorDistribution")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2002 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2002();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getPaymentsErrorDistribution(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2003> getPaymentsSplitAmount(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String splitUnit, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsSplitAmount")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2003 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2003();
        } else {
            var splitFilterRequest = getSplitFilterRequest(
                    partyID,
                    shopIDs, excludeShopIDs, fromTime,
                    toTime,
                    splitUnit
            );
            response = analyticsService.getPaymentsSplitAmount(splitFilterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2004> getPaymentsSplitCount(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String splitUnit, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsSplitCount")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2004 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2004();
        } else {
            var splitFilterRequest = getSplitFilterRequest(
                    partyID,
                    shopIDs, excludeShopIDs, fromTime,
                    toTime,
                    splitUnit
            );
            response = analyticsService.getPaymentsSplitCount(splitFilterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2005> getPaymentsSubErrorDistribution(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsSubErrorDistribution")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2005 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2005();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getPaymentsSubErrorDistribution(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse2006> getPaymentsToolDistribution(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetPaymentsToolDistribution")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse2006 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse2006();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getPaymentsToolDistribution(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InlineResponse200> getRefundsAmount(String xRequestID, String partyID, OffsetDateTime fromTime, OffsetDateTime toTime, String xRequestDeadline, List<String> shopIDs, List<String> excludeShopIDs, String paymentInstitutionRealm) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("GetRefundsAmount")
                        .partyId(partyID)
                        .shopIds(shopIDs)
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse200 response;
        if (shopIDs.isEmpty()) {
            response = new InlineResponse200();
        } else {
            var filterRequest = getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime);
            response = analyticsService.getRefundsAmount(filterRequest);
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    private SplitFilterRequest getSplitFilterRequest(
            String partyID,
            List<String> shopIDs,
            List<String> excludeShopIDs,
            OffsetDateTime fromTime,
            OffsetDateTime toTime,
            String splitUnit) {
        return new SplitFilterRequest()
                .setFilterRequest(getFilterRequest(partyID, shopIDs, excludeShopIDs, fromTime, toTime))
                .setSplitUnit(SplitUnit.valueOf(splitUnit.toUpperCase()));
    }

    private FilterRequest getFilterRequest(
            String partyID,
            List<String> shopIDs,
            List<String> excludeShopIDs,
            OffsetDateTime fromTime,
            OffsetDateTime toTime) {
        return new FilterRequest()
                .setMerchantFilter(getMerchantFilter(partyID, shopIDs, excludeShopIDs))
                .setTimeFilter(new TimeFilter()
                        .setFromTime(fromTime.format(DateTimeFormatter.ISO_INSTANT))
                        .setToTime(toTime.format(DateTimeFormatter.ISO_INSTANT)));
    }

    private MerchantFilter getMerchantFilter(String partyID, List<String> shopIDs, List<String> excludeShopIDs) {
        return new MerchantFilter()
                .setPartyId(partyID)
                .setShopIds(shopIDs)
                .setExcludeShopIds(excludeShopIDs);
    }
}
