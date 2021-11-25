package com.rbkmoney.anapi.v2;

import com.rbkmoney.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import com.rbkmoney.anapi.v2.testutil.OpenApiUtil;
import com.rbkmoney.bouncer.decisions.ArbiterSrv;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import com.rbkmoney.orgmanagement.AuthContextProviderSrv;
import com.rbkmoney.reporter.ReportingSrv;
import lombok.SneakyThrows;
import org.apache.thrift.TException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createContextFragment;
import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createJudgementAllowed;
import static com.rbkmoney.anapi.v2.testutil.OpenApiUtil.getReportsRequiredParams;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomInt;
import static com.rbkmoney.anapi.v2.testutil.RandomUtil.randomIntegerAsString;
import static com.rbkmoney.anapi.v2.testutil.ReporterUtil.createReport;
import static com.rbkmoney.anapi.v2.testutil.ReporterUtil.createSearchReportsResponse;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportsTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @MockBean
    public VortigonServiceSrv.Iface vortigonClient;
    @MockBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockBean
    public ArbiterSrv.Iface bouncerClient;
    @MockBean
    private ReportingSrv.Iface reporterClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[] {reporterClient, vortigonClient, orgManagerClient, bouncerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void cancelReportRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        int reportId = randomInt(1, 1000);
        mvc.perform(post("/lk/v2/reports/{reportId}/cancel", reportId)
                        .header("Authorization", "Bearer " + generatePartyWriteJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(getReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").doesNotExist());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).cancelReport(reportId);
    }

    @Test
    @SneakyThrows
    void cancelReportRequestServerUnavailable() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        int reportId = randomInt(1, 1000);
        doThrow(new TException()).when(reporterClient).cancelReport(reportId);
        mvc.perform(post("/lk/v2/reports/{reportId}/cancel", reportId)
                        .header("Authorization", "Bearer " + generatePartyWriteJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(getReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").doesNotExist());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).cancelReport(reportId);
    }

    @Test
    @SneakyThrows
    void createReportRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        long reportId = randomInt(1, 1000);
        when(reporterClient.createReport(any(), any())).thenReturn(reportId);
        when(reporterClient.getReport(reportId)).thenReturn(createReport(reportId));
        mvc.perform(post("/lk/v2/reports")
                        .header("Authorization", "Bearer " + generatePartyWriteJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getCreateReportRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).createReport(any(), any());
        verify(reporterClient, times(1)).getReport(reportId);
    }

    @Test
    @SneakyThrows
    void downloadUrlRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        String reportId = randomIntegerAsString(1, 1000);
        String fileId = randomIntegerAsString(1, 1000);
        when(reporterClient.generatePresignedUrl(eq(fileId), any())).thenReturn("www.google.ru");
        mvc.perform(get("/lk/v2/reports/{reportID}/files/{fileID}/download", reportId, fileId)
                        .header("Authorization", "Bearer " + generatePartyReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).generatePresignedUrl(eq(fileId), notNull());
    }

    @Test
    @SneakyThrows
    void getReportRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        long reportId = randomInt(1, 1000);
        when(reporterClient.getReport(reportId)).thenReturn(createReport(reportId));
        mvc.perform(get("/lk/v2/reports/{reportID}", reportId)
                        .header("Authorization", "Bearer " + generatePartyReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getCreateReportRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(reporterClient, times(1)).getReport(reportId);
    }

    @Test
    @SneakyThrows
    void getSearchReportsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(reporterClient.getReports(any())).thenReturn(createSearchReportsResponse());
        mvc.perform(get("/lk/v2/reports")
                        .header("Authorization", "Bearer " + generatePartyReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(OpenApiUtil.getSearchReportsRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
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
                        .header("Authorization", "Bearer " + generatePartyReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists());
    }

}
