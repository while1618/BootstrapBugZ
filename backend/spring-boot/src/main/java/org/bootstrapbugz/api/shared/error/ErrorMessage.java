package org.bootstrapbugz.api.shared.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
  private final LocalDateTime timestamp;
  private final int status;
  private final String error;
  private final List<Detail> details;

  public ErrorMessage(HttpStatus status) {
    this.timestamp = LocalDateTime.now();
    this.status = status.value();
    this.error = status.getReasonPhrase();
    this.details = new ArrayList<>();
  }

  public void addDetails(String message) {
    this.details.add(new Detail(message));
  }

  public void addDetails(String field, String message) {
    this.details.add(new Detail(field, message));
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

  @Getter
  @Setter
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private static final class Detail {
    private String field;
    private String message;

    private Detail(String message) {
      this.message = message;
    }

    private Detail(String field, String message) {
      this.field = field;
      this.message = message;
    }

    @Override
    public String toString() {
      return new Gson().toJson(this);
    }
  }
}
