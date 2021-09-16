package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.util.DamselUtil;
import com.rbkmoney.magista.ChargebackSearchQuery;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.anapi.v2.util.CommonUtil.merge;
import static com.rbkmoney.anapi.v2.util.DamselUtil.fillCommonParams;

@Component
public class ParamsToChargebackSearchQueryConverter {

    public ChargebackSearchQuery convert(String partyID,
                                         OffsetDateTime fromTime,
                                         OffsetDateTime toTime,
                                         Integer limit,
                                         String shopID,
                                         List<String> shopIDs,
                                         String paymentInstitutionRealm,
                                         Integer offset,
                                         String invoiceID,
                                         String paymentID,
                                         String chargebackID,
                                         List<String> chargebackStatuses,
                                         List<String> chargebackStages,
                                         List<String> chargebackCategories,
                                         String continuationToken) {
        return new ChargebackSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, merge(shopID, shopIDs),
                                continuationToken))
                .setInvoiceIds(invoiceID != null ? List.of(invoiceID) : null)
                .setPaymentId(paymentID)
                .setChargebackId(chargebackID)
                .setChargebackStatuses(chargebackStatuses != null
                        ? chargebackStatuses.stream()
                        .map(DamselUtil::mapToDamselStatus)
                        .collect(Collectors.toList())
                        : null
                )
                .setChargebackStages(chargebackStages != null
                        ? chargebackStages.stream()
                        .map(DamselUtil::mapToDamselStage)
                        .collect(Collectors.toList())
                        : null
                )
                .setChargebackCategories(chargebackCategories != null
                        ? chargebackCategories.stream()
                        .map(DamselUtil::mapToDamselCategory)
                        .collect(Collectors.toList())
                        : null);
    }
}
