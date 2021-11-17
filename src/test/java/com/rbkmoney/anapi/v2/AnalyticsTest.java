package com.rbkmoney.anapi.v2;

import com.rbkmoney.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import com.rbkmoney.anapi.v2.model.DefaultLogicError;
import com.rbkmoney.anapi.v2.testutil.AnalyticsUtil;
import com.rbkmoney.anapi.v2.testutil.OpenApiUtil;
import com.rbkmoney.bouncer.decisions.ArbiterSrv;
import com.rbkmoney.damsel.analytics.AnalyticsServiceSrv;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import com.rbkmoney.orgmanagement.AuthContextProviderSrv;
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
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createContextFragment;
import static com.rbkmoney.anapi.v2.testutil.MagistaUtil.createJudgementAllowed;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalyticsTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @MockBean
    public VortigonServiceSrv.Iface vortigonClient;
    @MockBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockBean
    public ArbiterSrv.Iface bouncerClient;
    @MockBean
    private AnalyticsServiceSrv.Iface analyticsClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{analyticsClient, vortigonClient, orgManagerClient, bouncerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void getAveragePaymentRequiredParamsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(analyticsClient.getAveragePayment(any())).thenReturn(AnalyticsUtil.createAveragePaymentRequiredResponse());
        mvc.perform(get("/lk/v2/analytics/payments/average")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getAnalyticsRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(analyticsClient, times(1)).getAveragePayment(any());
    }

    @Test
    @SneakyThrows
    void getAveragePaymentAllParamsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(analyticsClient.getAveragePayment(any())).thenReturn(AnalyticsUtil.createAveragePaymentAllResponse());
        mvc.perform(get("/lk/v2/analytics/payments/average")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getAnalyticsAllParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(analyticsClient, times(1)).getAveragePayment(any());
    }

    @Test
    @SneakyThrows
    void getAveragePaymentRequestInvalid() {
        MultiValueMap<String, String> params = OpenApiUtil.getAnalyticsRequiredParams();
        params.remove("partyID");
        mvc.perform(get("/lk/v2/analytics/payments/average")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(params)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(DefaultLogicError.CodeEnum.INVALIDREQUEST.getValue()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void getAveragePaymentRequestServerUnavailable() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(analyticsClient.getAveragePayment(any())).thenThrow(TException.class);
        mvc.perform(get("/lk/v2/analytics/payments/average")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getAnalyticsRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(analyticsClient, times(1)).getAveragePayment(any());
    }
}
