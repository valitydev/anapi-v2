package com.rbkmoney.anapi.v2.converter.magista.request;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.model.ChargebackCategory;
import com.rbkmoney.anapi.v2.model.ChargebackStage;
import com.rbkmoney.anapi.v2.model.ChargebackStatus;
import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.magista.ChargebackSearchQuery;
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
                                         String invoiceID,
                                         String paymentID,
                                         String chargebackID,
                                         List<String> chargebackStatuses,
                                         List<String> chargebackStages,
                                         List<String> chargebackCategories,
                                         String continuationToken) {
        return new ChargebackSearchQuery()
                .setCommonSearchQueryParams(
                        fillCommonParams(fromTime, toTime, limit, partyID, shopIDs, continuationToken))
                .setInvoiceIds(invoiceID != null ? List.of(invoiceID) : null)
                .setPaymentId(paymentID)
                .setChargebackId(chargebackID)
                .setChargebackStatuses(chargebackStatuses != null
                        ? chargebackStatuses.stream()
                        .map(this::mapStatus)
                        .collect(Collectors.toList())
                        : null
                )
                .setChargebackStages(chargebackStages != null
                        ? chargebackStages.stream()
                        .map(this::mapStage)
                        .collect(Collectors.toList())
                        : null
                )
                .setChargebackCategories(chargebackCategories != null
                        ? chargebackCategories.stream()
                        .map(this::mapCategory)
                        .collect(Collectors.toList())
                        : null);
    }

    protected InvoicePaymentChargebackStage mapStage(String chargebackStage) {
        try {
            var stage = ChargebackStage.fromValue(chargebackStage);
            var damselStage = new InvoicePaymentChargebackStage();
            switch (stage) {
                case CHARGEBACK -> damselStage.setChargeback(new InvoicePaymentChargebackStageChargeback());
                case PRE_ARBITRATION -> damselStage.setPreArbitration(
                        new InvoicePaymentChargebackStagePreArbitration());
                case ARBITRATION -> damselStage.setArbitration(new InvoicePaymentChargebackStageArbitration());
                default -> throw new BadRequestException(
                        String.format("Chargeback stage %s cannot be processed", chargebackStage));
            }
            return damselStage;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Chargeback stage %s cannot be processed", chargebackStage));
        }
    }

    protected InvoicePaymentChargebackStatus mapStatus(String chargebackStatus) {
        try {
            var status = ChargebackStatus.fromValue(chargebackStatus);
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
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Chargeback status %s cannot be processed", chargebackStatus));
        }
    }

    protected InvoicePaymentChargebackCategory mapCategory(String chargebackCategory) {
        try {
            var category = ChargebackCategory.fromValue(chargebackCategory);
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
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    String.format("Chargeback category %s cannot be processed", chargebackCategory));
        }
    }
}
