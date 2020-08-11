package com.app.bootstrapbugz.service;

import com.app.bootstrapbugz.dto.model.user.UserDto;
import com.app.bootstrapbugz.dto.request.auth.ForgotPasswordRequest;
import com.app.bootstrapbugz.dto.request.auth.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.dto.request.auth.ResetPasswordRequest;
import com.app.bootstrapbugz.dto.request.auth.SignUpRequest;

public interface AuthService {
    UserDto signUp(SignUpRequest signUpRequest);
    void confirmRegistration(String token);
    void resendConfirmationEmail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
