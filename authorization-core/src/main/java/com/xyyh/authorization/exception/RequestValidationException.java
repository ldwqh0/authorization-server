package com.xyyh.authorization.exception;

public class RequestValidationException extends RuntimeException {

    private static final long serialVersionUID = -3157465183026609933L;

    public RequestValidationException() {
        super();
    }

    public RequestValidationException(String message) {
        super(message);
    }

}
