package com.amigoscode.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.NOT_FOUND)
public class RessourceNotFoundException extends RuntimeException {

    public RessourceNotFoundException(String message){
        super(message);
    }
}
