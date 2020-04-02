package com.app.webapp.error.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException() {
    }

    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
