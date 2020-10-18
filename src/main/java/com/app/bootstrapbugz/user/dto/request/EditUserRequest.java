package com.app.bootstrapbugz.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {
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
    private String username;

    @NotEmpty(message = "{email.notEmpty}")
    @Email(message = "{email.regex}")
    private String email;
}
