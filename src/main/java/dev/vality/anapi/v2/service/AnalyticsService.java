package dev.vality.anapi.v2.service;

import dev.vality.anapi.v2.exception.AnalytycsException;
import dev.vality.anapi.v2.model.*;
import dev.vality.anapi.v2.model.OffsetAmount;
import dev.vality.anapi.v2.model.OffsetCount;
import dev.vality.anapi.v2.model.SplitUnit;
import dev.vality.anapi.v2.model.SubError;
import dev.vality.damsel.analytics.*;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsServiceSrv.Iface analyticsClient;

    public GetPaymentsToolDistribution200Response getPaymentsToolDistribution(FilterRequest filterRequest) {
        try {
            var distribution = analyticsClient.getPaymentsToolDistribution(filterRequest);
            return new GetPaymentsToolDistribution200Response()
                    .result(distribution.getPaymentToolsDistributions().stream()
                            .map(o -> new PaymentsToolDistributionResult()
                                    .name(o.getName())
                                    .percents(o.getPercents()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsToolDistribution", e);
        }
    }

    public GetPaymentsAmount200Response getPaymentsAmount(FilterRequest filterRequest) {
        try {
            var paymentsAmount = analyticsClient.getPaymentsAmount(filterRequest);
            return new GetPaymentsAmount200Response()
                    .result(paymentsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsAmount", e);
        }
    }

    public GetPaymentsAmount200Response getCreditingsAmount(FilterRequest filterRequest) {
        try {
            var creditingsAmount = analyticsClient.getCreditingsAmount(filterRequest);
            return new GetPaymentsAmount200Response()
                    .result(creditingsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getCreditingsAmount", e);
        }
    }

    public GetPaymentsAmount200Response getAveragePayment(FilterRequest filterRequest) {
        try {
            var averagePayment = analyticsClient.getAveragePayment(filterRequest);
            return new GetPaymentsAmount200Response()
                    .result(averagePayment.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getAveragePayment", e);
        }
    }

    public GetPaymentsCount200Response getPaymentsCount(FilterRequest filterRequest) {
        try {
            var paymentsCount = analyticsClient.getPaymentsCount(filterRequest);
            return new GetPaymentsCount200Response()
                    .result(paymentsCount.getGroupsCount().stream()
                            .map(o -> new CountResult()
                                    .count(o.getCount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsCount", e);
        }
    }

    public GetPaymentsErrorDistribution200Response getPaymentsErrorDistribution(FilterRequest filterRequest) {
        try {
            var distribution = analyticsClient.getPaymentsErrorDistribution(filterRequest);
            return new GetPaymentsErrorDistribution200Response()
                    .result(distribution.getErrorDistributions().stream()
                            .map(o -> new PaymentsErrorsDistributionResult()
                                    .error(o.getName())
                                    .percents(o.getPercents()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsErrorDistribution", e);
        }
    }

    public GetPaymentsSubErrorDistribution200Response getPaymentsSubErrorDistribution(FilterRequest filterRequest) {
        try {
            var distribution = analyticsClient.getPaymentsSubErrorDistribution(filterRequest);
            return new GetPaymentsSubErrorDistribution200Response()
                    .result(distribution.getErrorDistributions().stream()
                            .map(o -> new PaymentsSubErrorsDistributionResult()
                                    .error(getSubError(o.getError()))
                                    .percents(o.getPercents()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsSubErrorDistribution", e);
        }
    }

    public GetPaymentsSplitAmount200Response getPaymentsSplitAmount(SplitFilterRequest splitFilterRequest) {
        try {
            var paymentsSplitAmount = analyticsClient.getPaymentsSplitAmount(splitFilterRequest);
            return new GetPaymentsSplitAmount200Response()
                    .result(paymentsSplitAmount.getGroupedCurrencyAmounts().stream()
                            .map(o -> createSplitAmountResult(o, paymentsSplitAmount.getResultSplitUnit()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsSplitAmount", e);
        }
    }

    public GetPaymentsSplitCount200Response getPaymentsSplitCount(SplitFilterRequest splitFilterRequest) {
        try {
            var paymentsSplitCount = analyticsClient.getPaymentsSplitCount(splitFilterRequest);
            return new GetPaymentsSplitCount200Response()
                    .result(paymentsSplitCount.getPaymentToolsDestrobutions().stream()
                            .map(o -> createSplitCountResult(o, paymentsSplitCount.getResultSplitUnit()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getPaymentsSplitCount", e);
        }
    }

    public GetPaymentsAmount200Response getRefundsAmount(FilterRequest filterRequest) {
        try {
            var refundsAmount = analyticsClient.getRefundsAmount(filterRequest);
            return new GetPaymentsAmount200Response()
                    .result(refundsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getRefundsAmount", e);
        }
    }

    public GetPaymentsAmount200Response getCurrentBalances(MerchantFilter merchantFilter) {
        try {
            var refundsAmount = analyticsClient.getCurrentBalances(merchantFilter);
            return new GetPaymentsAmount200Response()
                    .result(refundsAmount.getGroupsAmount().stream()
                            .map(o -> new AmountResult()
                                    .amount(o.getAmount())
                                    .currency(o.getCurrency()))
                            .collect(Collectors.toList()));
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getCurrentBalances", e);
        }
    }

    public GetCurrentShopBalances200Response getCurrentShopBalances(MerchantFilter merchantFilter) {
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
            return new GetCurrentShopBalances200Response()
                    .result(shopAmountResults);
        } catch (TException e) {
            throw new AnalytycsException("Error while call analyticsClient.getCurrentShopBalances", e);
        }
    }

    private SubError getSubError(dev.vality.damsel.analytics.SubError o) {
        return new SubError()
                .code(o.getCode())
                .subError(o.getSubError() != null ? getSubError(o.getSubError()) : null);
    }

    private SplitAmountResult createSplitAmountResult(GroupedCurrencyOffsetAmount groupedCurrencyOffsetAmount,
                                                      dev.vality.damsel.analytics.SplitUnit splitUnit) {
        var splitAmountResult = new SplitAmountResult();
        splitAmountResult.setSplitUnit(SplitUnit.valueOf(splitUnit.name()));
        splitAmountResult.setCurrency(groupedCurrencyOffsetAmount.getCurrency());
        splitAmountResult.setOffsetAmounts(groupedCurrencyOffsetAmount.getOffsetAmounts().stream()
                .map(this::createOffsetAmount)
                .collect(Collectors.toList()));
        return splitAmountResult;
    }

    private SplitCountResult createSplitCountResult(GroupedCurrencyOffsetCount groupedCurrencyOffsetCount,
                                                    dev.vality.damsel.analytics.SplitUnit unit) {
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

    private OffsetAmount createOffsetAmount(dev.vality.damsel.analytics.OffsetAmount offsetAmount) {
        var result = new OffsetAmount();
        result.setAmount(offsetAmount.getAmount());
        result.setOffset(offsetAmount.getOffset());
        return result;
    }

    private OffsetCount createOffsetCount(dev.vality.damsel.analytics.OffsetCount offsetCount) {
        var offsetCountResult = new OffsetCount();
        offsetCountResult.setCount(offsetCount.getCount());
        offsetCountResult.setOffset(offsetCount.getOffset());
        return offsetCountResult;
    }
}
