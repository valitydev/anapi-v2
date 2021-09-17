package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.exception.DeadlineException;
import com.rbkmoney.openapi.anapi_v2.model.DefaultLogicError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice {

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("<- Res [400]: Not valid", e);
        Set<ConstraintViolation<?>> constraintViolations =
                e.getConstraintViolations();
        String errorMessage =
                constraintViolations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.joining(", "));
        return new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                .message(errorMessage);
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBadRequestException(BadRequestException e) {
        log.warn("<- Res [400]: Not valid", e);
        return new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                .message(e.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("<- Res [400]: Missing ServletRequestParameter", e);
        return new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                .message(e.getMessage());
    }

    @ExceptionHandler({DeadlineException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleDeadlineException(DeadlineException e) {
        log.warn("<- Res [400]: Not valid", e);
        return new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDDEADLINE)
                .message(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e) {
        log.error("<- Res [500]: Unrecognized inner error", e);
    }

}
