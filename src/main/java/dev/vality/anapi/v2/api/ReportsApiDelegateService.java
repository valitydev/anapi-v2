package dev.vality.anapi.v2.api;

import dev.vality.anapi.v2.converter.magista.request.ParamsToRefundSearchQueryConverter;
import dev.vality.anapi.v2.converter.reporter.request.ParamsToStatReportRequestConverter;
import dev.vality.anapi.v2.model.InlineResponse20014;
import dev.vality.anapi.v2.model.Report;
import dev.vality.anapi.v2.model.ReportLink;
import dev.vality.anapi.v2.security.AccessData;
import dev.vality.anapi.v2.security.AccessService;
import dev.vality.anapi.v2.service.ReporterService;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.anapi.v2.util.DeadlineUtil;
import dev.vality.reporter.ReportRequest;
import dev.vality.reporter.ReportTimeRange;
import dev.vality.reporter.StatReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"ParameterName"})
public class ReportsApiDelegateService implements ReportsApiDelegate {

    private final AccessService accessService;
    private final ReporterService reporterService;

    private final ParamsToStatReportRequestConverter statReportRequestConverter;
    @Value("${service.reporter.reportUrlLifetimeSec}")
    private long reportLifetimeSec = 60L;

    @PreAuthorize("hasAuthority('party:write')")
    @Override
    public ResponseEntity<Void> cancelReport(String xRequestID,
                                             String partyID,
                                             Long reportID,
                                             String xRequestDeadline) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        accessService.checkUserAccess(
                AccessData.builder()
                        .operationId("CancelReport")
                        .partyId(partyID)
                        .reportId(String.valueOf(reportID))
                        .build());
        reporterService.cancelReport(reportID);
        log.info("<- Res [202]: xRequestID={}", xRequestID);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasAuthority('party:write')")
    @Override
    public ResponseEntity<Report> createReport(String xRequestID, String partyID, OffsetDateTime fromTime,
                                               OffsetDateTime toTime, String reportType, String xRequestDeadline,
                                               String shopID) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        var shops = shopID != null ? List.of(shopID) : null;
        accessService.checkUserAccess(
                AccessData.builder()
                        .operationId("CreateReport")
                        .partyId(partyID)
                        .shopIds(shops)
                        .build());
        var request = getReportRequest(partyID, shopID, fromTime, toTime);
        var reportId = reporterService.createReport(request, reportType);
        var response = reporterService.getReport(reportId);
        log.info("<- Res [201]: xRequestID={}", xRequestID);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('party:read')")
    @Override
    public ResponseEntity<ReportLink> downloadFile(String xRequestID, String partyID, Long reportID, String fileID,
                                                   String xRequestDeadline) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        accessService.checkUserAccess(
                AccessData.builder()
                        .operationId("DownloadFile")
                        .partyId(partyID)
                        .fileId(fileID)
                        .reportId(String.valueOf(reportID))
                        .build());
        var response = reporterService.getDownloadUrl(fileID,
                TypeUtil.temporalToString(LocalDateTime.now().plus(reportLifetimeSec, ChronoUnit.SECONDS)));
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('party:read')")
    @Override
    public ResponseEntity<Report> getReport(String xRequestID, String partyID, Long reportID, String xRequestDeadline) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        accessService.checkUserAccess(
                AccessData.builder()
                        .operationId("GetReport")
                        .partyId(partyID)
                        .reportId(String.valueOf(reportID))
                        .build());
        var response = reporterService.getReport(reportID);
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('party:read')")
    @Override
    public ResponseEntity<InlineResponse20014> searchReports(String xRequestID, String partyID, OffsetDateTime fromTime,
                                                             OffsetDateTime toTime, Integer limit,
                                                             List<String> reportTypes, String xRequestDeadline,
                                                             String shopID, String paymentInstitutionRealm,
                                                             String continuationToken) {
        log.info("-> Req: xRequestID={}", xRequestID);
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        List<String> shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchReports")
                        .partyId(partyID)
                        .shopIds(shopID == null ? null : List.of(shopID))
                        .realm(paymentInstitutionRealm)
                        .build());
        InlineResponse20014 response;
        if (shopID == null || shopIDs.contains(shopID)) {
            var request =
                    statReportRequestConverter.convert(partyID, shopID, fromTime, toTime, limit, reportTypes,
                            continuationToken);
            response = reporterService.getReports(request);
        } else {
            response = new InlineResponse20014();
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    private ReportRequest getReportRequest(String partyId, String shopId, OffsetDateTime fromTime,
                                           OffsetDateTime toTime) {
        return new ReportRequest()
                .setPartyId(partyId)
                .setShopId(shopId)
                .setTimeRange(new ReportTimeRange()
                        .setFromTime(fromTime.toString())
                        .setToTime(toTime.toString()));
    }
}
