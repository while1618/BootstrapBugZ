package org.bootstrapbugz.api.admin.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Builder;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.user.model.Role.RoleName;

@Builder
public record UpdateRoleRequest(
    @NotEmpty(message = "{usernames.empty}")
        Set<@Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String> usernames,
    @NotEmpty(message = "{roles.empty}") Set<RoleName> roleNames) {}
