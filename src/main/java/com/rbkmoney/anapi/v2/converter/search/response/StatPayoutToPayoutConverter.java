package com.rbkmoney.anapi.v2.converter.search.response;

import com.rbkmoney.anapi.v2.util.OpenApiUtil;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.StatPayout;
import com.rbkmoney.openapi.anapi_v2.model.Payout;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

import static com.rbkmoney.anapi.v2.util.OpenApiUtil.mapToPayoutToolDetails;

@Component
public class StatPayoutToPayoutConverter {

    public Payout convert(StatPayout payout) {
        return new Payout()
                .amount(payout.getAmount())
                .createdAt(TypeUtil.stringToInstant(payout.getCreatedAt()).atOffset(ZoneOffset.UTC))
                .currency(payout.getCurrencySymbolicCode())
                .fee(payout.getFee())
                .id(payout.getId())
                .payoutToolDetails(mapToPayoutToolDetails(payout.getPayoutToolInfo()))
                .shopID(payout.getShopId())
                .status(OpenApiUtil.mapToPayoutStatus(payout.getStatus()))
                .cancellationDetails(
                        payout.getStatus().isSetCancelled()
                                ? payout.getStatus().getCancelled().getDetails()
                                : null);
    }
}
