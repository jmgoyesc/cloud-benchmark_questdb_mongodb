package com.github.jmgoyesc.control.adapters.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Juan Manuel Goyes Coral
 */

//TODO: add handler exception for invlaid enum in database

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ProblemDetail onException(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

}
