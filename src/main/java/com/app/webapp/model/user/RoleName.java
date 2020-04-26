package com.app.webapp.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RoleName {
    @JsonProperty("user")
    ROLE_USER,
    @JsonProperty("admin")
    ROLE_ADMIN
}
