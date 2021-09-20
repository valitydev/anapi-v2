package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.openapi.anapi_v2.model.PaymentStatus;
import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ConverterUtil {

    public static List<String> merge(@Nullable String id, @Nullable List<String> ids) {
        if (id != null) {
            if (ids == null) {
                ids = new ArrayList<>();
            }
            ids.add(id);
        }
        return ids;
    }

    public static CommonSearchQueryParams fillCommonParams(OffsetDateTime fromTime, OffsetDateTime toTime,
                                                           Integer limit,
                                                           String partyId, List<String> shopIDs,
                                                           String continuationToken) {
        return new CommonSearchQueryParams()
                .setContinuationToken(continuationToken)
                .setFromTime(TypeUtil.temporalToString(fromTime.toLocalDateTime()))
                .setToTime(TypeUtil.temporalToString(toTime.toLocalDateTime()))
                .setLimit(limit)
                .setPartyId(partyId)
                .setShopIds(shopIDs);
    }
}
