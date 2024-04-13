package org.bootstrapbugz.backend.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.bootstrapbugz.backend.shared.constants.Regex;
import org.bootstrapbugz.backend.shared.validator.FieldMatch;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
public record RegisterUserRequest(
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
        String confirmPassword) {}
