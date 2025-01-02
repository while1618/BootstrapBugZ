package org.bugzkit.api.shared.error;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorMessage {
  private final LocalDateTime timestamp;
  private final int status;
  private final String error;
  private final List<String> codes;

  public ErrorMessage(HttpStatus status) {
    this.timestamp = LocalDateTime.now();
    this.status = status.value();
    this.error = status.getReasonPhrase();
    this.codes = new ArrayList<>();
  }

  public void addCode(String code) {
    this.codes.add(code);
  }

  @Override
  public String toString() {
    return new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(
            LocalDateTime.class,
            (JsonSerializer<LocalDateTime>)
                (localDateTime, type, jsonSerializationContext) ->
                    new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME)))
        .create()
        .toJson(this);
  }
}
