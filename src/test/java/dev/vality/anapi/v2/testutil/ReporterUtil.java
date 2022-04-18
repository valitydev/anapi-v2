package dev.vality.anapi.v2.testutil;

import dev.vality.reporter.Report;
import dev.vality.reporter.StatReportResponse;
import lombok.experimental.UtilityClass;

import static dev.vality.anapi.v2.testutil.DamselUtil.fillRequiredTBaseObject;

@UtilityClass
public class ReporterUtil {

    public static Report createReport(long reportId) {
        return fillRequiredTBaseObject(new Report(), Report.class)
                .setReportId(reportId)
                .setReportType("payment_registry");
    }

    public static StatReportResponse createSearchReportsResponse() {
        return fillRequiredTBaseObject(new StatReportResponse(), StatReportResponse.class);
    }
}
