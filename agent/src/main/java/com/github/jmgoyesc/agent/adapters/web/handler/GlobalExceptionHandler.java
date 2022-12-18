package com.github.jmgoyesc.agent.adapters.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Juan Manuel Goyes Coral
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ProblemDetail onException(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

}
