package com.rbkmoney.anapi.v2.exception;

import com.rbkmoney.anapi.v2.model.DefaultLogicError;
import lombok.Getter;

@Getter
public class BadRequestException extends AnapiV24xxException {

    private final Object response;

    public BadRequestException(String message, Throwable cause, Object response) {
        super(message, cause);
        this.response = response;
    }

    public BadRequestException(String message, Object response) {
        super(message);
        this.response = response;
    }

    public BadRequestException(String message) {
        super(message);
        this.response = new DefaultLogicError()
                .code(DefaultLogicError.CodeEnum.INVALIDREQUEST)
                .message(message);
    }
}