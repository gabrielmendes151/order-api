package com.orderapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicatedOrderException extends RuntimeException {

    public DuplicatedOrderException(String message) {
        super(message);
    }
}
