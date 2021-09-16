package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatChargeback;
import com.rbkmoney.openapi.anapi_v2.model.Chargeback;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackReason;
import com.rbkmoney.openapi.anapi_v2.model.Content;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

import static com.rbkmoney.anapi.v2.util.OpenApiUtil.mapToChargebackCategory;

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
}
