package com.rbkmoney.anapi.v2;

import com.rbkmoney.anapi.v2.config.AbstractKeycloakOpenIdAsWiremockConfig;
import com.rbkmoney.anapi.v2.testutil.MagistaUtil;
import com.rbkmoney.anapi.v2.testutil.OpenApiUtil;
import com.rbkmoney.magista.MerchantStatisticsServiceSrv;
import com.rbkmoney.openapi.anapi_v2.model.DefaultLogicError;
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

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchRefundsTest extends AbstractKeycloakOpenIdAsWiremockConfig {

    @MockBean
    public MerchantStatisticsServiceSrv.Iface magistaClient;

    @Autowired
    private MockMvc mvc;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[] {magistaClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    @SneakyThrows
    void searchRefundsRequiredParamsRequestSuccess() {
        when(magistaClient.searchRefunds(any())).thenReturn(MagistaUtil.createSearchRefundRequiredResponse());
        mvc.perform(get("/refunds")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(magistaClient, times(1)).searchRefunds(any());
    }

    @Test
    @SneakyThrows
    void searchRefundsAllParamsRequestSuccess() {
        when(magistaClient.searchRefunds(any())).thenReturn(MagistaUtil.createsearchRefundAllResponse());
        mvc.perform(get("/refunds")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRefundAllParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").exists());
        verify(magistaClient, times(1)).searchRefunds(any());
    }

    @Test
    @SneakyThrows
    void searchRefundsRequestInvalid() {
        MultiValueMap<String, String> params = OpenApiUtil.getSearchRequiredParams();
        params.remove("partyID");
        mvc.perform(get("/refunds")
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
    void searchRefundsRequestMagistaUnavailable() {
        when(magistaClient.searchRefunds(any())).thenThrow(TException.class);
        mvc.perform(get("/refunds")
                .header("Authorization", "Bearer " + generateInvoicesReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .params(OpenApiUtil.getSearchRequiredParams())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(""))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(magistaClient, times(1)).searchRefunds(any());
    }
}
