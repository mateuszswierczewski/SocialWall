package pl.mswierczewski.socialwall.exceptions.api;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException{

    private static final HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadRequestException(String msg) {
        super(msg, status);
    }

    public BadRequestException(String msg, Throwable cause) {
        super(msg, cause, status);
    }
}
