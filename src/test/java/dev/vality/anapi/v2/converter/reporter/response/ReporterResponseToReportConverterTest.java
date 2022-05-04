package dev.vality.anapi.v2.converter.reporter.response;

import dev.vality.anapi.v2.model.Report;
import dev.vality.anapi.v2.testutil.ReporterUtil;
import dev.vality.reporter.StatReportResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReporterResponseToReportConverterTest {

    private static final ReporterResponseToReportConverter converter = new ReporterResponseToReportConverter();

    @Test
    void convert() {
        StatReportResponse response = ReporterUtil.createSearchReportsResponse();
        Report report = converter.convert(response.getReports().get(0));
        assertNotNull(report);
    }
}