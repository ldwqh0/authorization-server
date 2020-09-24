package org.xyyh.authorization.exception;

public class UnRegisteredRedirectUriException extends Exception {
    public UnRegisteredRedirectUriException() {
        super();
    }

    public UnRegisteredRedirectUriException(String message) {
        super(message);
    }
}
