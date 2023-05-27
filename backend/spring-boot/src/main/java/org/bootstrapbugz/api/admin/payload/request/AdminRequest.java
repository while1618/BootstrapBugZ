package org.bootstrapbugz.api.admin.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import org.bootstrapbugz.api.shared.constants.Regex;

public record AdminRequest(
    @NotEmpty(message = "{usernames.empty}")
        Set<@Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String> usernames) {}
