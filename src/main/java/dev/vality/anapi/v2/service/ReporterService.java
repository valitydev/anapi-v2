package dev.vality.anapi.v2.service;

import dev.vality.anapi.v2.converter.reporter.response.ReporterResponseToReportConverter;
import dev.vality.anapi.v2.exception.ReporterException;
import dev.vality.anapi.v2.model.InlineResponse20013;
import dev.vality.anapi.v2.model.Report;
import dev.vality.anapi.v2.model.ReportLink;
import dev.vality.reporter.ReportRequest;
import dev.vality.reporter.ReportingSrv;
import dev.vality.reporter.StatReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporterService {

    private final ReportingSrv.Iface reporterClient;
    private final ReporterResponseToReportConverter reporterResponseToReportConverter;

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
            return reporterResponseToReportConverter.convert(reporterClient.getReport(reportId));
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.getReport, reportId=%d", reportId), e);
        }
    }

    public InlineResponse20013 getReports(StatReportRequest request) {
        try {
            var response = reporterClient.getReports(request);
            return new InlineResponse20013()
                    .result(response.getReports().stream()
                            .map(reporterResponseToReportConverter::convert)
                            .toList())
                    .continuationToken(request.getContinuationToken());
        } catch (TException e) {
            throw new ReporterException(
                    String.format("Error while call reporterClient.getReports, partyId=%s",
                            request.getRequest().getPartyId()), e);
        }
    }

}
