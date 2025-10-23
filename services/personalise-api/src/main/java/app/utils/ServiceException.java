package app.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends Exception {

    private final HttpStatus status;

    public ServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}