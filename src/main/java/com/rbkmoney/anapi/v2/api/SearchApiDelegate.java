package com.rbkmoney.anapi.v2.api;

import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

public interface SearchApiDelegate
        extends
        PaymentsApiDelegate,
        ChargebacksApiDelegate,
        InvoicesApiDelegate,
        PayoutsApiDelegate,
        RefundsApiDelegate,
        InvoiceTemplatesApiDelegate {

    @Override
    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }
}
