package com.app.webapp.error.exception;

import lombok.Getter;

@Getter
public class DepartmentNotFoundException extends RuntimeException {
    private String domain = "department.notFound";

    public DepartmentNotFoundException() {
    }

    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
