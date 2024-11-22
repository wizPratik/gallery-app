package com.gallery_app.core_service.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException() {
        super();
    }
    public InternalServerErrorException(final String message) {
        super(message);
    }
}
