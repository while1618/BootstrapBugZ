package org.bootstrapbugz.api.admin.payload.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import org.bootstrapbugz.api.user.model.Role.RoleName;

public record UpdateRoleRequest(@NotEmpty(message = "{roles.empty}") Set<RoleName> roleNames) {}
