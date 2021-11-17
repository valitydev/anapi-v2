package com.rbkmoney.anapi.v2.exception;

class AnapiV25xxException extends RuntimeException {

    public AnapiV25xxException() {
    }

    public AnapiV25xxException(String message) {
        super(message);
    }

    public AnapiV25xxException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnapiV25xxException(Throwable cause) {
        super(cause);
    }
}
