package com.rbkmoney.anapi.v2.converter.search.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.ChargebackSearchQuery;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackCategory;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackStage;
import com.rbkmoney.openapi.anapi_v2.model.ChargebackStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.rbkmoney.anapi.v2.util.ConverterUtil.fillCommonParams;

@Component
public class ParamsToChargebackSearchQueryConverter {

    public ChargebackSearchQuery convert(String partyID,
                                         OffsetDateTime fromTime,
                                         OffsetDateTime toTime,
                                         Integer limit,
                                         List<String> shopIDs,
                                         Integer offset,
                                         String invoiceID,
                                         String paymentID,
                                         String chargebackID,
                                         List<String> chargebackStatuses,
                                         List<String> chargebackStages,
                                         List<String> chargebackCategories,
                                         String continuationToken) {
        //TODO: Mapping for offset
        return new ChargebackSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setInvoiceIds(invoiceID != null ? List.of(invoiceID) : null)
                .setPaymentId(paymentID)
                .setChargebackId(chargebackID)
                .setChargebackStatuses(chargebackStatuses != null
                        ? chargebackStatuses.stream()
                        .map(this::mapToDamselStatus)
                        .collect(Collectors.toList())
                        : null
                )
                .setChargebackStages(chargebackStages != null
                        ? chargebackStages.stream()
                        .map(this::mapToDamselStage)
                        .collect(Collectors.toList())
                        : null
                )
                .setChargebackCategories(chargebackCategories != null
                        ? chargebackCategories.stream()
                        .map(this::mapToDamselCategory)
                        .collect(Collectors.toList())
                        : null);
    }

    private InvoicePaymentChargebackStage mapToDamselStage(String chargebackStage) {
        var stage = Enum.valueOf(ChargebackStage.class, chargebackStage);
        var damselStage = new InvoicePaymentChargebackStage();
        switch (stage) {
            case CHARGEBACK -> damselStage.setChargeback(new InvoicePaymentChargebackStageChargeback());
            case PRE_ARBITRATION -> damselStage.setPreArbitration(new InvoicePaymentChargebackStagePreArbitration());
            case ARBITRATION -> damselStage.setArbitration(new InvoicePaymentChargebackStageArbitration());
            default -> throw new BadRequestException(
                    String.format("Chargeback stage %s cannot be processed", chargebackStage));
        }

        return damselStage;
    }

    private InvoicePaymentChargebackStatus mapToDamselStatus(String chargebackStatus) {
        var status = Enum.valueOf(ChargebackStatus.class, chargebackStatus);
        var damselStatus = new InvoicePaymentChargebackStatus();
        switch (status) {
            case PENDING -> damselStatus.setPending(new InvoicePaymentChargebackPending());
            case ACCEPTED -> damselStatus.setAccepted(new InvoicePaymentChargebackAccepted());
            case REJECTED -> damselStatus.setRejected(new InvoicePaymentChargebackRejected());
            case CANCELLED -> damselStatus.setCancelled(new InvoicePaymentChargebackCancelled());
            default -> throw new BadRequestException(
                    String.format("Chargeback status %s cannot be processed", chargebackStatus));
        }

        return damselStatus;
    }

    private InvoicePaymentChargebackCategory mapToDamselCategory(String chargebackCategory) {
        var category = Enum.valueOf(ChargebackCategory.class, chargebackCategory);
        var damselCategory = new InvoicePaymentChargebackCategory();
        switch (category) {
            case FRAUD -> damselCategory.setFraud(new InvoicePaymentChargebackCategoryFraud());
            case DISPUTE -> damselCategory.setDispute(new InvoicePaymentChargebackCategoryDispute());
            case AUTHORISATION -> damselCategory
                    .setAuthorisation(new InvoicePaymentChargebackCategoryAuthorisation());
            case PROCESSING_ERROR -> damselCategory
                    .setProcessingError(new InvoicePaymentChargebackCategoryProcessingError());
            default -> throw new BadRequestException(
                    String.format("Chargeback category %s cannot be processed", chargebackCategory));
        }

        return damselCategory;
    }
}
