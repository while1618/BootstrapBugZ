package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public record RegisterUserRequest(
    @NotNull @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
        String firstName,
    @NotNull @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
        String lastName,
    @NotNull @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String username,
    @NotEmpty(message = "{email.invalid}") @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        String email,
    @NotNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String password,
    @NotNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmPassword) {}
