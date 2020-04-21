package com.app.webapp.service;

import com.app.webapp.dto.model.user.UserDto;
import com.app.webapp.dto.request.auth.ForgotPasswordRequest;
import com.app.webapp.dto.request.auth.ResendConfirmationEmailRequest;
import com.app.webapp.dto.request.auth.ResetPasswordRequest;
import com.app.webapp.dto.request.auth.SignUpRequest;

public interface AuthService {
    UserDto signUp(SignUpRequest signUpRequest);
    void confirmRegistration(String token);
    void resendConfirmationMail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
