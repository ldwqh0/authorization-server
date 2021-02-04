package org.xyyh.authorization.exception;

public class UnRegisteredRedirectUriException extends Exception {
    private static final long serialVersionUID = -5868037293836375838L;

    public UnRegisteredRedirectUriException() {
        super();
    }

    public UnRegisteredRedirectUriException(String message) {
        super(message);
    }
}
