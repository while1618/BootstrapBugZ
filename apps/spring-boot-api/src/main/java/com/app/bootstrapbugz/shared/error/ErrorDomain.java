package com.app.bootstrapbugz.shared.error;

public enum ErrorDomain {
  GLOBAL("global"),
  USER("user"),
  AUTH("auth");

  private final String value;

  ErrorDomain(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
