package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {
    private String domain = "employee.notFound";

    public EmployeeNotFoundException() {
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
