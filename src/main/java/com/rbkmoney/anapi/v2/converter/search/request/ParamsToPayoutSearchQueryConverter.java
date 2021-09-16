package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.magista.PayoutSearchQuery;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

import static com.rbkmoney.anapi.v2.util.CommonUtil.merge;
import static com.rbkmoney.anapi.v2.util.DamselUtil.fillCommonParams;
import static com.rbkmoney.anapi.v2.util.DamselUtil.mapToDamselPayoutToolInfo;

@Component
public class ParamsToPayoutSearchQueryConverter {

    public PayoutSearchQuery convert(String partyID,
                                     OffsetDateTime fromTime,
                                     OffsetDateTime toTime,
                                     Integer limit,
                                     String shopID,
                                     List<String> shopIDs,
                                     String paymentInstitutionRealm,
                                     Integer offset,
                                     String payoutID,
                                     String payoutToolType,
                                     List<String> excludedShops,
                                     String continuationToken) {

        return new PayoutSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs),
                                continuationToken))
                .setPayoutId(payoutID)
                .setPayoutType(payoutToolType != null ? mapToDamselPayoutToolInfo(payoutToolType) : null);
    }
}
