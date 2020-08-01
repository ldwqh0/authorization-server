package com.xyyh.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 请求Token时错误信息
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TokenRequestValidationException extends RuntimeException {

    private static final long serialVersionUID = -3157465183026609933L;

    public TokenRequestValidationException() {
        super();
    }

    public TokenRequestValidationException(String message) {
        super(message);
    }

}
