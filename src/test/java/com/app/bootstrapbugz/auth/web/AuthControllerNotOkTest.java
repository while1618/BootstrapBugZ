package com.app.bootstrapbugz.auth.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.bootstrapbugz.auth.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.request.LoginRequest;
import com.app.bootstrapbugz.auth.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.request.SignUpRequest;
import com.app.bootstrapbugz.shared.constants.Path;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerNotOkTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void login_badCredentials_unauthorized() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("test", "BlaBla123"))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void login_userNotActivated_unauthorized() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new LoginRequest("not_activated", "qwerty123"))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void login_userLocked_unauthorized() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("locked", "qwerty123"))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signUp_invalidParameters_badRequest() throws Exception {
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidSignUpRequest())))
        .andExpect(status().isBadRequest());
  }

  private SignUpRequest invalidSignUpRequest() {
    return new SignUpRequest()
        .setFirstName("Test12")
        .setLastName("Test12")
        .setUsername("test")
        .setEmail("the.littlefinger63@gmail.com")
        .setPassword("BlaBla123")
        .setConfirmPassword("qwerty123");
  }

  @Test
  void confirmRegistration_invalidToken_forbidden() throws Exception {
    String invalidToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNTg3NDA1MTA1fQ.mZw2TRWdCnlRlqyKznhJKPxp_4vBBALYsOGjoqDbWq42lK9d2lFaavLxR-WuMelZJC2Ae0AVb4Xd-5JMpGgoJQ";
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void confirmRegistration_invalidToken_userNotFound_forbidden() throws Exception {
    String invalidToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzIiwiZXhwIjoxNTk3MTY1OTY2fQ.jodYzNMMTcAr7v5r-aeTDs7qyxXOY625qAVBbptHsvbN1LBp5loe3s647LYAAupt9Eku-yyzNPty7d0lUYYnhA";
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", invalidToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void confirmRegistration_tokenNotProvided_forbidden() throws Exception {
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", "")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void resendConfirmationEmail_userNotFound_notFound() throws Exception {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("not_found@localhost.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void resendConfirmationEmail_alreadyActivated_forbidden() throws Exception {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("decrescendo807@gmail.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void forgotPassword_userNotFound_notFound() throws Exception {
    ForgotPasswordRequest forgotPasswordRequest =
        new ForgotPasswordRequest("notFound@localhost.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void forgotPassword_invalidParameters_badRequest() throws Exception {
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("notAnEmail");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void resetPassword_invalidToken_forbidden() throws Exception {
    String invalidToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxNTg3NDA1MTA1fQ.mZw2TRWdCnlRlqyKznhJKPxp_4vBBALYsOGjoqDbWq42lK9d2lFaavLxR-WuMelZJC2Ae0AVb4Xd-5JMpGgoJQ";
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(invalidToken, "qwerty123", "qwerty123");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void resetPassword_invalidToken_userNotFound_forbidden() throws Exception {
    String invalidToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIzIiwiZXhwIjoxNTk3MTY1OTY2fQ.jodYzNMMTcAr7v5r-aeTDs7qyxXOY625qAVBbptHsvbN1LBp5loe3s647LYAAupt9Eku-yyzNPty7d0lUYYnhA";
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(invalidToken, "qwerty123", "qwerty123");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void resetPassword_tokenNotProvided_forbidden() throws Exception {
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest("", "qwerty123", "qwerty123");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isForbidden());
  }

  @Test
  void logout_userNotLoggedIn_forbidden() throws Exception {
    mockMvc
        .perform(get(Path.AUTH + "/logout").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
