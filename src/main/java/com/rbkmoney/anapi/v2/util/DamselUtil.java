package com.rbkmoney.anapi.v2.util;

import com.rbkmoney.damsel.domain.*;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.CommonSearchQueryParams;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.util.List;

@UtilityClass
public class DamselUtil {

    public static PaymentTool mapToPaymentTool(String paymentMethod) {
        var paymentTool = new PaymentTool();
        switch (paymentMethod) {
            case "bankCard" -> paymentTool.setBankCard(new BankCard());
            case "paymentTerminal" -> paymentTool.setPaymentTerminal(new PaymentTerminal());
            default -> throw new IllegalArgumentException("");
        }

        return paymentTool;
    }

    public static InvoicePaymentFlow mapToInvoicePaymentFlow(String paymentFlow) {
        var invoicePaymentFlow = new InvoicePaymentFlow();
        switch (paymentFlow) {
            case "instant" -> invoicePaymentFlow.setInstant(new InvoicePaymentFlowInstant());
            case "hold" -> invoicePaymentFlow.setHold(new InvoicePaymentFlowHold());
            default -> throw new IllegalArgumentException("");
        }
        return invoicePaymentFlow;
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

    public static com.rbkmoney.damsel.domain.InvoicePaymentStatus getStatus(String paymentStatus) {
        var status = Enum.valueOf(PaymentStatus.StatusEnum.class, paymentStatus);
        var invoicePaymentStatus = new com.rbkmoney.damsel.domain.InvoicePaymentStatus();
        switch (status) {
            case PENDING -> invoicePaymentStatus.setPending(new InvoicePaymentPending());
            case PROCESSED -> invoicePaymentStatus.setProcessed(new InvoicePaymentProcessed());
            case CAPTURED -> invoicePaymentStatus.setCaptured(new InvoicePaymentCaptured());
            case CANCELLED -> invoicePaymentStatus.setCancelled(new InvoicePaymentCancelled());
            case REFUNDED -> invoicePaymentStatus.setRefunded(new InvoicePaymentRefunded());
            case FAILED -> invoicePaymentStatus.setFailed(new InvoicePaymentFailed());
            default -> throw new IllegalArgumentException("");
        }
        return invoicePaymentStatus;
    }

    public static InvoicePaymentChargebackStage mapToDamselStage(String chargebackStage) {
        var stage = Enum.valueOf(ChargebackStage.class, chargebackStage);
        var damselStage = new InvoicePaymentChargebackStage();
        switch (stage) {
            case CHARGEBACK -> damselStage.setChargeback(new InvoicePaymentChargebackStageChargeback());
            case PRE_ARBITRATION -> damselStage.setPreArbitration(new InvoicePaymentChargebackStagePreArbitration());
            case ARBITRATION -> damselStage.setArbitration(new InvoicePaymentChargebackStageArbitration());
            default -> throw new IllegalArgumentException("");
        }

        return damselStage;
    }

    public static InvoicePaymentChargebackStatus mapToDamselStatus(String chargebackStatus) {
        var status = Enum.valueOf(ChargebackStatus.class, chargebackStatus);
        var damselStatus = new InvoicePaymentChargebackStatus();
        switch (status) {
            case PENDING -> damselStatus.setPending(new InvoicePaymentChargebackPending());
            case ACCEPTED -> damselStatus.setAccepted(new InvoicePaymentChargebackAccepted());
            case REJECTED -> damselStatus.setRejected(new InvoicePaymentChargebackRejected());
            case CANCELLED -> damselStatus.setCancelled(new InvoicePaymentChargebackCancelled());
            default -> throw new IllegalArgumentException("");
        }

        return damselStatus;
    }

    public static InvoicePaymentChargebackCategory mapToDamselCategory(String chargebackCategory) {
        var category = Enum.valueOf(ChargebackCategory.class, chargebackCategory);
        var damselCategory = new InvoicePaymentChargebackCategory();
        switch (category) {
            case FRAUD -> damselCategory.setFraud(new InvoicePaymentChargebackCategoryFraud());
            case DISPUTE -> damselCategory.setDispute(new InvoicePaymentChargebackCategoryDispute());
            case AUTHORISATION -> damselCategory
                    .setAuthorisation(new InvoicePaymentChargebackCategoryAuthorisation());
            case PROCESSING_ERROR -> damselCategory
                    .setProcessingError(new InvoicePaymentChargebackCategoryProcessingError());
            default -> throw new IllegalArgumentException("");
        }

        return damselCategory;
    }

    public static PayoutToolInfo mapToDamselPayoutToolInfo(String payoutToolType) {
        var payoutToolInfo = new PayoutToolInfo();
        switch (payoutToolType) {
            case "PayoutAccount" -> payoutToolInfo
                    .setRussianBankAccount(new RussianBankAccount()); //TODO: Russian or international?
            case "Wallet" -> payoutToolInfo.setWalletInfo(new WalletInfo());
            case "PaymentInstitutionAccount" -> payoutToolInfo
                    .setPaymentInstitutionAccount(new PaymentInstitutionAccount());
            default -> throw new IllegalArgumentException("");
        }

        return payoutToolInfo;
    }

    public static InvoicePaymentRefundStatus getRefundStatus(String refundStatus) {
        var invoicePaymentRefundStatus = new InvoicePaymentRefundStatus();
        switch (Enum.valueOf(RefundStatus.StatusEnum.class, refundStatus)) {
            case PENDING -> invoicePaymentRefundStatus.setPending(new InvoicePaymentRefundPending());
            case SUCCEEDED -> invoicePaymentRefundStatus.setSucceeded(new InvoicePaymentRefundSucceeded());
            case FAILED -> invoicePaymentRefundStatus.setFailed(new InvoicePaymentRefundFailed());
            default -> throw new IllegalArgumentException("");
        }
        return invoicePaymentRefundStatus;
    }
}
