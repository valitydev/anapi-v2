package dev.vality.anapi.v2.exception;

public class TokenKeeperException extends RuntimeException {

    public TokenKeeperException(String s) {
        super(s);
    }

    public TokenKeeperException(String message, Throwable cause) {
        super(message, cause);
    }
}
