package com.rbkmoney.anapi.v2.exception;

public class AuthorizationException extends AnapiV24xxException {

    public AuthorizationException(String s) {
        super(s);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
