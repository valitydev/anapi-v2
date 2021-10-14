package com.rbkmoney.anapi.v2.exception;

import com.rbkmoney.openapi.anapi_v2.model.DefaultLogicError;
import lombok.Getter;

public class BadRequestException extends IllegalArgumentException {

    @Getter
    private DefaultLogicError.CodeEnum errorCode = DefaultLogicError.CodeEnum.INVALIDREQUEST;

    public BadRequestException(String s) {
        super(s);
    }

    public BadRequestException(String s, DefaultLogicError.CodeEnum errorCode) {
        super(s);
        this.errorCode = errorCode;
    }
}