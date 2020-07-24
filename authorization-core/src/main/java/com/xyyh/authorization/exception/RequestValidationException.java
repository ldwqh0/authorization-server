package com.xyyh.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "asdgd")
public class RequestValidationException extends RuntimeException {

    private static final long serialVersionUID = -3157465183026609933L;

    public RequestValidationException() {
        super();
    }

    public RequestValidationException(String message) {
        super(message);
    }

}
