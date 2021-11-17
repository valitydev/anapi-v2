package com.rbkmoney.anapi.v2.exception;

public class MagistaException extends AnapiV25xxException {

    public MagistaException(String s) {
        super(s);
    }

    public MagistaException(String message, Throwable cause) {
        super(message, cause);
    }
}
