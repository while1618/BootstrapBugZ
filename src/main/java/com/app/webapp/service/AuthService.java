package com.app.webapp.service;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.*;

public interface AuthService {
    UserDto signUp(SignUpRequest signUpRequest);
    void confirmRegistration(String token);
    void resendConfirmationMail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
