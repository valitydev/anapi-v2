package dev.vality.anapi.v2.util;

import dev.vality.geck.common.util.TypeUtil;
import dev.vality.magista.CommonSearchQueryParams;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ConverterUtil {

    public static List<String> merge(@Nullable String id, @Nullable List<String> ids) {
        List<String> identifiers = new ArrayList<>();
        if (id != null) {
            identifiers.add(id);
        }

        if (ids != null && !ids.isEmpty()) {
            identifiers.addAll(ids);
        }
        return identifiers;
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
