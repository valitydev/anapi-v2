package dev.vality.anapi.v2;

import dev.vality.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import dev.vality.anapi.v2.service.DominantService;
import dev.vality.anapi.v2.testutil.OpenApiUtil;
import dev.vality.anapi.v2.testutil.RandomUtil;
import dev.vality.bouncer.decisions.ArbiterSrv;
import dev.vality.orgmanagement.AuthContextProviderSrv;
import dev.vality.reporter.ReportingSrv;
import lombok.SneakyThrows;
import org.apache.thrift.TException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.vality.anapi.v2.testutil.MagistaUtil.createContextFragment;
import static dev.vality.anapi.v2.testutil.MagistaUtil.createJudgementAllowed;
import static dev.vality.anapi.v2.testutil.OpenApiUtil.getReportsRequiredParams;
import static dev.vality.anapi.v2.testutil.RandomUtil.randomInt;
import static dev.vality.anapi.v2.testutil.RandomUtil.randomIntegerAsString;
import static dev.vality.anapi.v2.testutil.ReporterUtil.createReport;
import static dev.vality.anapi.v2.testutil.ReporterUtil.createSearchReportsResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportsTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @MockitoBean
    public DominantService dominantService;
    @MockitoBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockitoBean
    public ArbiterSrv.Iface bouncerClient;
    @MockitoBean
    private ReportingSrv.Iface reporterClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[] {reporterClient, dominantService, orgManagerClient, bouncerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void cancelReportRequestSuccess() {
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        int reportId = randomInt(1, 1000);
        mvc.perform(post("/lk/v2/reports/{reportId}/cancel", reportId)
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(getReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").doesNotExist());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).cancelReport(reportId);
    }

    @Test
    @SneakyThrows
    void cancelReportRequestServerUnavailable() {
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        int reportId = randomInt(1, 1000);
        doThrow(new TException()).when(reporterClient).cancelReport(reportId);
        mvc.perform(post("/lk/v2/reports/{reportId}/cancel", reportId)
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(getReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").doesNotExist());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).cancelReport(reportId);
    }

    @Test
    @SneakyThrows
    void createReportRequestSuccess() {
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        long reportId = randomInt(1, 1000);
        when(reporterClient.createReport(any(), any())).thenReturn(reportId);
        when(reporterClient.getReport(reportId)).thenReturn(createReport(reportId));
        mvc.perform(post("/lk/v2/reports")
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getCreateReportRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).createReport(any(), any());
        verify(reporterClient, times(1)).getReport(reportId);
    }

    @Test
    @SneakyThrows
    void downloadUrlRequestSuccess() {
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        String reportId = randomIntegerAsString(1, 1000);
        String fileId = randomIntegerAsString(1, 1000);
        when(reporterClient.generatePresignedUrl(eq(fileId), any())).thenReturn("www.google.ru");
        mvc.perform(get("/lk/v2/reports/{reportID}/files/{fileID}/download", reportId, fileId)
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).generatePresignedUrl(eq(fileId), notNull());
    }

    @Test
    @SneakyThrows
    void getReportRequestSuccess() {
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        long reportId = randomInt(1, 1000);
        when(reporterClient.getReport(reportId)).thenReturn(createReport(reportId));
        mvc.perform(get("/lk/v2/reports/{reportID}", reportId)
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getCreateReportRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).getReport(reportId);
    }

    @Test
    @SneakyThrows
    void getSearchReportsRequestSuccess() {
        when(dominantService.getShopIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(reporterClient.getReports(any())).thenReturn(createSearchReportsResponse());
        mvc.perform(get("/lk/v2/reports")
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getSearchReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        verify(dominantService, times(1)).getShopIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).getReports(notNull());
    }

    @Test
    @SneakyThrows
    void getSearchReportsRequestInvalid() {
        var params = OpenApiUtil.getSearchReportsRequiredParams();
        params.remove("partyID");
        mvc.perform(get("/lk/v2/reports")
                        .header("Authorization", "Bearer " + generateSimpleJwt())
                        .header("X-Request-ID", RandomUtil.randomRequestId())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists());
    }

}
