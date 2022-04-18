package dev.vality.anapi.v2.converter.reporter.request;

import dev.vality.anapi.v2.model.Report;
import dev.vality.reporter.ReportRequest;
import dev.vality.reporter.ReportTimeRange;
import dev.vality.reporter.StatReportRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParamsToStatReportRequestConverter {

    public StatReportRequest convert(String partyId, String shopId, OffsetDateTime fromTime,
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
                .setReportTypes(mapReportTypes(reportTypes))
                .setLimit(limit)
                .setContinuationToken(continuationToken);
    }

    private List<String> mapReportTypes(List<String> requestReportTypes) {
        return requestReportTypes.stream().map(input -> {
            Report.ReportTypeEnum inputType = Report.ReportTypeEnum.fromValue(input);
            return switch (inputType) {
                case PAYMENTREGISTRY -> "payment_registry";
                case PROVISIONOFSERVICE -> "provision_of_service";
                case PAYMENTREGISTRYBYPAYOUT -> "payment_registry_by_payout";
                default -> throw new IllegalArgumentException("Unknown report type: " + inputType.getValue());
            };
        }).collect(Collectors.toList());
    }

}
