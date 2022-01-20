package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.exception.ReporterException;
import com.rbkmoney.geck.common.util.TypeUtil;
import dev.vality.anapi.v2.model.*;
import dev.vality.reporter.ReportRequest;
import dev.vality.reporter.ReportingSrv;
import dev.vality.reporter.StatReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporterService {

    private final ReportingSrv.Iface reporterClient;

    public void cancelReport(long reportId) {
        try {
            reporterClient.cancelReport(reportId);
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.cancelReport, reportId=%d", reportId), e);
        }
    }

    public long createReport(ReportRequest request, String reportType) {
        try {
            return reporterClient.createReport(request, reportType);
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.createReport, partyId=%s, reportType=%s",
                            request.getPartyId(), reportType), e);
        }
    }

    public ReportLink getDownloadUrl(String fileId, String expiresAt) {
        try {
            return new ReportLink().url(reporterClient.generatePresignedUrl(fileId, expiresAt));
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.generatePresignedUrl, fileId=%s, expiresAt=%s",
                            fileId, expiresAt), e);
        }
    }

    public Report getReport(long reportId) {
        try {
            return mapReport(reporterClient.getReport(reportId));
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.getReport, reportId=%d", reportId), e);
        }
    }

    public InlineResponse20014 getReports(StatReportRequest request) {
        try {
            var response = reporterClient.getReports(request);
            return new InlineResponse20014()
                    .result(response.getReports().stream()
                            .map(this::mapReport)
                            .collect(Collectors.toList()))
                    .continuationToken(request.getContinuationToken());
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.getReports, partyId=%s",
                            request.getRequest().getPartyId()), e);
        }
    }

    private Report mapReport(dev.vality.reporter.Report response) {
        return new Report()
                .id(response.getReportId())
                .partyID(response.getPartyId())
                .shopID(response.getShopId())
                .createdAt(TypeUtil.stringToInstant(response.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .fromTime(TypeUtil.stringToInstant(response.getTimeRange().getFromTime()).atOffset(ZoneOffset.UTC))
                .toTime(TypeUtil.stringToInstant(response.getTimeRange().getToTime()).atOffset(ZoneOffset.UTC))
                .status(Report.StatusEnum.fromValue(response.getStatus().name()))
                .reportType(Report.ReportTypeEnum.fromValue(response.getReportType()))
                .files(response.getFiles() != null
                        ? response.getFiles().stream()
                        .map(fileReporter -> new FileMeta()
                                .id(fileReporter.getFileId())
                                .filename(fileReporter.getFilename())
                                .signatures(new FileMetaSignatures()
                                        .md5(fileReporter.getSignature().getMd5())
                                        .sha256(fileReporter.getSignature().getSha256())))
                        .collect(Collectors.toList()) : Collections.emptyList());
    }

}
