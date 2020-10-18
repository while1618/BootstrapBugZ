package com.app.bootstrapbugz.user.dto.request;

import com.app.bootstrapbugz.validator.FieldMatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{password.doNotMatch}")
public class ChangePasswordRequest {
    @NotEmpty(message = "{password.notEmpty}")
    private String oldPassword;

    @NotEmpty(message = "{password.notEmpty}")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "{password.regex}")
    private String newPassword;

    private String confirmNewPassword;
}
