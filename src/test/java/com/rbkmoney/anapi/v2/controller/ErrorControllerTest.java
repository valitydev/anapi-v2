package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import com.rbkmoney.anapi.v2.converter.magista.request.ParamsToRefundSearchQueryConverter;
import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.model.DefaultLogicError;
import com.rbkmoney.anapi.v2.testutil.OpenApiUtil;
import com.rbkmoney.bouncer.decisions.ArbiterSrv;
import com.rbkmoney.damsel.vortigon.VortigonServiceSrv;
import com.rbkmoney.orgmanagement.AuthContextProviderSrv;
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

class ErrorControllerTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ParamsToRefundSearchQueryConverter refundSearchConverter;
    @MockBean
    public VortigonServiceSrv.Iface vortigonClient;
    @MockBean
    public AuthContextProviderSrv.Iface orgManagerClient;
    @MockBean
    public ArbiterSrv.Iface bouncerClient;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{refundSearchConverter, vortigonClient, orgManagerClient, bouncerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    void testConstraintViolationException() throws Exception {
        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.set("limit", "1001");

        mockMvc.perform(
                        get("/lk/v2/payments")
                                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                                .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(DefaultLogicError.CodeEnum.INVALIDREQUEST.getValue()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void testBadRequestException() throws Exception {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        String message = "Error!";
        doThrow(new BadRequestException(message)).when(refundSearchConverter)
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());

        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();

        mockMvc.perform(
                get("/lk/v2/refunds")
                                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                                .header("X-Request-ID", randomUUID())
                                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                                .params(params)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(DefaultLogicError.CodeEnum.INVALIDREQUEST.getValue()))
                .andExpect(jsonPath("$.message").value(message));
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(refundSearchConverter, times(1))
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());
    }

    @Test
    void testMissingServletRequestParameterException() throws Exception {

        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.remove("limit");

        mockMvc.perform(
                get("/lk/v2/refunds")
                        .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(DefaultLogicError.CodeEnum.INVALIDREQUEST.getValue()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void testDeadlineException() throws Exception {
        mockMvc.perform(
                get("/lk/v2/refunds")
                        .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", "fail")
                        .params(OpenApiUtil.getSearchRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(DefaultLogicError.CodeEnum.INVALIDDEADLINE.getValue()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void testInternalException() throws Exception {
        when(vortigonClient.getShopsIds(any(), any())).thenReturn(List.of("1", "2", "3"));
        when(orgManagerClient.getUserContext(any())).thenReturn(createContextFragment());
        when(bouncerClient.judge(any(), any())).thenReturn(createJudgementAllowed());
        doThrow(new RuntimeException()).when(refundSearchConverter)
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());

        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();

        mockMvc.perform(
                get("/lk/v2/refunds")
                                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                                .header("X-Request-ID", randomUUID())
                                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                                .params(params)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .content(""))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").doesNotExist());
        verify(vortigonClient, times(1)).getShopsIds(any(), any());
        verify(orgManagerClient, times(1)).getUserContext(any());
        verify(bouncerClient, times(1)).judge(any(), any());
        verify(refundSearchConverter, times(1))
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());
    }

    @Test
    void testUnauthorizedException() throws Exception {
        mockMvc.perform(
                get("/lk/v2/refunds")
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", "fail")
                        .params(OpenApiUtil.getSearchRequiredParams())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());
    }

}
