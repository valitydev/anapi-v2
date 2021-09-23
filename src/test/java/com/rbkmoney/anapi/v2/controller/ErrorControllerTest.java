package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import com.rbkmoney.anapi.v2.converter.search.request.*;
import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.service.SearchService;
import com.rbkmoney.anapi.v2.testutil.OpenApiUtil;
import com.rbkmoney.openapi.anapi_v2.model.DefaultLogicError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
    private SearchService searchService;
    @MockBean
    private ParamsToPaymentSearchQueryConverter paymentSearchConverter;
    @MockBean
    private ParamsToChargebackSearchQueryConverter chargebackSearchConverter;
    @MockBean
    private ParamsToInvoiceSearchQueryConverter invoiceSearchConverter;
    @MockBean
    private ParamsToPayoutSearchQueryConverter payoutSearchConverter;
    @MockBean
    private ParamsToRefundSearchQueryConverter refundSearchConverter;

    @Test
    void testConstraintViolationException() throws Exception {
        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.set("limit", "1001");

        mockMvc.perform(
                get("/payments")
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
        String message = "Error!";
        doThrow(new BadRequestException(message)).when(refundSearchConverter)
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());

        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();

        mockMvc.perform(
                get("/refunds")
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

        verify(refundSearchConverter, times(1))
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());
    }

    @Test
    void testMissingServletRequestParameterException() throws Exception {

        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.remove("limit");

        mockMvc.perform(
                get("/refunds")
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
                get("/refunds")
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
        doThrow(new RuntimeException()).when(refundSearchConverter)
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());

        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();

        mockMvc.perform(
                get("/refunds")
                        .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                        .header("X-Request-ID", randomUUID())
                        .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(""))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").doesNotExist());
        verify(refundSearchConverter, times(1))
                .convert(any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any(),
                        any(), any(), any(), any());
    }

    @Test
    void testUnauthorizedException() throws Exception {
        mockMvc.perform(
                get("/refunds")
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
