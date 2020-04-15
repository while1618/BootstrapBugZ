package com.app.webapp.dto.request;

import com.app.webapp.validator.FieldMatch;
import com.app.webapp.validator.EmailExist;
import com.app.webapp.validator.UsernameExist;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
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
