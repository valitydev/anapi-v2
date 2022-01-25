package dev.vality.anapi.v2.exception;

public class DeadlineException extends AnapiV24xxException {

    public DeadlineException(String message) {
        super(message);
    }

    public DeadlineException(String message, Throwable cause) {
        super(message, cause);
    }
}
