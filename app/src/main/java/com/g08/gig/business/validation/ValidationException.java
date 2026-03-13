package com.g08.gig.business.validation;



public class ValidationException extends RuntimeException {

    private String field;

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;

    }

    public String getField() {
        return field;
    }
}
