package pl.mswierczewski.socialwall.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.mswierczewski.socialwall.exceptions.SocialWallBadCredentialsException;
import pl.mswierczewski.socialwall.exceptions.SocialWallUserNotFoundException;
import pl.mswierczewski.socialwall.exceptions.UserAlreadyExistException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {UserAlreadyExistException.class})
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
        ErrorResponse response = getResponse(e.getMessage(), HttpStatus.CONFLICT);

        if (e.isUsernameExists()){
            response.addAdditionalData("username", "Username already exists!");
        }

        if (e.isEmailExists()){
            response.addAdditionalData("email", "Email already exists!");
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse response = getResponse(e.getMessage(), HttpStatus.BAD_REQUEST);

        e.getBindingResult().getAllErrors().forEach(
                error -> response.addAdditionalData(((FieldError) error).getField(), error.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(value = {SocialWallBadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleSocialWallBadCredentialsException(SocialWallBadCredentialsException e) {
        ErrorResponse response = getResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);

        response.addAdditionalData(e.getWrongField(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(value = {SocialWallUserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleSocialWallUserNotFoundException(SocialWallUserNotFoundException e) {
        ErrorResponse response = getResponse(e.getMessage(), HttpStatus.NOT_FOUND);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    private ErrorResponse getResponse(String message, HttpStatus httpStatus) {
        return new ErrorResponse(
                message,
                httpStatus,
                ZonedDateTime.now(ZoneId.of("UTC"))
        );
    }
}
