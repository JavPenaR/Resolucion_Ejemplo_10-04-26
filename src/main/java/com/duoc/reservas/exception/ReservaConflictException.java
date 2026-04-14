package com.duoc.reservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservaConflictException extends RuntimeException {
    public ReservaConflictException(String message) {
        super(message);
    }
}
