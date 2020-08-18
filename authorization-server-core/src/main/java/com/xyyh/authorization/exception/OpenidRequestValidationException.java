package com.xyyh.authorization.exception;

import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;

public class OpenidRequestValidationException extends RuntimeException {

    private static final long serialVersionUID = -5266787893793188272L;
    /**
     * 发生错误的请求
     */
    private OpenidAuthorizationRequest request;

    public OpenidRequestValidationException(OpenidAuthorizationRequest request, String message) {
        super(message);
        this.request = request;
    }

    public OpenidRequestValidationException(OpenidAuthorizationRequest request, Throwable ex) {
        super(ex);
        this.request = request;
    }

    public OpenidRequestValidationException(OpenidAuthorizationRequest request) {
        this.request = request;
    }

    public OpenidAuthorizationRequest getRequest() {
        return request;
    }
}