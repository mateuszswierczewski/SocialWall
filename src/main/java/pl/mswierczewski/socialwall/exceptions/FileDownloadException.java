package pl.mswierczewski.socialwall.exceptions;

import org.springframework.http.HttpStatus;

public class FileDownloadException extends RuntimeException {

    private final HttpStatus httpStatus;

    private FileDownloadException(String msg, HttpStatus httpStatus){
        super(msg);
        this.httpStatus = httpStatus;
    }

    private FileDownloadException(String msg, Throwable cause, HttpStatus httpStatus){
        super(msg, cause);
        this.httpStatus = httpStatus;
    }

    public static FileDownloadException downloadError(String msg, Throwable cause){
        return new FileDownloadException(msg, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
