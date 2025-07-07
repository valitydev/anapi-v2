package dev.vality.anapi.v2.exception;

public class DominantException extends AnapiV25xxException {

    public DominantException(String s) {
        super(s);
    }

    public DominantException(String message, Throwable cause) {
        super(message, cause);
    }
}
