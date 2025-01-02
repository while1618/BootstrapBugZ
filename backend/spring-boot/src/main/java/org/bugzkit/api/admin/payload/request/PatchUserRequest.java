package org.bugzkit.api.admin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Builder;
import org.bugzkit.api.shared.constants.Regex;
import org.bugzkit.api.shared.validator.FieldMatch;
import org.bugzkit.api.user.model.Role.RoleName;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{user.passwordsDoNotMatch}")
public record PatchUserRequest(
    @Pattern(regexp = Regex.USERNAME, message = "{user.usernameInvalid}") String username,
    @Email(message = "{user.emailInvalid}", regexp = Regex.EMAIL) String email,
    @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}") String password,
    @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}") String confirmPassword,
    Boolean active,
    Boolean lock,
    Set<RoleName> roleNames) {}
