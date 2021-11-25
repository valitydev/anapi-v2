package com.rbkmoney.anapi.v2.api;

import com.rbkmoney.anapi.v2.model.InlineResponse20014;
import com.rbkmoney.anapi.v2.model.Report;
import com.rbkmoney.anapi.v2.model.ReportLink;
import com.rbkmoney.anapi.v2.security.AccessService;
import com.rbkmoney.anapi.v2.service.ReporterService;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.reporter.ReportRequest;
import com.rbkmoney.reporter.ReportTimeRange;
import com.rbkmoney.reporter.StatReportRequest;
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

import static com.rbkmoney.anapi.v2.util.DeadlineUtil.checkDeadline;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"ParameterName"})
public class ReportsApiDelegateService implements ReportsApiDelegate {

    private final AccessService accessService;
    private final ReporterService reporterService;
    @Value("${service.reporter.reportUrlLifetimeSec}")
    private long reportLifetimeSec = 60L;

    @PreAuthorize("hasAuthority('party:write')")
    @Override
    public ResponseEntity<Void> cancelReport(String xRequestID, String partyID, Long reportID, String xRequestDeadline,
                                             String paymentInstitutionRealm) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        accessService.getAccessibleShops(
                "CancelReport",
                partyID,
                paymentInstitutionRealm);
        reporterService.cancelReport(reportID);
        log.info("<- Res [202]: xRequestID={}", xRequestID);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasAuthority('party:write')")
    @Override
    public ResponseEntity<Report> createReport(String xRequestID, String partyID, OffsetDateTime fromTime,
                                               OffsetDateTime toTime, String reportType, String xRequestDeadline,
                                               String shopID, String paymentInstitutionRealm) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        var shops = shopID != null ? List.of(shopID) : null;
        accessService.getAccessibleShops("CreateReport", partyID, shops, paymentInstitutionRealm);
        var request = getReportRequest(partyID, shopID, fromTime, toTime);
        var reportId = reporterService.createReport(request, reportType);
        var response = reporterService.getReport(reportId);
        log.info("<- Res [201]: xRequestID={}", xRequestID);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('party:read')")
    @Override
    public ResponseEntity<ReportLink> downloadFile(String xRequestID, String partyID, Long reportID, String fileID,
                                                   String xRequestDeadline, String paymentInstitutionRealm) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        accessService.getAccessibleShops(
                "DownloadFile",
                partyID,
                paymentInstitutionRealm);
        var response = reporterService.getDownloadUrl(fileID,
                TypeUtil.temporalToString(LocalDateTime.now().plus(reportLifetimeSec, ChronoUnit.SECONDS)));
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('party:read')")
    @Override
    public ResponseEntity<Report> getReport(String xRequestID, String partyID, Long reportID, String xRequestDeadline,
                                            String paymentInstitutionRealm) {
        log.info("-> Req: xRequestID={}", xRequestID);
        checkDeadline(xRequestDeadline, xRequestID);
        accessService.getAccessibleShops(
                "GetReport",
                partyID,
                paymentInstitutionRealm);
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
        checkDeadline(xRequestDeadline, xRequestID);
        accessService.getAccessibleShops(
                "SearchReports",
                partyID,
                shopID == null ? null : List.of(shopID),
                paymentInstitutionRealm);
        var request = getStatReportRequest(partyID, shopID, fromTime, toTime, limit, reportTypes, continuationToken);
        var response = reporterService.getReports(request);
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

    private StatReportRequest getStatReportRequest(String partyId, String shopId, OffsetDateTime fromTime,
                                                   OffsetDateTime toTime, Integer limit,
                                                   List<String> reportTypes, String continuationToken) {
        return new StatReportRequest()
                .setRequest(
                        new ReportRequest()
                                .setPartyId(partyId)
                                .setShopId(shopId)
                                .setTimeRange(new ReportTimeRange()
                                        .setFromTime(fromTime.toString())
                                        .setToTime(toTime.toString())))
                .setReportTypes(reportTypes)
                .setLimit(limit)
                .setContinuationToken(continuationToken);
    }
}
