package com.rbkmoney.anapi.v2.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends AnapiV24xxException {

    private final Object response;

    public NotFoundException(String message, Throwable cause, Object response) {
        super(message, cause);
        this.response = response;
    }
}
