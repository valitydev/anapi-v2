package com.rbkmoney.anapi.v2.testutil;

import com.rbkmoney.reporter.Report;
import com.rbkmoney.reporter.StatReportResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReporterUtil {

    public static Report createReport(long reportId) {
        return MagistaUtil.fillRequiredTBaseObject(new Report(), Report.class)
                .setReportId(reportId)
                .setReportType("paymentRegistry");
    }

    public static StatReportResponse createSearchReportsResponse() {
        return MagistaUtil.fillRequiredTBaseObject(new StatReportResponse(), StatReportResponse.class);
    }
}
