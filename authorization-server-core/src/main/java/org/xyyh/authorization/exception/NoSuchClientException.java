package org.xyyh.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoSuchClientException extends Exception {

    private static final long serialVersionUID = 3688734189269714270L;

    public NoSuchClientException(String message) {
        super(message);
    }

    public NoSuchClientException() {
        super();
    }

}
