package pl.mswierczewski.socialwall.exceptions.api;

import org.springframework.http.HttpStatus;
import pl.mswierczewski.socialwall.exceptions.api.ApiException;

public class EntityNotFoundException extends ApiException {

    private static final HttpStatus status = HttpStatus.NOT_FOUND;

    public EntityNotFoundException(String msg) {
        super(msg, status);
    }

    public EntityNotFoundException(String msg, Throwable cause) {
        super(msg, cause, status);
    }
}
