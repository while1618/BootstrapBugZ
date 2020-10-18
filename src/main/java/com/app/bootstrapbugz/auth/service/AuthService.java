package com.app.bootstrapbugz.auth.service;

import com.app.bootstrapbugz.user.dto.model.UserDto;
import com.app.bootstrapbugz.auth.dto.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.dto.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.dto.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.dto.request.SignUpRequest;

public interface AuthService {
    UserDto signUp(SignUpRequest signUpRequest);

    void confirmRegistration(String token);

    void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);

    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
