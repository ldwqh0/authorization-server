package org.xyyh.authorization.exception;

public class RefreshTokenValidationException extends Exception {
    public RefreshTokenValidationException() {
        super();
    }

    public RefreshTokenValidationException(String message) {
        super(message);
    }
}
