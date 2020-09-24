package org.xyyh.authorization.exception;

public class PkceRequiredException extends Exception {
    public PkceRequiredException() {
        super();
    }

    public PkceRequiredException(String message) {
        super(message);
    }
}
