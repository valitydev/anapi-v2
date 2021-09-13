package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.damsel.domain.InvoicePaymentChargebackCategory;
import com.rbkmoney.damsel.domain.PayoutToolInfo;
import com.rbkmoney.geck.common.util.TypeUtil;
import com.rbkmoney.magista.InvoiceStatus;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.model.Payer;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MerchantStatisticsServiceSrv.Iface magistaClient;

    public InlineResponse20010 findPayments(PaymentSearchQuery query) {
        try {
            StatPaymentResponse magistaResponse = magistaClient.searchPayments(query);
            List<PaymentSearchResult> results = new ArrayList<>(magistaResponse.getPaymentsSize());
            for (StatPayment payment : magistaResponse.getPayments()) {
                PaymentSearchResult result = new PaymentSearchResult()
                        .amount(payment.getAmount())
                        .createdAt(TypeUtil.stringToInstant(payment.getCreatedAt()).atOffset(ZoneOffset.UTC))
                        .currency(payment.getCurrencySymbolicCode())
                        .externalID(payment.getExternalId())
                        .fee(payment.getFee())
                        .flow(new PaymentFlow()
                                .type(payment.getFlow().isSetHold() ? PaymentFlow.TypeEnum.PAYMENTFLOWHOLD :
                                        PaymentFlow.TypeEnum.PAYMENTFLOWINSTANT))
                        .geoLocationInfo(new GeoLocationInfo()
                                .cityGeoID(payment.getLocationInfo().getCityGeoId())
                                .countryGeoID(payment.getLocationInfo().getCountryGeoId()))
                        .id(payment.getId())
                        .invoiceID(payment.getInvoiceId())
                        .makeRecurrent(payment.isMakeRecurrent())
                        .payer(getPayer(payment))
                        .shopID(payment.getShopId())
                        .shortID(payment.getShortId())
                        .status(getStatus(payment.getStatus()))
                        .statusChangedAt(TypeUtil.stringToInstant(getAt(payment.getStatus()))
                                .atOffset(ZoneOffset.UTC))
                        .transactionInfo(new TransactionInfo()
                                .approvalCode(payment.getAdditionalTransactionInfo().getApprovalCode())
                                .rrn(payment.getAdditionalTransactionInfo().getRrn())
                        );
                results.add(result);
            }
            return new InlineResponse20010()
                    .result(results)
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (TException e) {
            e.printStackTrace();
        }
        //TODO: Error processing;
        return null;
    }

    public InlineResponse2008 findChargebacks(ChargebackSearchQuery query) {
        try {
            StatChargebackResponse magistaResponse = magistaClient.searchChargebacks(query);
            List<Chargeback> results = new ArrayList<>(magistaResponse.getChargebacksSize());
            for (StatChargeback chargeback : magistaResponse.getChargebacks()) {
                Chargeback result = new Chargeback()
                        .bodyAmount(chargeback.getAmount())
                        .createdAt(TypeUtil.stringToInstant(chargeback.getCreatedAt()).atOffset(ZoneOffset.UTC))
                        .chargebackId(chargeback.getChargebackId())
                        .fee(chargeback.getFee())
                        .chargebackReason(new ChargebackReason()
                                .category(mapToCategory(chargeback.getChargebackReason().getCategory()))
                                .code(chargeback.getChargebackReason().getCode()))
                        .content(new Content().data(chargeback.getContent().getData())
                                .type(chargeback.getContent().getType()))
                        .bodyCurrency(chargeback.getCurrencyCode().getSymbolicCode());
                results.add(result);
            }
            return new InlineResponse2008()
                    .result(results)
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (TException e) {
            e.printStackTrace();
        }
        //TODO: Error processing;
        return null;
    }

    private ChargebackCategory mapToCategory(InvoicePaymentChargebackCategory chargebackCategory) {
        if (chargebackCategory.isSetAuthorisation()) {
            return ChargebackCategory.AUTHORISATION;
        }

        if (chargebackCategory.isSetDispute()) {
            return ChargebackCategory.DISPUTE;
        }

        if (chargebackCategory.isSetFraud()) {
            return ChargebackCategory.FRAUD;
        }

        if (chargebackCategory.isSetProcessingError()) {
            return ChargebackCategory.PROCESSING_ERROR;
        }

        return null;
    }

    public InlineResponse2009 findInvoices(InvoiceSearchQuery query) {
        try {
            StatInvoiceResponse magistaResponse = magistaClient.searchInvoices(query);
            List<Invoice> results = new ArrayList<>(magistaResponse.getInvoicesSize());
            for (StatInvoice invoice : magistaResponse.getInvoices()) {
                Invoice result = new Invoice()
                        .amount(invoice.getAmount())
                        .createdAt(TypeUtil.stringToInstant(invoice.getCreatedAt()).atOffset(ZoneOffset.UTC))
                        .currency(invoice.getCurrencySymbolicCode())
                        .externalID(invoice.getExternalId())
                        .cart(invoice.getCart().getLines().stream().map(invoiceLine -> new InvoiceLine()
                                        .cost(invoiceLine.getQuantity() * invoiceLine.getPrice().getAmount())
                                        .price(invoiceLine.getPrice().getAmount())
                                        .product(invoiceLine.getProduct())
                                //.getTaxMode()
                        ).collect(Collectors.toList()))
                        .description(invoice.getDescription())
                        .dueDate(TypeUtil.stringToInstant(invoice.getDue()).atOffset(ZoneOffset.UTC))
                        .id(invoice.getId())
                        .product(invoice.getProduct())
                        //.reason()
                        .shopID(invoice.getShopId())
                        .status(mapToStatus(invoice.getStatus()));
                results.add(result);
            }
            return new InlineResponse2009()
                    .result(results)
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (TException e) {
            e.printStackTrace();
        }
        //TODO: Error processing;
        return null;
    }

    private Invoice.StatusEnum mapToStatus(InvoiceStatus status) {
        if (status.isSetFulfilled()) {
            return Invoice.StatusEnum.FULFILLED;
        }

        if (status.isSetPaid()) {
            return Invoice.StatusEnum.PAID;
        }

        if (status.isSetUnpaid()) {
            return Invoice.StatusEnum.UNPAID;
        }

        if (status.isSetCancelled()) {
            return Invoice.StatusEnum.CANCELLED;
        }

        throw new IllegalArgumentException("");
    }

    public InlineResponse20011 findPayouts(PayoutSearchQuery query) {
        try {
            StatPayoutResponse magistaResponse = magistaClient.searchPayouts(query);
            List<Payout> results = new ArrayList<>(magistaResponse.getPayoutsSize());
            for (StatPayout payout : magistaResponse.getPayouts()) {
                Payout result = new Payout()
                        .amount(payout.getAmount())
                        .createdAt(TypeUtil.stringToInstant(payout.getCreatedAt()).atOffset(ZoneOffset.UTC))
                        .currency(payout.getCurrencySymbolicCode())
                        .fee(payout.getFee())
                        // .cancellationDetails
                        .id(payout.getId())
                        .payoutToolDetails(mapToPayoutToolDetails(payout.getPayoutToolInfo()))
                        .shopID(payout.getShopId())
                        .status(mapToStatus(payout.getStatus()))
                        .cancellationDetails(
                                payout.getStatus().isSetCancelled() ? payout.getStatus().getCancelled().getDetails() :
                                        null);
                results.add(result);
            }
            return new InlineResponse20011()
                    .result(results)
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (TException e) {
            e.printStackTrace();
        }
        //TODO: Error processing;
        return null;
    }

    private String mapToStatus(PayoutStatus status) {
        if (status.isSetCancelled()) {
            return "Cancelled";
        }

        if (status.isSetPaid()) {
            return "Paid";
        }

        if (status.isSetConfirmed()) {
            return "Confirmed";
        }

        if (status.isSetUnpaid()) {
            return "Unpaid";
        }

        throw new IllegalArgumentException("");
    }

    private PayoutToolDetails mapToPayoutToolDetails(PayoutToolInfo payoutToolInfo) {
        if (payoutToolInfo.isSetRussianBankAccount()) {
            var account = payoutToolInfo.getRussianBankAccount();
            return new PayoutToolDetailsBankAccount()
                    .account(account.getAccount())
                    .bankBik(account.getBankBik())
                    .bankName(account.getBankName())
                    .bankPostAccount(account.getBankPostAccount())
                    .detailsType("PayoutToolDetailsBankAccount");
        }

        if (payoutToolInfo.isSetInternationalBankAccount()) {
            var account = payoutToolInfo.getInternationalBankAccount();
            return new PayoutToolDetailsInternationalBankAccount()
                    .iban(account.getIban())
                    .number(account.getNumber())
                    .bankDetails(new InternationalBankDetails()
                            .name(account.getBank().getName())
                            .bic(account.getBank().getBic())
                            .countryCode(account.getBank().getCountry().name())
                            .address(account.getBank().getAddress())
                            .abartn(account.getBank().getAbaRtn()))
                    .correspondentBankAccount(mapToInternationalCorrespondentBankAccount(account))
                    .detailsType("PayoutToolDetailsInternationalBankAccount");
        }

        if (payoutToolInfo.isSetPaymentInstitutionAccount()) {
            return new PayoutToolDetailsPaymentInstitutionAccount()
                    .detailsType("PayoutToolDetailsPaymentInstitutionAccount");
        }

        if (payoutToolInfo.isSetWalletInfo()) {
            return new PayoutToolDetailsWalletInfo()
                    .walletID(payoutToolInfo.getWalletInfo().getWalletId())
                    .detailsType("PayoutToolDetailsWalletInfo");
        }

        throw new IllegalArgumentException("");

    }

    private InternationalCorrespondentBankAccount mapToInternationalCorrespondentBankAccount(
            com.rbkmoney.damsel.domain.InternationalBankAccount account) {
        var details = account.getBank();
        return new InternationalCorrespondentBankAccount()
                .bankDetails(new InternationalBankDetails()
                        .name(details.getName())
                        .bic(details.getBic())
                        .countryCode(details.getCountry().name())
                        .address(details.getAddress())
                        .abartn(details.getAbaRtn()))
                .iban(account.getIban())
                .number(account.getNumber())
                .correspondentBankAccount(
                        mapToInternationalCorrespondentBankAccount(account.getCorrespondentAccount()));
    }

    public InlineResponse20012 findRefunds(RefundSearchQuery query) {
        try {
            StatRefundResponse magistaResponse = magistaClient.searchRefunds(query);
            List<RefundSearchResult> results = new ArrayList<>(magistaResponse.getRefundsSize());
            for (StatRefund refund : magistaResponse.getRefunds()) {
                RefundSearchResult result = new RefundSearchResult()
                        .amount(refund.getAmount())
                        .createdAt(TypeUtil.stringToInstant(refund.getCreatedAt()).atOffset(ZoneOffset.UTC))
                        .currency(refund.getCurrencySymbolicCode())
                        // .cancellationDetails
                        .id(refund.getId())
                        .shopID(refund.getShopId())
                        .status(mapToStatus(refund.getStatus()))
                        .externalID(refund.getExternalId())
                        .error(new RefundStatusError()
                                .code());
                results.add(result);
            }
            return new InlineResponse20012()
                    .result(results)
                    .continuationToken(magistaResponse.getContinuationToken());
        } catch (TException e) {
            e.printStackTrace();
        }
        //TODO: Error processing;
        return null;
    }

    private RefundSearchResult.StatusEnum mapToStatus(InvoicePaymentRefundStatus status) {
        if (status.isSetPending()) {
            return RefundSearchResult.StatusEnum.PENDING;
        }

        if (status.isSetFailed()) {
            return RefundSearchResult.StatusEnum.FAILED;
        }

        if (status.isSetSucceeded()) {
            return RefundSearchResult.StatusEnum.SUCCEEDED;
        }

        throw new IllegalArgumentException("");
    }

    private Payer getPayer(StatPayment payment) {
        var statPayer = payment.getPayer();
        Payer payer = new Payer();

        if (statPayer.isSetCustomer()) {
            return payer.payerType(Payer.PayerTypeEnum.CUSTOMERPAYER);
        }

        if (statPayer.isSetPaymentResource()) {
            return payer.payerType(Payer.PayerTypeEnum.PAYMENTRESOURCEPAYER);
        }

        if (statPayer.isSetRecurrent()) {
            return payer.payerType(Payer.PayerTypeEnum.RECURRENTPAYER);
        }

        return null;
    }

    private PaymentSearchResult.StatusEnum getStatus(InvoicePaymentStatus status) {
        if (status.isSetCancelled()) {
            return PaymentSearchResult.StatusEnum.CANCELLED;
        }

        if (status.isSetCaptured()) {
            return PaymentSearchResult.StatusEnum.CAPTURED;
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            return PaymentSearchResult.StatusEnum.PROCESSED;
        }

        if (status.isSetPending()) {
            return PaymentSearchResult.StatusEnum.PENDING;
        }

        if (status.isSetProcessed()) {
            return PaymentSearchResult.StatusEnum.PROCESSED;
        }

        if (status.isSetRefunded()) {
            return PaymentSearchResult.StatusEnum.REFUNDED;
        }

        throw new IllegalArgumentException("");

    }

    private String getAt(InvoicePaymentStatus status) {
        if (status.isSetCancelled()) {
            return status.getCancelled().getAt();
        }

        if (status.isSetCaptured()) {
            return status.getCaptured().getAt();
        }

        if (status.isSetChargedBack()) {
            //TODO: Clearify
        }

        if (status.isSetFailed()) {
            return status.getFailed().getAt();
        }

        if (status.isSetProcessed()) {
            return status.getProcessed().getAt();
        }

        if (status.isSetRefunded()) {
            return status.getRefunded().getAt();
        }

        return null;

    }
}
