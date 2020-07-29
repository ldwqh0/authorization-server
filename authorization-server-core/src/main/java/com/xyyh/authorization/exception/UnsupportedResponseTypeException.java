package com.xyyh.authorization.exception;

public class UnsupportedResponseTypeException extends RuntimeException {
    public UnsupportedResponseTypeException() {
        super();
    }

    public UnsupportedResponseTypeException(String message) {
        super(message);
    }
}
