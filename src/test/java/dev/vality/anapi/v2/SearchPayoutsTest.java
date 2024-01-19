package dev.vality.anapi.v2;

import dev.vality.anapi.v2.config.AbstractConfig;
import dev.vality.anapi.v2.model.DefaultLogicError;
import dev.vality.anapi.v2.testutil.MagistaUtil;
import dev.vality.anapi.v2.testutil.OpenApiUtil;
import dev.vality.bouncer.decisions.ArbiterSrv;
import dev.vality.damsel.vortigon.VortigonServiceSrv;
import dev.vality.magista.MerchantStatisticsServiceSrv;
import dev.vality.orgmanagement.AuthContextProviderSrv;
import dev.vality.token.keeper.TokenAuthenticatorSrv;
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

import static dev.vality.anapi.v2.testutil.BouncerUtil.createContextFragment;
import static dev.vality.anapi.v2.testutil.BouncerUtil.createJudgementAllowed;
import static dev.vality.anapi.v2.testutil.TokenKeeperUtil.createAuthData;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchPayoutsTest extends AbstractConfig {

    @MockBean
    public MerchantStatisticsServiceSrv.Iface magistaClient;
    @MockBean
    public VortigonServiceSrv.Iface vortigonClient;
    @MockBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockBean
    public ArbiterSrv.Iface bouncerClient;
    @MockBean
    public TokenAuthenticatorSrv.Iface tokenKeeperClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{magistaClient, vortigonClient, orgManagerClient, bouncerClient, tokenKeeperClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void searchPayoutsRequiredParamsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(tokenKeeperClient.authenticate(any(), any())).thenReturn(createAuthData(generateSimpleJwt()));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayouts(any())).thenReturn(MagistaUtil.createSearchPayoutRequiredResponse());
        mvc.perform(get("/lk/v2/payouts")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(tokenKeeperClient, times(1)).authenticate(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchPayouts(any());
    }

    @Test
    @SneakyThrows
    void searchPayoutsAllParamsRequestSuccess() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(tokenKeeperClient.authenticate(any(), any())).thenReturn(createAuthData(generateSimpleJwt()));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayouts(any())).thenReturn(MagistaUtil.createSearchPayoutAllResponse());
        mvc.perform(get("/lk/v2/payouts")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchPayoutAllParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(tokenKeeperClient, times(1)).authenticate(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchPayouts(any());
    }

    @Test
    @SneakyThrows
    void searchPayoutsRequestInvalid() {
        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.remove("partyID");
        mvc.perform(get("/lk/v2/payouts")
                .header("Authorization", "Bearer " + generateSimpleJwt())
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
    void searchPayoutsRequestMagistaUnavailable() {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(tokenKeeperClient.authenticate(any(), any())).thenReturn(createAuthData(generateSimpleJwt()));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        when(magistaClient.searchPayouts(any())).thenThrow(TException.class);
        mvc.perform(get("/lk/v2/payouts")
                .header("Authorization", "Bearer " + generateSimpleJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(tokenKeeperClient, times(1)).authenticate(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(magistaClient, times(1)).searchPayouts(any());
    }
}
