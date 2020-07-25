package com.app.bootstrapbugz.dto.request.auth;

import com.app.bootstrapbugz.validator.FieldMatch;
import com.app.bootstrapbugz.validator.EmailExist;
import com.app.bootstrapbugz.validator.UsernameExist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@FieldMatch(first = "password", second = "confirmPassword", message = "{password.doNotMatch}")
public class SignUpRequest {
    @NotEmpty(message = "{firstName.notEmpty}")
    @Size(min = 2, max = 16, message = "{firstName.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{firstName.regex}")
    private String firstName;

    @NotEmpty(message = "{lastName.notEmpty}")
    @Size(min = 2, max = 16, message = "{lastName.size}")
    @Pattern(regexp = "^[a-zA-Z ,.'-]+$", message = "{lastName.regex}")
    private String lastName;

    @NotEmpty(message = "{username.notEmpty}")
    @Size(min = 2, max = 16, message = "{username.size}")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{username.regex}")
    @UsernameExist(message = "{username.exists}")
    private String username;

    @NotEmpty(message = "{email.notEmpty}")
    @Email(message = "{email.regex}")
    @EmailExist(message = "{email.exists}")
    private String email;

    @NotEmpty(message = "{password.notEmpty}")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "{password.regex}")
    private String password;

    private String confirmPassword;
}
