package dev.vality.anapi.v2.exception;

public class ReporterException extends AnapiV25xxException {

    public ReporterException(String s) {
        super(s);
    }

    public ReporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
