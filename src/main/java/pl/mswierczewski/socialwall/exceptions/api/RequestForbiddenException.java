package pl.mswierczewski.socialwall.exceptions.api;

import org.springframework.http.HttpStatus;

public class RequestForbiddenException extends ApiException {

    private static final HttpStatus status = HttpStatus.FORBIDDEN;

    public RequestForbiddenException(String msg) {
        super(msg, status);
    }
}
