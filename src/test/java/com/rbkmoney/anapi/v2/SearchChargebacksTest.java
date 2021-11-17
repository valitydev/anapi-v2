package com.rbkmoney.anapi.v2;

import com.rbkmoney.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import com.rbkmoney.anapi.v2.model.DefaultLogicError;
import com.rbkmoney.anapi.v2.testutil.MagistaUtil;
import com.rbkmoney.anapi.v2.testutil.OpenApiUtil;
import com.rbkmoney.bouncer.decisions.ArbiterSrv;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import com.rbkmoney.magista.MerchantStatisticsServiceSrv;
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


class SearchChargebacksTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @MockBean
    public MerchantStatisticsServiceSrv.Iface magistaClient;
    @MockBean
    public VortigonServiceSrv.Iface vortigonClient;
    @MockBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockBean
    public ArbiterSrv.Iface bouncerClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{magistaClient, vortigonClient, orgManagerClient, bouncerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void searchChargebacksRequiredParamsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchChargebacks(any())).thenReturn(MagistaUtil.createSearchChargebackRequiredResponse());
        mvc.perform(get("/lk/v2/chargebacks")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchChargebacks(any());
    }

    @Test
    @SneakyThrows
    void searchChargebacksAllParamsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchChargebacks(any())).thenReturn(MagistaUtil.createSearchChargebackAllResponse());
        mvc.perform(get("/lk/v2/chargebacks")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchChargebackAllParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchChargebacks(any());
    }

    @Test
    @SneakyThrows
    void searchChargebacksRequestInvalid() {
        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.remove("partyID");
        mvc.perform(get("/lk/v2/chargebacks")
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
    void searchChargebacksRequestMagistaUnavailable() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayments(any())).thenThrow(TException.class);
        mvc.perform(get("/lk/v2/chargebacks")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchChargebacks(any());
    }
}
