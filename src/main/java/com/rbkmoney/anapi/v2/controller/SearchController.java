package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.converter.search.request.*;
import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.service.SearchService;
import com.rbkmoney.magista.*;
import com.rbkmoney.openapi.anapi_v2.api.*;
import com.rbkmoney.openapi.anapi_v2.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.rbkmoney.anapi.v2.util.CommonUtil.getRequestDeadlineMillis;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SearchController implements PaymentsApi, ChargebacksApi, InvoicesApi, PayoutsApi, RefundsApi {

    private final SearchService searchService;
    private final ParamsToPaymentSearchQueryConverter paymentSearchConverter;
    private final ParamsToChargebackSearchQueryConverter chargebackSearchConverter;
    private final ParamsToInvoiceSearchQueryConverter invoiceSearchConverter;
    private final ParamsToPayoutSearchQueryConverter payoutSearchConverter;
    private final ParamsToRefundSearchQueryConverter refundSearchConverter;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return PaymentsApi.super.getRequest();
    }

    @Override
    public ResponseEntity<InlineResponse20010> searchPayments(String xrequestID,
                                                              @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                              @NotNull @Valid OffsetDateTime fromTime,
                                                              @NotNull @Valid OffsetDateTime toTime,
                                                              @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                              String xrequestDeadline,
                                                              @Size(min = 1, max = 40) @Valid String shopID,
                                                              @Valid List<String> shopIDs,
                                                              @Valid String paymentInstitutionRealm,
                                                              @Valid List<String> invoiceIDs,
                                                              @Valid String paymentStatus, @Valid String paymentFlow,
                                                              @Valid String paymentMethod,
                                                              @Valid String paymentTerminalProvider,
                                                              @Size(min = 1, max = 40) @Valid String invoiceID,
                                                              @Size(min = 1, max = 40) @Valid String paymentID,
                                                              @Size(min = 1, max = 40) @Valid String externalID,
                                                              @Size(max = 100) @Email @Valid String payerEmail,
                                                              @Size(max = 45) @Valid String payerIP,
                                                              @Size(max = 1000) @Valid String payerFingerprint,
                                                              @Size(min = 1, max = 40) @Valid String customerID,
                                                              @Pattern(regexp = "^\\d{6}$") @Valid String first6,
                                                              @Pattern(regexp = "^\\d{4}$") @Valid String last4,
                                                              @Pattern(regexp = "^[a-zA-Z0-9]{12}$") @Valid String rrn,
                                                              @Size(min = 1, max = 40) @Valid String approvalCode,
                                                              @Valid BankCardTokenProvider bankCardTokenProvider,
                                                              @Valid BankCardPaymentSystem bankCardPaymentSystem,
                                                              @Min(1L) @Valid Long paymentAmountFrom,
                                                              @Min(1L) @Valid Long paymentAmountTo,
                                                              @Valid List<String> excludedShops,
                                                              @Valid String continuationToken) {
        PaymentSearchQuery query = paymentSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopID,
                shopIDs,
                paymentInstitutionRealm,
                invoiceIDs,
                paymentStatus, paymentFlow,
                paymentMethod,
                paymentTerminalProvider,
                invoiceID,
                paymentID,
                externalID,
                payerEmail,
                payerIP,
                payerFingerprint,
                customerID,
                first6,
                last4,
                rrn,
                approvalCode,
                bankCardTokenProvider,
                bankCardPaymentSystem,
                paymentAmountFrom,
                paymentAmountTo,
                excludedShops,
                continuationToken);
        try {
            InlineResponse20010 response;
            if (xrequestDeadline != null) {
                response = searchService
                        .findPayments(query)
                        .get(getRequestDeadlineMillis(xrequestDeadline), TimeUnit.MILLISECONDS);
            } else {
                response = searchService.findPayments(query).get();
            }
            return ResponseEntity.ok(response);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @GetMapping(
            value = "/chargebacks",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse2008> searchChargebacks(String xrequestID,
                                                                @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                                @NotNull @Valid OffsetDateTime fromTime,
                                                                @NotNull @Valid OffsetDateTime toTime,
                                                                @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                                String xrequestDeadline,
                                                                @Size(min = 1, max = 40) @Valid String shopID,
                                                                @Valid List<String> shopIDs,
                                                                @Valid String paymentInstitutionRealm,
                                                                @Min(0L) @Valid Integer offset,
                                                                @Size(min = 1, max = 40) @Valid String invoiceID,
                                                                @Size(min = 1, max = 40) @Valid String paymentID,
                                                                @Size(min = 1, max = 40) @Valid String chargebackID,
                                                                @Valid List<String> chargebackStatuses,
                                                                @Valid List<String> chargebackStages,
                                                                @Valid List<String> chargebackCategories,
                                                                @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xrequestID, xrequestDeadline, offset
        ChargebackSearchQuery query = chargebackSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopID,
                shopIDs,
                paymentInstitutionRealm,
                offset,
                invoiceID,
                paymentID,
                chargebackID,
                chargebackStatuses,
                chargebackStages,
                chargebackCategories,
                continuationToken);
        try {
            InlineResponse2008 response;
            if (xrequestDeadline != null) {
                response = searchService
                        .findChargebacks(query)
                        .get(getRequestDeadlineMillis(xrequestDeadline), TimeUnit.MILLISECONDS);
            } else {
                response = searchService.findChargebacks(query).get();
            }
            return ResponseEntity.ok(response);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @GetMapping(
            value = "/invoices",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse2009> searchInvoices(String xrequestID,
                                                             @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                             @NotNull @Valid OffsetDateTime fromTime,
                                                             @NotNull @Valid OffsetDateTime toTime,
                                                             @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                             String xrequestDeadline,
                                                             @Size(min = 1, max = 40) @Valid String shopID,
                                                             @Valid List<String> shopIDs,
                                                             @Valid String paymentInstitutionRealm,
                                                             @Valid List<String> invoiceIDs,
                                                             @Valid String invoiceStatus,
                                                             @Size(min = 1, max = 40) @Valid String invoiceID,
                                                             @Size(min = 1, max = 40) @Valid String externalID,
                                                             @Min(1L) @Valid Long invoiceAmountFrom,
                                                             @Min(1L) @Valid Long invoiceAmountTo,
                                                             @Valid List<String> excludedShops,
                                                             @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xrequestID, xrequestDeadline, excludedShops
        InvoiceSearchQuery query = invoiceSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopID,
                shopIDs,
                paymentInstitutionRealm,
                invoiceIDs,
                invoiceStatus,
                invoiceID,
                externalID,
                invoiceAmountFrom,
                invoiceAmountTo,
                excludedShops,
                continuationToken);

        try {
            InlineResponse2009 response;
            if (xrequestDeadline != null) {
                response = searchService
                        .findInvoices(query)
                        .get(getRequestDeadlineMillis(xrequestDeadline), TimeUnit.MILLISECONDS);
            } else {
                response = searchService.findInvoices(query).get();
            }
            return ResponseEntity.ok(response);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @GetMapping(
            value = "/payouts",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse20011> searchPayouts(String xrequestID,
                                                             @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                             @NotNull @Valid OffsetDateTime fromTime,
                                                             @NotNull @Valid OffsetDateTime toTime,
                                                             @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                             String xrequestDeadline,
                                                             @Size(min = 1, max = 40) @Valid String shopID,
                                                             @Valid List<String> shopIDs,
                                                             @Valid String paymentInstitutionRealm,
                                                             @Min(0L) @Valid Integer offset,
                                                             @Size(min = 1, max = 40) @Valid String payoutID,
                                                             @Valid String payoutToolType,
                                                             @Valid List<String> excludedShops,
                                                             @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xrequestID, xrequestDeadline, excludedShops,
        //offset + setStatuses
        PayoutSearchQuery query = payoutSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopID,
                shopIDs,
                paymentInstitutionRealm,
                offset,
                payoutID,
                payoutToolType,
                excludedShops,
                continuationToken);

        try {
            InlineResponse20011 response;
            if (xrequestDeadline != null) {
                response = searchService
                        .findPayouts(query)
                        .get(getRequestDeadlineMillis(xrequestDeadline), TimeUnit.MILLISECONDS);
            } else {
                response = searchService.findPayouts(query).get();
            }
            return ResponseEntity.ok(response);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @GetMapping(
            value = "/refunds",
            produces = {"application/json; charset=utf-8"}
    )
    @Override
    public ResponseEntity<InlineResponse20012> searchRefunds(String xrequestID,
                                                             @NotNull @Size(min = 1, max = 40) @Valid String partyID,
                                                             @NotNull @Valid OffsetDateTime fromTime,
                                                             @NotNull @Valid OffsetDateTime toTime,
                                                             @NotNull @Min(1L) @Max(1000L) @Valid Integer limit,
                                                             String xrequestDeadline,
                                                             @Size(min = 1, max = 40) @Valid String shopID,
                                                             @Valid List<String> shopIDs,
                                                             @Valid String paymentInstitutionRealm,
                                                             @Min(0L) @Valid Integer offset,
                                                             @Valid List<String> invoiceIDs,
                                                             @Size(min = 1, max = 40) @Valid String invoiceID,
                                                             @Size(min = 1, max = 40) @Valid String paymentID,
                                                             @Size(min = 1, max = 40) @Valid String refundID,
                                                             @Size(min = 1, max = 40) @Valid String externalID,
                                                             @Valid String refundStatus,
                                                             @Valid List<String> excludedShops,
                                                             @Valid String continuationToken) {
        //TODO: clarify mapping for paymentInstitutionRealm, xrequestID, xrequestDeadline, excludedShops, offset
        RefundSearchQuery query = refundSearchConverter.convert(partyID,
                fromTime,
                toTime,
                limit,
                shopID,
                shopIDs,
                paymentInstitutionRealm,
                offset,
                invoiceIDs,
                invoiceID,
                paymentID,
                refundID,
                externalID,
                refundStatus,
                excludedShops,
                continuationToken);

        try {
            InlineResponse20012 response;
            if (xrequestDeadline != null) {
                response = searchService
                        .findRefunds(query)
                        .get(getRequestDeadlineMillis(xrequestDeadline), TimeUnit.MILLISECONDS);
            } else {
                response = searchService.findRefunds(query).get();
            }
            return ResponseEntity.ok(response);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (TimeoutException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
        }
    }

    @ExceptionHandler({ConstraintViolationException.class, BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<DefaultLogicError> handleConstraintViolation(Exception ex) {
        DefaultLogicError error;
        if (ex instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations =
                    ((ConstraintViolationException) ex).getConstraintViolations();
            String errorMessage =
                    constraintViolations.stream()
                            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                            .collect(Collectors.joining(", "));
            error = new DefaultLogicError()
                    .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                    .message(errorMessage);
        } else if (ex instanceof BadRequestException) {
            error = new DefaultLogicError()
                    .code(((BadRequestException) ex).getErrorCode())
                    .message(ex.getMessage());
        } else {
            error = new DefaultLogicError()
                    .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                    .message(ex.getMessage());
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
