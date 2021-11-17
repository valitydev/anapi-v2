package com.rbkmoney.anapi.v2.exception;

public class BouncerException extends AnapiV25xxException {

    public BouncerException(String s) {
        super(s);
    }

    public BouncerException(String message, Throwable cause) {
        super(message, cause);
    }
}
