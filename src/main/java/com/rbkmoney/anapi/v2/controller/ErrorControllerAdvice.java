package com.rbkmoney.anapi.v2.controller;

import com.rbkmoney.anapi.v2.exception.AuthorizationException;
import com.rbkmoney.anapi.v2.exception.BadRequestException;
import com.rbkmoney.anapi.v2.exception.DeadlineException;
import com.rbkmoney.anapi.v2.exception.NotFoundException;
import com.rbkmoney.anapi.v2.model.DefaultLogicError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice {

    // ----------------- 4xx -----------------------------------------------------

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBadRequestException(BadRequestException e) {
        log.warn("<- Res [400]: Not valid", e);
        return e.getResponse();
    }

    @ExceptionHandler({DeadlineException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleDeadlineException(DeadlineException e) {
        log.warn("<- Res [400]: Not valid", e);
        return new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDDEADLINE)
                .message(e.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("<- Res [400]: Not valid", e);
        var errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                .message(errorMessage);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("<- Res [400]: MethodArgument not valid", e);
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

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleAccessDeniedException(AccessDeniedException e) {
        log.warn("<- Res [403]: Request denied access", e);
    }

    @ExceptionHandler({AuthorizationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleAccessDeniedException(AuthorizationException e) {
        log.warn("<- Res [403]: Request denied access", e);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleFileNotFoundException(NotFoundException e) {
        log.warn("<- Res [404]: Not found", e);
        return e.getResponse();
    }

    @ExceptionHandler({HttpMediaTypeNotAcceptableException.class})
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public void handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e) {
        log.warn("<- Res [406]: MediaType not acceptable", e);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<?> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e, WebRequest request) {
        log.warn("<- Res [415]: MediaType not supported", e);
        return status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .headers(httpHeaders(e))
                .build();
    }

    // ----------------- 5xx -----------------------------------------------------

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleHttpClientErrorException(HttpClientErrorException e) {
        log.error("<- Res [500]: Error with using inner http client, code={}, body={}",
                e.getStatusCode(), e.getResponseBodyAsString(), e);
    }

    @ExceptionHandler(HttpTimeoutException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleHttpTimeoutException(HttpTimeoutException e) {
        log.error("<- Res [500]: Timeout with using inner http client", e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e) {
        log.error("<- Res [500]: Unrecognized inner error", e);
    }

    private HttpHeaders httpHeaders(HttpMediaTypeNotSupportedException e) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = e.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }
        return headers;
    }
}
