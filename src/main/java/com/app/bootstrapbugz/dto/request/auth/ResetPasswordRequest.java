package com.app.bootstrapbugz.dto.request.auth;

import com.app.bootstrapbugz.validator.FieldMatch;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public class ResetPasswordRequest {
    @NotEmpty(message = "{authToken.notFound}")
    private String token;

    @NotEmpty(message = "{password.notEmpty}")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "{password.regex}")
    private String password;

    private String confirmPassword;
}
