package com.xyyh.authorization.exception;

public class NoSuchClientException extends RuntimeException {

    private static final long serialVersionUID = 3688734189269714270L;

    public NoSuchClientException(String message) {
        super(message);
    }

    public NoSuchClientException() {
        super();
    }

}
