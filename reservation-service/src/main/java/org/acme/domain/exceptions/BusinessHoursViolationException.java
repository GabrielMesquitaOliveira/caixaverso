package org.acme.domain.exceptions;

public class BusinessHoursViolationException extends RuntimeException {
    public BusinessHoursViolationException(String message) {
        super(message);
    }
}
