package dev.vality.anapi.v2.converter.reporter.request;

import dev.vality.anapi.v2.model.Report;
import dev.vality.anapi.v2.testutil.RandomUtil;
import dev.vality.geck.common.util.TypeUtil;
import dev.vality.reporter.ReportRequest;
import dev.vality.reporter.StatReportRequest;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class ParamsToStatReportRequestConverterTest {

    private static final ParamsToStatReportRequestConverter converter =
            new ParamsToStatReportRequestConverter();

    @Test
    void convert() {
        String partyId = RandomUtil.randomString(2);
        String shopId = RandomUtil.randomString(2);
        int limit = RandomUtil.randomInt(1, 10);
        StatReportRequest request = converter.convert(
                partyId,
                shopId,
                OffsetDateTime.MIN,
                OffsetDateTime.now(),
                limit,
                Arrays.stream(Report.ReportTypeEnum.values()).map(Report.ReportTypeEnum::getValue).collect(
                        Collectors.toList()),
                RandomUtil.randomString(5)
        );

        assertNotNull(request);
        assertNotNull(request.getRequest());
        assertEquals(partyId, request.getRequest().getPartyId());
        assertEquals(shopId, request.getRequest().getShopId());

        assertNotNull(request.getRequest().getTimeRange().getFromTime());
        assertNotNull(request.getRequest().getTimeRange().getToTime());

        String fromTime = request.getRequest().getTimeRange().getFromTime();
        String toTime = request.getRequest().getTimeRange().getToTime();
        assertDoesNotThrow(() -> TypeUtil.stringToInstant(fromTime));
        assertDoesNotThrow(() -> TypeUtil.stringToInstant(toTime));

        assertEquals(limit, request.getLimit());
        assertEquals(Report.ReportTypeEnum.values().length, request.getReportTypes().size());
        assertNotNull(request.getContinuationToken());
    }

    @Test
    void mapToReportRequest() {
        String partyId = RandomUtil.randomString(2);
        String shopId = RandomUtil.randomString(2);
        ReportRequest request = converter.mapToReportRequest(
                partyId,
                shopId,
                OffsetDateTime.MIN,
                OffsetDateTime.now()
        );

        assertNotNull(request);
        assertEquals(partyId, request.getPartyId());
        assertEquals(shopId, request.getShopId());

        assertNotNull(request.getTimeRange().getFromTime());
        assertNotNull(request.getTimeRange().getToTime());

        String fromTime = request.getTimeRange().getFromTime();
        String toTime = request.getTimeRange().getToTime();
        assertDoesNotThrow(() -> TypeUtil.stringToInstant(fromTime));
        assertDoesNotThrow(() -> TypeUtil.stringToInstant(toTime));
    }

    @Test
    void mapReportType() {
        for (Report.ReportTypeEnum type : Report.ReportTypeEnum.values()) {
            assertDoesNotThrow(() -> converter.mapReportType(type.getValue()));
        }
    }
}