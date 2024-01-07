package org.bootstrapbugz.api.admin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Builder;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;
import org.bootstrapbugz.api.user.model.Role.RoleName;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
public record UserRequest(
    @NotBlank(message = "{firstName.required}")
        @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
        String firstName,
    @NotBlank(message = "{lastName.required}")
        @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
        String lastName,
    @NotBlank(message = "{username.required}")
        @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
        String username,
    @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        String email,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String password,
    @NotBlank(message = "{password.required}")
        @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmPassword,
    @NotNull(message = "{user.active.required}") Boolean active,
    @NotNull(message = "{user.lock.required}") Boolean lock,
    @NotEmpty(message = "{roles.empty}") Set<RoleName> roleNames) {}
