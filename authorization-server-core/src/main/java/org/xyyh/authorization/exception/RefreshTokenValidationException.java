package org.xyyh.authorization.exception;

public class RefreshTokenValidationException extends Exception {
    private static final long serialVersionUID = -4599666623584805810L;

    public RefreshTokenValidationException() {
        super();
    }

    public RefreshTokenValidationException(String message) {
        super(message);
    }
}
