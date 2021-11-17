package com.rbkmoney.anapi.v2.exception;

class AnapiV24xxException extends RuntimeException {

    public AnapiV24xxException(String message) {
        super(message);
    }

    public AnapiV24xxException(String message, Throwable cause) {
        super(message, cause);
    }
}
