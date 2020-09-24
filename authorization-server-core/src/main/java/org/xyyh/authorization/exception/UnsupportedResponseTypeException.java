package org.xyyh.authorization.exception;

public class UnsupportedResponseTypeException extends Exception {
    private static final long serialVersionUID = -496818695443849954L;

    public UnsupportedResponseTypeException() {
        super();
    }

    public UnsupportedResponseTypeException(String message) {
        super(message);
    }
}
