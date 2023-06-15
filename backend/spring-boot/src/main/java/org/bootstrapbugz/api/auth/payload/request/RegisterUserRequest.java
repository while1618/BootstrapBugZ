package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public record RegisterUserRequest(
    @NotBlank @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
        String firstName,
    @NotBlank @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
        String lastName,
    @NotBlank @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String username,
    @NotBlank @Email(message = "{email.invalid}", regexp = Regex.EMAIL) String email,
    @NotBlank @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String password,
    @NotBlank @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmPassword) {}
