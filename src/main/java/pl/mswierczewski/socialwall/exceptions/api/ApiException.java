package pl.mswierczewski.socialwall.exceptions.api;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException{

    private final HttpStatus status;

    public ApiException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public ApiException(String msg, Throwable cause, HttpStatus status) {
        super(msg, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
