package pl.mswierczewski.socialwall.exceptions;

import org.springframework.http.HttpStatus;

public class FileUploadException extends RuntimeException {

    private final HttpStatus httpStatus;

    private FileUploadException(String msg, HttpStatus httpStatus){
        super(msg);
        this.httpStatus = httpStatus;
    }

    private FileUploadException(String msg, Throwable cause, HttpStatus httpStatus){
        super(msg, cause);
        this.httpStatus = httpStatus;
    }

    public static FileUploadException unsupportedMediaType(String msg){
        return new FileUploadException(msg, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    public static FileUploadException emptyFile(String msg){
        return new FileUploadException(msg, HttpStatus.CONFLICT);
    }

    public static FileUploadException saveError(String msg, Throwable cause){
        return new FileUploadException(msg, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
