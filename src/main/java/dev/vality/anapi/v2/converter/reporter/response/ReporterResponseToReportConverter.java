package dev.vality.anapi.v2.converter.reporter.response;

import dev.vality.anapi.v2.model.FileMeta;
import dev.vality.anapi.v2.model.FileMetaSignatures;
import dev.vality.anapi.v2.model.Report;
import dev.vality.geck.common.util.TypeUtil;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ReporterResponseToReportConverter {

    public Report convert(dev.vality.reporter.Report response) {
        return new Report()
                .id(response.getReportId())
                .partyID(response.getPartyId())
                .shopID(response.getShopId())
                .createdAt(TypeUtil.stringToInstant(response.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .fromTime(TypeUtil.stringToInstant(response.getTimeRange().getFromTime()).atOffset(ZoneOffset.UTC))
                .toTime(TypeUtil.stringToInstant(response.getTimeRange().getToTime()).atOffset(ZoneOffset.UTC))
                .status(Report.StatusEnum.fromValue(response.getStatus().name()))
                .reportType(mapReportType(response.getReportType()))
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

    private Report.ReportTypeEnum mapReportType(String type) {
        return switch (type) {
            case "provision_of_service" -> Report.ReportTypeEnum.PROVISION_OF_SERVICE;
            case "payment_registry" -> Report.ReportTypeEnum.PAYMENT_REGISTRY;
            default -> throw new IllegalArgumentException("Unknown report type: " + type);
        };
    }
}
