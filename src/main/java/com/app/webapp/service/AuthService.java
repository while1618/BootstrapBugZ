package com.app.webapp.service;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.*;
import com.app.webapp.dto.response.JwtAuthenticationResponse;

public interface AuthService {
    JwtAuthenticationResponse login(LoginRequest loginRequest);
    UserDto signUp(SignUpRequest signUpRequest);
    void confirmRegistration(String token);
    void resendConfirmationMail(ResendConfirmationEmailRequest resendConfirmationEmailRequest);
    void forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
