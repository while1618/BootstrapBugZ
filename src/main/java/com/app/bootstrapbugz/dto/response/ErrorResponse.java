package com.app.bootstrapbugz.dto.response;

import com.app.bootstrapbugz.constant.ErrorDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private List<Error> errors;

    public ErrorResponse(HttpStatus status) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.errors = new ArrayList<>();
    }

    public ErrorResponse(HttpStatus status, ErrorDomain domain, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.errors = new ArrayList<>();
        this.errors.add(new Error(domain.getValue(), message));
    }

    public void addError(String domain, String message) {
        this.errors.add(new Error(domain, message));
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext)
                        -> new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))))
                .create();

        return gson.toJson(this);
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

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
