package dev.vality.anapi.v2;

import dev.vality.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import dev.vality.anapi.v2.model.DefaultLogicError;
import dev.vality.anapi.v2.service.DominantService;
import dev.vality.anapi.v2.testutil.MagistaUtil;
import dev.vality.anapi.v2.testutil.OpenApiUtil;
import dev.vality.anapi.v2.testutil.RandomUtil;
import dev.vality.bouncer.decisions.ArbiterSrv;
import dev.vality.magista.MerchantStatisticsServiceSrv;
import dev.vality.orgmanagement.AuthContextProviderSrv;
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
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static dev.vality.anapi.v2.testutil.MagistaUtil.createContextFragment;
import static dev.vality.anapi.v2.testutil.MagistaUtil.createJudgementAllowed;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchPaymentsTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @MockitoBean
    public MerchantStatisticsServiceSrv.Iface magistaClient;
    @MockitoBean
    public DominantService dominantService;
    @MockitoBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockitoBean
    public ArbiterSrv.Iface bouncerClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{magistaClient, dominantService, orgManagerClient, bouncerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void searchPaymentsRequiredParamsRequestSuccess() {
        when(dominantService.getShopIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayments(any())).thenReturn(MagistaUtil.createSearchPaymentRequiredResponse());
        mvc.perform(get("/lk/v2/payments")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", RandomUtil.randomRequestId())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(dominantService, times(1)).getShopIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchPayments(any());
    }

    @Test
    @SneakyThrows
    void searchPaymentsAllParamsRequestSuccess() {
        when(dominantService.getShopIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayments(any())).thenReturn(MagistaUtil.createSearchPaymentAllResponse());
        mvc.perform(get("/lk/v2/payments")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", RandomUtil.randomRequestId())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchPaymentAllParams())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(dominantService, times(1)).getShopIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchPayments(any());
    }

    @Test
    @SneakyThrows
    void searchPaymentsRequestInvalid() {
        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.remove("partyID");
        mvc.perform(get("/lk/v2/payments")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", RandomUtil.randomRequestId())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(params)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value(DefaultLogicError.CodeEnum.INVALID_REQUEST.getValue()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void searchPaymentsRequestMagistaUnavailable() {
        when(dominantService.getShopIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayments(any())).thenThrow(TException.class);
        mvc.perform(get("/lk/v2/payments")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", RandomUtil.randomRequestId())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(dominantService, times(1)).getShopIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchPayments(any());
    }
}
