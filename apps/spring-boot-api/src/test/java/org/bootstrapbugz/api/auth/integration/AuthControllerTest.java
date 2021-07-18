package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.response.RefreshTokenResponse;
import org.bootstrapbugz.api.auth.service.impl.JwtServiceImpl;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.response.RoleResponse;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class AuthControllerTest extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtServiceImpl jwtService;

  @Test
  void itShouldSignUp() throws Exception {
    var signUpRequest =
        new SignUpRequest("Test", "Test", "test", "test@localhost.com", "qwerty123", "qwerty123");
    var roleResponses = Set.of(new RoleResponse(RoleName.USER.name()));
    var expectedUserResponse =
        new UserResponse(
            8L, "Test", "Test", "test", "test@localhost.com", false, true, roleResponses);
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    var actualUserResponse =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class);
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void signUpShouldThrowBadRequest_invalidParameters() throws Exception {
    var signUpRequest =
        new SignUpRequest("Test1", "Test1", "user", "user@localhost.com", "qwerty123", "qwerty12");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addError("firstName", "Invalid first name.");
    expectedErrorResponse.addError("lastName", "Invalid last name.");
    expectedErrorResponse.addError("username", "Username already exists.");
    expectedErrorResponse.addError("email", "Email already exists.");
    expectedErrorResponse.addError("password", "Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldConfirmRegistration() throws Exception {
    String token = jwtService.createToken(3L, JwtPurpose.CONFIRM_REGISTRATION); // notActivated
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", JwtUtil.removeTokenTypeFromToken(token))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalidToken() throws Exception {
    String token = jwtService.createToken(10L, JwtPurpose.CONFIRM_REGISTRATION);  // unknown
    var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/confirm-registration")
                    .param("token", JwtUtil.removeTokenTypeFromToken(token))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Invalid token.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() throws Exception {
    String token = jwtService.createToken(2L, JwtPurpose.CONFIRM_REGISTRATION); // user
    var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/confirm-registration")
                    .param("token", JwtUtil.removeTokenTypeFromToken(token))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "User already activated.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldResendConfirmationEmail() throws Exception {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("notActivated");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void resendConfirmationEmailShouldThrowResourceNotFound_userNotFound() throws Exception {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("unknown");
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.NOT_FOUND, ErrorDomain.AUTH, "User not found.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/resend-confirmation-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
            .andExpect(status().isNotFound());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void resendConfirmationEmailShouldThrowForbidden_userAlreadyActivated() throws Exception {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("user");
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "User already activated.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/resend-confirmation-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldForgotPassword() throws Exception {
    var forgotPasswordRequest = new ForgotPasswordRequest("user@localhost.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() throws Exception {
    var forgotPasswordRequest = new ForgotPasswordRequest("unknown@localhost.com");
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.NOT_FOUND, ErrorDomain.AUTH, "User not found.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/forgot-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
            .andExpect(status().isNotFound());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldResetPassword() throws Exception {
    String token = jwtService.createToken(5L, JwtPurpose.FORGOT_PASSWORD);  // forUpdate1
    var resetPasswordRequest =
        new ResetPasswordRequest(
            JwtUtil.removeTokenTypeFromToken(token), "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isNoContent());
    TestUtil.login(mockMvc, objectMapper, new LoginRequest("forUpdate1", "qwerty1234"));
  }

  @Test
  void resetPasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    String token = jwtService.createToken(6L, JwtPurpose.FORGOT_PASSWORD);  // forUpdate2
    var resetPasswordRequest =
        new ResetPasswordRequest(
            JwtUtil.removeTokenTypeFromToken(token), "qwerty123", "qwerty1234");
    var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addError("password", "Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalidToken() throws Exception {
    String token = jwtService.createToken(10L, JwtPurpose.FORGOT_PASSWORD); // unknown
    var resetPasswordRequest =
        new ResetPasswordRequest(
            JwtUtil.removeTokenTypeFromToken(token), "qwerty1234", "qwerty1234");
    var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isForbidden());
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Invalid token.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldRefreshToken() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    var refreshTokenRequest = new RefreshTokenRequest(loginResponse.getRefreshToken());
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    var refreshTokenResponse =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(),
            RefreshTokenResponse.class);
    assertThat(refreshTokenResponse.getToken()).isNotEqualTo(loginResponse.getToken());
    assertThat(refreshTokenResponse.getRefreshToken())
        .isNotEqualTo(loginResponse.getRefreshToken());
  }

  @Test
  void itShouldLogout() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    mockMvc
        .perform(
            get(Path.AUTH + "/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, loginResponse.getToken()))
        .andExpect(status().isNoContent());
    jwtShouldBeInvalid(loginResponse.getToken());
    refreshTokenShouldBeInvalid(loginResponse.getRefreshToken());
  }

  private void jwtShouldBeInvalid(String token) throws Exception {
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Forbidden");
    var resultActions =
        mockMvc
            .perform(
                get(Path.USERS + "/{username}", "user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, token))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  private void refreshTokenShouldBeInvalid(String refreshToken) throws Exception {
    var refreshTokenRequest = new RefreshTokenRequest(refreshToken);
    var expectedErrorResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Invalid token.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldLogoutFromAllDevices() throws Exception {
    var loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    mockMvc
        .perform(
            get(Path.AUTH + "/logout-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, loginResponse.getToken()))
        .andExpect(status().isNoContent());
    jwtShouldBeInvalid(loginResponse.getToken());
    refreshTokenShouldBeInvalid(loginResponse.getRefreshToken());
  }
}
