package org.bootstrapbugz.api.auth.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.EmailExist;
import org.bootstrapbugz.api.shared.validator.FieldMatch;
import org.bootstrapbugz.api.shared.validator.UsernameExist;

@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public record SignUpRequest(
    @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}") String firstName,
    @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}") String lastName,
    @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
        @UsernameExist(message = "{username.exists}")
        String username,
    @NotEmpty(message = "{email.invalid}")
        @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        @EmailExist(message = "{email.exists}")
        String email,
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String password,
    @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String confirmPassword) {}
