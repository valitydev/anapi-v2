package com.rbkmoney.anapi.v2.exception;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String s) {
        super(s);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
