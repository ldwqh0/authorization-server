package org.xyyh.authorization.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidScopeException extends RuntimeException {

    private static final long serialVersionUID = 6683655619233752577L;

}
