package org.xyyh.authorization.exception;

import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;

public class OpenidRequestValidationException extends Exception {

    private static final long serialVersionUID = -5266787893793188272L;
    /**
     * 发生错误的请求
     */
    private final OpenidAuthorizationRequest request;

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
