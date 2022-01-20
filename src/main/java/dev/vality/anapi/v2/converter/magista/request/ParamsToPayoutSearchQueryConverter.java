package dev.vality.anapi.v2.converter.magista.request;

import dev.vality.anapi.v2.exception.BadRequestException;
import dev.vality.anapi.v2.util.ConverterUtil;
import dev.vality.magista.PayoutSearchQuery;
import dev.vality.magista.PayoutToolType;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class ParamsToPayoutSearchQueryConverter {

    public PayoutSearchQuery convert(String partyID,
                                     OffsetDateTime fromTime,
                                     OffsetDateTime toTime,
                                     Integer limit,
                                     List<String> shopIDs,
                                     String payoutID,
                                     String payoutToolType,
                                     String continuationToken) {
        return new PayoutSearchQuery()
                .setCommonSearchQueryParams(
                        ConverterUtil.fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setPayoutId(payoutID)
                .setPayoutType(payoutToolType != null ? mapPayoutToolType(payoutToolType) : null);
    }

    protected PayoutToolType mapPayoutToolType(String payoutToolType) {
        return switch (payoutToolType) {
            case "PayoutAccount" -> PayoutToolType.payout_account;
            case "Wallet" -> PayoutToolType.wallet;
            case "PaymentInstitutionAccount" -> PayoutToolType.payment_institution_account;
            default -> throw new BadRequestException(
                    String.format("PayoutToolType %s cannot be processed", payoutToolType));
        };
    }
}
