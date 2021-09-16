package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.damsel.domain.InvoicePaymentChargebackCategory;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatChargeback;
import com.rbkmoney.openapi.anapi_v2.model.Chargeback;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackCategory;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackReason;
import com.rbkmoney.openapi.anapi_v2.model.Content;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class StatChargebackToChargebackConverter {

    public Chargeback convert(StatChargeback chargeback) {
        return new Chargeback()
                .bodyAmount(chargeback.getAmount())
                .createdAt(TypeUtil.stringToInstant(chargeback.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .chargebackId(chargeback.getChargebackId())
                .fee(chargeback.getFee())
                .chargebackReason(chargeback.getChargebackReason() != null
                        ? new ChargebackReason()
                        .category(mapToChargebackCategory(chargeback.getChargebackReason().getCategory()))
                        .code(chargeback.getChargebackReason().getCode()) : null)
                .content(chargeback.getContent() != null
                        ? new Content().data(chargeback.getContent().getData())
                        .type(chargeback.getContent().getType()) : null)
                .bodyCurrency(chargeback.getCurrencyCode().getSymbolicCode());
    }

    private ChargebackCategory mapToChargebackCategory(InvoicePaymentChargebackCategory chargebackCategory) {
        if (chargebackCategory.isSetAuthorisation()) {
            //TODO: Is it a typo? Could be fixed? (authorization)
            return ChargebackCategory.AUTHORISATION;
        }

        if (chargebackCategory.isSetDispute()) {
            return ChargebackCategory.DISPUTE;
        }

        if (chargebackCategory.isSetFraud()) {
            return ChargebackCategory.FRAUD;
        }

        if (chargebackCategory.isSetProcessingError()) {
            return ChargebackCategory.PROCESSING_ERROR;
        }

        throw new IllegalArgumentException(
                String.format("Chargeback category %s cannot be processed", chargebackCategory));
    }
}
