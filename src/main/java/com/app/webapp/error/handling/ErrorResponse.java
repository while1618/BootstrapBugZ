package com.app.webapp.error.handling;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<Error> errors;

    public ErrorResponse(HttpStatus status) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.errors = new ArrayList<>();
    }

    public ErrorResponse(HttpStatus status, String domain, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.errors = new ArrayList<>();
        this.errors.add(new Error(domain, message));
    }

    public void addError(String domain, String message) {
        this.errors.add(new Error(domain, message));
    }

    @Getter
    @Setter
    private static final class Error {
        private final String domain;
        private final String message;

        private Error(String domain, String message) {
            this.domain = domain;
            this.message = message;
        }
    }
}
