package org.bootstrapbugz.api.admin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Builder;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;
import org.bootstrapbugz.api.user.model.Role.RoleName;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public record PatchUserRequest(
    @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}") String firstName,
    @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}") String lastName,
    @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String username,
    @Email(message = "{email.invalid}", regexp = Regex.EMAIL) String email,
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String password,
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String confirmPassword,
    Boolean active,
    Boolean lock,
    Set<RoleName> roleNames) {}
