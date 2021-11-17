package com.rbkmoney.anapi.v2.service;

import com.rbkmoney.anapi.v2.exception.AnalytycsException;
import com.rbkmoney.anapi.v2.model.OffsetAmount;
import com.rbkmoney.anapi.v2.model.OffsetCount;
import com.rbkmoney.anapi.v2.model.SplitUnit;
import com.rbkmoney.anapi.v2.model.SubError;
import com.rbkmoney.anapi.v2.model.*;
import com.rbkmoney.damsel.analytics.*;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsServiceSrv.Iface analyticsClient;

    public InlineResponse2006 getPaymentsToolDistribution(FilterRequest filterRequest) {
        try {
            var distribution = analyticsClient.getPaymentsToolDistribution(filterRequest);
            return new InlineResponse2006()
                    .result(distribution.getPaymentToolsDistributions().stream()
                            .map(o -> new PaymentsToolDistributionResult()
                                    .name(o.getName())
                                    .percents(o.getPercents()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsToolDistribution", e);
        }
    }

    public InlineResponse200 getPaymentsAmount(FilterRequest filterRequest) {
        try {
            var paymentsAmount = analyticsClient.getPaymentsAmount(filterRequest);
            return new InlineResponse200()
                    .result(paymentsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsAmount", e);
        }
    }

    public InlineResponse200 getCreditingsAmount(FilterRequest filterRequest) {
        try {
            var creditingsAmount = analyticsClient.getCreditingsAmount(filterRequest);
            return new InlineResponse200()
                    .result(creditingsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getCreditingsAmount", e);
        }
    }

    public InlineResponse200 getAveragePayment(FilterRequest filterRequest) {
        try {
            var averagePayment = analyticsClient.getAveragePayment(filterRequest);
            return new InlineResponse200()
                    .result(averagePayment.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getAveragePayment", e);
        }
    }

    public InlineResponse2001 getPaymentsCount(FilterRequest filterRequest) {
        try {
            var paymentsCount = analyticsClient.getPaymentsCount(filterRequest);
            return new InlineResponse2001()
                    .result(paymentsCount.getGroupsCount().stream()
                            .map(o -> new CountResult()
                                    .count(o.getCount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsCount", e);
        }
    }

    public InlineResponse2002 getPaymentsErrorDistribution(FilterRequest filterRequest) {
        try {
            var distribution = analyticsClient.getPaymentsErrorDistribution(filterRequest);
            return new InlineResponse2002()
                    .result(distribution.getErrorDistributions().stream()
                            .map(o -> new PaymentsErrorsDistributionResult()
                                    .error(o.getName())
                                    .percents(o.getPercents()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsErrorDistribution", e);
        }
    }

    public InlineResponse2005 getPaymentsSubErrorDistribution(FilterRequest filterRequest) {
        try {
            var distribution = analyticsClient.getPaymentsSubErrorDistribution(filterRequest);
            return new InlineResponse2005()
                    .result(distribution.getErrorDistributions().stream()
                            .map(o -> new PaymentsSubErrorsDistributionResult()
                                    .error(getSubError(o.getError()))
                                    .percents(o.getPercents()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsSubErrorDistribution", e);
        }
    }

    public InlineResponse2003 getPaymentsSplitAmount(SplitFilterRequest splitFilterRequest) {
        try {
            var paymentsSplitAmount = analyticsClient.getPaymentsSplitAmount(splitFilterRequest);
            return new InlineResponse2003()
                    .result(paymentsSplitAmount.getGroupedCurrencyAmounts().stream()
                            .map(o -> createSplitAmountResult(o, paymentsSplitAmount.getResultSplitUnit()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsSplitAmount", e);
        }
    }

    public InlineResponse2004 getPaymentsSplitCount(SplitFilterRequest splitFilterRequest) {
        try {
            var paymentsSplitCount = analyticsClient.getPaymentsSplitCount(splitFilterRequest);
            return new InlineResponse2004()
                    .result(paymentsSplitCount.getPaymentToolsDestrobutions().stream()
                            .map(o -> createSplitCountResult(o, paymentsSplitCount.getResultSplitUnit()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsSplitCount", e);
        }
    }

    public InlineResponse200 getRefundsAmount(FilterRequest filterRequest) {
        try {
            var refundsAmount = analyticsClient.getRefundsAmount(filterRequest);
            return new InlineResponse200()
                    .result(refundsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getRefundsAmount", e);
        }
    }

    public InlineResponse200 getCurrentBalances(MerchantFilter merchantFilter) {
        try {
            var refundsAmount = analyticsClient.getCurrentBalances(merchantFilter);
            return new InlineResponse200()
                    .result(refundsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getCurrentBalances", e);
        }
    }

    public InlineResponse2007 getCurrentShopBalances(MerchantFilter merchantFilter) {
        try {
            var currentShopBalances = analyticsClient.getCurrentShopBalances(merchantFilter);
            var shopAmountResults = currentShopBalances.getGroupsAmount().stream()
                    .collect(Collectors.groupingBy(
                            ShopGroupedAmount::getShopId,
                            Collectors.mapping(
                                    o -> new AmountResult()
                                            .amount(o.getAmount())
                                            .currency(o.getCurrency()),
                                    Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> new ShopAmountResult()
                            .id(entry.getKey())
                            .amountResults(entry.getValue()))
                    .collect(Collectors.toList());
            return new InlineResponse2007()
                    .result(shopAmountResults);
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getCurrentShopBalances", e);
        }
    }

    private SubError getSubError(com.rbkmoney.damsel.analytics.SubError o) {
        return new SubError()
                .code(o.getCode())
                .subError(o.getSubError() != null ? getSubError(o.getSubError()) : null);
    }

    private SplitAmountResult createSplitAmountResult(GroupedCurrencyOffsetAmount groupedCurrencyOffsetAmount,
                                                      com.rbkmoney.damsel.analytics.SplitUnit splitUnit) {
        var splitAmountResult = new SplitAmountResult();
        splitAmountResult.setSplitUnit(SplitUnit.valueOf(splitUnit.name()));
        splitAmountResult.setCurrency(groupedCurrencyOffsetAmount.getCurrency());
        splitAmountResult.setOffsetAmounts(groupedCurrencyOffsetAmount.getOffsetAmounts().stream()
                .map(this::createOffsetAmount)
                .collect(Collectors.toList()));
        return splitAmountResult;
    }

    private SplitCountResult createSplitCountResult(GroupedCurrencyOffsetCount groupedCurrencyOffsetCount,
                                                    com.rbkmoney.damsel.analytics.SplitUnit unit) {
        var splitCountResult = new SplitCountResult();
        splitCountResult.setSplitUnit(SplitUnit.valueOf(unit.name()));
        splitCountResult.setCurrency(groupedCurrencyOffsetCount.getCurrency());
        splitCountResult.setStatusOffsetCounts(groupedCurrencyOffsetCount.getOffsetAmounts().stream()
                .map(this::createStatusOffsetCount)
                .collect(Collectors.toList()));
        return splitCountResult;
    }

    private StatusOffsetCount createStatusOffsetCount(GroupedStatusOffsetCount groupedStatusOffsetCount) {
        var statusOffsetCount = new StatusOffsetCount();
        statusOffsetCount.setStatus(StatusOffsetCount.StatusEnum.valueOf(groupedStatusOffsetCount.getStatus().name()));
        statusOffsetCount.setOffsetCount(groupedStatusOffsetCount.getOffsetCounts().stream()
                .map(this::createOffsetCount)
                .collect(Collectors.toList()));
        return statusOffsetCount;
    }

    private OffsetAmount createOffsetAmount(com.rbkmoney.damsel.analytics.OffsetAmount offsetAmount) {
        var result = new OffsetAmount();
        result.setAmount(offsetAmount.getAmount());
        result.setOffset(offsetAmount.getOffset());
        return result;
    }

    private OffsetCount createOffsetCount(com.rbkmoney.damsel.analytics.OffsetCount offsetCount) {
        var offsetCountResult = new OffsetCount();
        offsetCountResult.setCount(offsetCount.getCount());
        offsetCountResult.setOffset(offsetCount.getOffset());
        return offsetCountResult;
    }
}
