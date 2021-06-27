package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.response.LoginResponse;
import org.bootstrapbugz.api.auth.response.RefreshTokenResponse;
import org.bootstrapbugz.api.auth.service.impl.JwtServiceImpl;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.config.RedisTestConfig;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.bootstrapbugz.api.user.response.RoleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = RedisTestConfig.class)
class AuthControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtServiceImpl jwtService;

  @Test
  void itShouldSignUp() throws Exception {
    SignUpRequest signUpRequest =
        new SignUpRequest("Test", "Test", "test", "test@localhost.com", "qwerty123", "qwerty123");
    Set<RoleResponse> roles = Set.of(new RoleResponse(RoleName.USER.name()));
    UserResponse expectedResponse =
        new UserResponse(8L, "Test", "Test", "test", "test@localhost.com", false, true, roles);
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    UserResponse actualResponse =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class);
    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

  @Test
  void signUpShouldThrowBadRequest_invalidParameters() throws Exception {
    SignUpRequest signUpRequest =
        new SignUpRequest("Test1", "Test1", "user", "user@localhost.com", "qwerty123", "qwerty12");
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedResponse.addError("firstName", "Invalid first name.");
    expectedResponse.addError("lastName", "Invalid last name.");
    expectedResponse.addError("username", "Username already exists.");
    expectedResponse.addError("email", "Email already exists.");
    expectedResponse.addError("password", "Passwords do not match.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldConfirmRegistration() throws Exception {
    String token = jwtService.createToken("notActivated", JwtPurpose.CONFIRM_REGISTRATION);
    mockMvc
        .perform(
            get(Path.AUTH + "/confirm-registration")
                .param("token", JwtUtil.removeTokenTypeFromToken(token))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalidToken() throws Exception {
    String token = jwtService.createToken("unknown", JwtPurpose.CONFIRM_REGISTRATION);
    ResultActions resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/confirm-registration")
                    .param("token", JwtUtil.removeTokenTypeFromToken(token))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Invalid token.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() throws Exception {
    String token = jwtService.createToken("user", JwtPurpose.CONFIRM_REGISTRATION);
    ResultActions resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/confirm-registration")
                    .param("token", JwtUtil.removeTokenTypeFromToken(token))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "User already activated.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldResendConfirmationEmail() throws Exception {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("notActivated");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void resendConfirmationEmailShouldThrowResourceNotFound_userNotFound() throws Exception {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("unknown");
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.NOT_FOUND, ErrorDomain.AUTH, "User not found.");
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/resend-confirmation-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
            .andExpect(status().isNotFound());
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void resendConfirmationEmailShouldThrowForbidden_userAlreadyActivated() throws Exception {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("user");
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "User already activated.");
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/resend-confirmation-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldForgotPassword() throws Exception {
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("user@localhost.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() throws Exception {
    ForgotPasswordRequest forgotPasswordRequest =
        new ForgotPasswordRequest("unknown@localhost.com");
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.NOT_FOUND, ErrorDomain.AUTH, "User not found.");
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/forgot-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
            .andExpect(status().isNotFound());
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldResetPassword() throws Exception {
    String token = jwtService.createToken("forUpdate1", JwtPurpose.FORGOT_PASSWORD);
    ResetPasswordRequest resetPasswordRequest =
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
    String token = jwtService.createToken("forUpdate2", JwtPurpose.FORGOT_PASSWORD);
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(
            JwtUtil.removeTokenTypeFromToken(token), "qwerty123", "qwerty1234");
    ResultActions resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isBadRequest());
    ErrorResponse expectedResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedResponse.addError("password", "Passwords do not match.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalidToken() throws Exception {
    String token = jwtService.createToken("unknown", JwtPurpose.FORGOT_PASSWORD);
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(
            JwtUtil.removeTokenTypeFromToken(token), "qwerty1234", "qwerty1234");
    ResultActions resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isForbidden());
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Invalid token.");
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldRefreshToken() throws Exception {
    LoginResponse loginResponse =
        TestUtil.login(mockMvc, objectMapper, new LoginRequest("user", "qwerty123"));
    RefreshTokenRequest refreshTokenRequest =
        new RefreshTokenRequest(loginResponse.getRefreshToken());
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    RefreshTokenResponse refreshTokenResponse =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(),
            RefreshTokenResponse.class);
    assertThat(refreshTokenResponse.getToken()).isNotEqualTo(loginResponse.getToken());
    assertThat(refreshTokenResponse.getRefreshToken())
        .isNotEqualTo(loginResponse.getRefreshToken());
  }

  @Test
  void itShouldLogout() throws Exception {
    LoginResponse loginResponse =
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
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Forbidden");
    ResultActions resultActions =
        mockMvc
            .perform(
                get(Path.USERS + "/{username}", "user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, token))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  private void refreshTokenShouldBeInvalid(String refreshToken) throws Exception {
    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshToken);
    ErrorResponse expectedResponse =
        new ErrorResponse(HttpStatus.FORBIDDEN, ErrorDomain.AUTH, "Invalid token.");
    ResultActions resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isForbidden());
    TestUtil.checkErrorMessages(expectedResponse, resultActions);
  }

  @Test
  void itShouldLogoutFromAllDevices() throws Exception {
    LoginResponse loginResponse =
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
