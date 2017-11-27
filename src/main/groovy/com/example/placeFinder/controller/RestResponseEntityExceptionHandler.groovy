package com.example.placeFinder.controller

import net.sf.json.JSONObject
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.CollectionUtils
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @SuppressWarnings("unchecked")
    private JSONObject createResponseBody(String message) {
        JSONObject body = new JSONObject()
        body.put("message", message)
        return body
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods()
        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods)
        }
        return handleExceptionInternal(ex, createResponseBody(ex.getMessage()), headers,
                status, request)
    }

    @ExceptionHandler([IllegalArgumentException.class, NullPointerException.class])
    ResponseEntity<Object> handleIllegalArgumentException(final Exception ex,
                                                                 final WebRequest request) {
        String message = ex.getMessage()
        return new ResponseEntity<>(createResponseBody(ex.getMessage()), new HttpHeaders(),
                HttpStatus.BAD_REQUEST)
    }

}
