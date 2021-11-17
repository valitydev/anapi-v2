package com.rbkmoney.anapi.v2.converter.magista.response;

import com.rbkmoney.anapi.v2.model.Chargeback;
import com.rbkmoney.anapi.v2.model.ChargebackCategory;
import com.rbkmoney.anapi.v2.model.ChargebackReason;
import com.rbkmoney.anapi.v2.model.Content;
import com.rbkmoney.damsel.domain.InvoicePaymentChargebackCategory;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatChargeback;
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
                .chargebackReason(chargeback.isSetChargebackReason()
                        ? new ChargebackReason()
                        .category(mapCategory(chargeback.getChargebackReason().getCategory()))
                        .code(chargeback.getChargebackReason().getCode()) : null)
                .content(chargeback.isSetContent()
                        ? new Content().data(chargeback.getContent().getData())
                        .type(chargeback.getContent().getType()) : null)
                .bodyCurrency(chargeback.getCurrencyCode().getSymbolicCode());
    }

    protected ChargebackCategory mapCategory(InvoicePaymentChargebackCategory chargebackCategory) {
        try {
            var field = InvoicePaymentChargebackCategory._Fields.findByName(
                    chargebackCategory.getSetField().getFieldName());
            return switch (field) {
                case FRAUD -> ChargebackCategory.FRAUD;
                case DISPUTE -> ChargebackCategory.DISPUTE;
                case AUTHORISATION -> ChargebackCategory.AUTHORISATION;
                case PROCESSING_ERROR -> ChargebackCategory.PROCESSING_ERROR;
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Chargeback category %s cannot be processed", chargebackCategory));
        }
    }
}
