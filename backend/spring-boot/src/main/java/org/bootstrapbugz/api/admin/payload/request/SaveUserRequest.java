package org.bootstrapbugz.api.admin.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import org.bootstrapbugz.api.shared.constants.Regex;
import org.bootstrapbugz.api.shared.validator.FieldMatch;
import org.bootstrapbugz.api.user.model.Role.RoleName;

@Builder
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public record SaveUserRequest(
    @NonNull @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
        String firstName,
    @NonNull @Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
        String lastName,
    @NonNull @Pattern(regexp = Regex.USERNAME, message = "{username.invalid}") String username,
    @NotEmpty(message = "{email.invalid}") @Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        String email,
    @NonNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}") String password,
    @NonNull @Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
        String confirmPassword,
    @NonNull Boolean active,
    @NotNull Boolean lock,
    @NotEmpty(message = "{roles.empty}") Set<RoleName> roleNames) {}
