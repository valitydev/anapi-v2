package dev.vality.anapi.v2.converter.reporter.request;

import dev.vality.anapi.v2.model.Report;
import dev.vality.reporter.ReportRequest;
import dev.vality.reporter.ReportTimeRange;
import dev.vality.reporter.StatReportRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParamsToStatReportRequestConverter {

    public StatReportRequest convert(String partyId, String shopId, OffsetDateTime fromTime,
                                     OffsetDateTime toTime, Integer limit,
                                     List<String> reportTypes, String continuationToken) {
        return new StatReportRequest()
                .setRequest(mapToReportRequest(partyId, shopId, fromTime, toTime))
                .setReportTypes(mapReportTypes(reportTypes))
                .setLimit(limit)
                .setContinuationToken(continuationToken);
    }

    public ReportRequest mapToReportRequest(String partyId, String shopId, OffsetDateTime fromTime,
                                 OffsetDateTime toTime) {
        return new ReportRequest()
                .setPartyId(partyId)
                .setShopId(shopId)
                .setTimeRange(new ReportTimeRange()
                        .setFromTime(fromTime.format(DateTimeFormatter.ISO_INSTANT))
                        .setToTime(toTime.format(DateTimeFormatter.ISO_INSTANT)));
    }

    public String mapReportType(String requestReportType) {
        Report.ReportTypeEnum inputType = Report.ReportTypeEnum.fromValue(requestReportType);
        return switch (inputType) {
            case PAYMENT_REGISTRY -> "payment_registry";
            case PROVISION_OF_SERVICE -> "provision_of_service";
        };
    }

    private List<String> mapReportTypes(List<String> requestReportTypes) {
        return requestReportTypes.stream().map(this::mapReportType).collect(Collectors.toList());
    }

}
