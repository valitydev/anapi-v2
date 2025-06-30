package dev.vality.anapi.v2.api;

import dev.vality.anapi.v2.converter.reporter.request.ParamsToStatReportRequestConverter;
import dev.vality.anapi.v2.model.Report;
import dev.vality.anapi.v2.model.ReportLink;
import dev.vality.anapi.v2.model.SearchReports200Response;
import dev.vality.anapi.v2.security.AccessData;
import dev.vality.anapi.v2.security.AccessService;
import dev.vality.anapi.v2.service.ReporterService;
import dev.vality.anapi.v2.util.DeadlineUtil;
import dev.vality.geck.common.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Override
    public ResponseEntity<Void> cancelReport(String xRequestID,
                                             String partyID,
                                             Long reportID,
                                             String xRequestDeadline) {
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

    @Override
    public ResponseEntity<Report> createReport(String xRequestID, String partyID, OffsetDateTime fromTime,
                                               OffsetDateTime toTime, String reportType, String xRequestDeadline,
                                               String shopID) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        var shops = shopID != null ? List.of(shopID) : null;
        accessService.checkUserAccess(
                AccessData.builder()
                        .operationId("CreateReport")
                        .partyId(partyID)
                        .shopIds(shops)
                        .build());
        var request = statReportRequestConverter.mapToReportRequest(partyID, shopID, fromTime, toTime);
        var reportId = reporterService.createReport(request, statReportRequestConverter.mapReportType(reportType));
        var response = reporterService.getReport(reportId);
        log.info("<- Res [201]: xRequestID={}", xRequestID);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ReportLink> downloadFile(String xRequestID, String partyID, Long reportID, String fileID,
                                                   String xRequestDeadline) {
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

    @Override
    public ResponseEntity<Report> getReport(String xRequestID, String partyID, Long reportID, String xRequestDeadline) {
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

    @Override
    public ResponseEntity<SearchReports200Response> searchReports(String xRequestID, String partyID,
                                                                  OffsetDateTime fromTime,
                                                                  OffsetDateTime toTime, Integer limit,
                                                                  List<String> reportTypes, String xRequestDeadline,
                                                                  String shopID, String paymentInstitutionRealm,
                                                                  String continuationToken) {
        DeadlineUtil.checkDeadline(xRequestDeadline, xRequestID);
        List<String> shopIDs = accessService.getRestrictedShops(
                AccessData.builder()
                        .operationId("SearchReports")
                        .partyId(partyID)
                        .shopIds(shopID == null ? null : List.of(shopID))
                        .realm(paymentInstitutionRealm)
                        .build());
        SearchReports200Response response;
        if (shopID == null || shopIDs.contains(shopID)) {
            var request =
                    statReportRequestConverter.convert(partyID, shopID, fromTime, toTime, limit, reportTypes,
                            continuationToken);
            response = reporterService.getReports(request);
        } else {
            response = new SearchReports200Response();
        }
        log.info("<- Res [200]: xRequestID={}", xRequestID);
        return ResponseEntity.ok(response);
    }

}
