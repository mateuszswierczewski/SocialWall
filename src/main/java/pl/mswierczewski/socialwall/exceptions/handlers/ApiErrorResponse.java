package pl.mswierczewski.socialwall.exceptions.handlers;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ApiErrorResponse {

    private String error;
    private String message;
    private HttpStatus httpStatus;

    private ErrorCause cause;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private final ZonedDateTime timestamp;

    public ApiErrorResponse(String error, String message, HttpStatus httpStatus) {
        this.error = error;
        this.message = message;
        this.httpStatus = httpStatus;
        timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public ErrorCause getCause() {
        return cause;
    }

    public void setCause(ErrorCause cause) {
        this.cause = cause;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }


}
