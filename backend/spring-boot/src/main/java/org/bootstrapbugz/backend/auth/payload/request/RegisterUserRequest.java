package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.bootstrapbugz.backend.shared.constants.Regex;
import org.bootstrapbugz.backend.shared.validator.FieldMatch;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{user.passwordsDoNotMatch}")
public record RegisterUserRequest(
    @NotBlank(message = "{user.usernameRequired}")
        @Pattern(regexp = Regex.USERNAME, message = "{user.usernameInvalid}")
        String username,
    @NotBlank(message = "{user.emailRequired}")
        @Email(message = "{user.emailInvalid}", regexp = Regex.EMAIL)
        String email,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String password,
    @NotBlank(message = "{user.passwordRequired}")
        @Pattern(regexp = Regex.PASSWORD, message = "{user.passwordInvalid}")
        String confirmPassword) {}
