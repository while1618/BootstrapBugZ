package org.bugzkit.api.auth.integration;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bugzkit.api.auth.jwt.service.impl.ResetPasswordTokenServiceImpl;
import org.bugzkit.api.auth.jwt.service.impl.VerificationTokenServiceImpl;
import org.bugzkit.api.auth.payload.request.AuthTokensRequest;
import org.bugzkit.api.auth.payload.request.ForgotPasswordRequest;
import org.bugzkit.api.auth.payload.request.RefreshAuthTokensRequest;
import org.bugzkit.api.auth.payload.request.RegisterUserRequest;
import org.bugzkit.api.auth.payload.request.ResetPasswordRequest;
import org.bugzkit.api.auth.payload.request.VerificationEmailRequest;
import org.bugzkit.api.auth.payload.request.VerifyEmailRequest;
import org.bugzkit.api.shared.config.DatabaseContainers;
import org.bugzkit.api.shared.constants.Path;
import org.bugzkit.api.shared.email.service.EmailService;
import org.bugzkit.api.shared.util.IntegrationTestUtil;
import org.bugzkit.api.user.model.User;
import org.bugzkit.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class AuthControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private VerificationTokenServiceImpl verificationTokenService;
  @Autowired private ResetPasswordTokenServiceImpl resetPasswordService;
  @Autowired private UserRepository userRepository;

  @MockitoBean private EmailService emailService;

  @Test
  void registerUser() throws Exception {
    final var registerUserRequest =
        RegisterUserRequest.builder()
            .username("test")
            .email("test@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .build();
    mockMvc
        .perform(
            post(Path.AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("test"));
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void registerUser_throwBadRequest_invalidParameters() throws Exception {
    final var registerUserRequest =
        RegisterUserRequest.builder()
            .username("user")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty1234")
            .build();
    mockMvc
        .perform(
            post(Path.AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("API_ERROR_USER_PASSWORDS_DO_NOT_MATCH")));
  }

  @Test
  void registerUser_throwConflict_usernameExists() throws Exception {
    final var registerUserRequest =
        RegisterUserRequest.builder()
            .username("user")
            .email("test123@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .build();
    mockMvc
        .perform(
            post(Path.AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("API_ERROR_USER_USERNAME_EXISTS")));
  }

  @Test
  void registerUser_throwConflict_emailExists() throws Exception {
    final var registerUserRequest =
        RegisterUserRequest.builder()
            .username("test123")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .build();
    mockMvc
        .perform(
            post(Path.AUTH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("API_ERROR_USER_EMAIL_EXISTS")));
  }

  @Test
  void authenticate() throws Exception {
    final var authTokensRequest = new AuthTokensRequest("user", "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.refreshToken").isString());
  }

  @Test
  void authenticate_throwUnauthorized_wrongCredentials() throws Exception {
    final var authTokensRequest = new AuthTokensRequest("wrong", "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString("API_ERROR_AUTH_UNAUTHORIZED")));
  }

  @ParameterizedTest
  @CsvSource({
    "locked, API_ERROR_USER_LOCK",
    "deactivated1, API_ERROR_USER_NOT_ACTIVE",
  })
  void authenticate_throwForbidden_lockedDeactivatedUser(String username, String message)
      throws Exception {
    final var authTokensRequest = new AuthTokensRequest(username, "qwerty123");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString(message)));
  }

  @Test
  void deleteTokens() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "delete1");
    mockMvc
        .perform(
            delete(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken())))
        .andExpect(status().isNoContent());
    invalidAccessToken(authTokens.accessToken());
    invalidRefreshToken(authTokens.refreshToken());
  }

  @Test
  void deleteTokensOnAllDevices() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "delete2");
    Thread.sleep(1000);
    mockMvc
        .perform(
            delete(Path.AUTH + "/tokens/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(authTokens.accessToken())))
        .andExpect(status().isNoContent());
    invalidAccessToken(authTokens.accessToken());
    invalidRefreshToken(authTokens.refreshToken());
  }

  private void invalidAccessToken(String accessToken) throws Exception {
    mockMvc
        .perform(
            get(Path.PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(IntegrationTestUtil.authHeader(accessToken)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString("API_ERROR_AUTH_UNAUTHORIZED")));
  }

  private void invalidRefreshToken(String refreshToken) throws Exception {
    final var refreshTokenRequest = new RefreshAuthTokensRequest(refreshToken);
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("API_ERROR_AUTH_TOKEN_INVALID")));
  }

  @Test
  void refreshToken() throws Exception {
    final var authTokens = IntegrationTestUtil.authTokens(mockMvc, objectMapper, "update1");
    final var refreshTokenRequest = new RefreshAuthTokensRequest(authTokens.refreshToken());
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void forgotPassword() throws Exception {
    final var forgotPasswordRequest = new ForgotPasswordRequest("update4@localhost");
    mockMvc
        .perform(
            post(Path.AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNoContent());
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void forgotPassword_throwResourceNotFound_userNotFound() throws Exception {
    final var forgotPasswordRequest = new ForgotPasswordRequest("unknown@localhost");
    mockMvc
        .perform(
            post(Path.AUTH + "/password/forgot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("API_ERROR_USER_NOT_FOUND")));
  }

  @Test
  void resetPassword() throws Exception {
    final var user = userRepository.findByUsername("update4").orElseThrow();
    final var token = resetPasswordService.create(user.getId());
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            post(Path.AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isNoContent());
    final var authTokensRequest = new AuthTokensRequest("update4", "qwerty1234");
    mockMvc
        .perform(
            post(Path.AUTH + "/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authTokensRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void resetPassword_throwBadRequest_passwordsDoNotMatch() throws Exception {
    final var user = userRepository.findByUsername("update4").orElseThrow();
    final var token = resetPasswordService.create(user.getId());
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty123", "qwerty1234");
    mockMvc
        .perform(
            post(Path.AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("API_ERROR_USER_PASSWORDS_DO_NOT_MATCH")));
  }

  @Test
  void resetPassword_throwBadRequest_invalidToken() throws Exception {
    final var user = User.builder().id(100L).build();
    final var token = resetPasswordService.create(user.getId());
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            post(Path.AUTH + "/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("API_ERROR_AUTH_TOKEN_INVALID")));
  }

  @Test
  void sendVerificationMail() throws Exception {
    final var verificationMailRequest = new VerificationEmailRequest("deactivated1");
    mockMvc
        .perform(
            post(Path.AUTH + "/verification-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationMailRequest)))
        .andExpect(status().isNoContent());
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void sendVerificationMail_throwResourceNotFound_userNotFound() throws Exception {
    final var verificationMailRequest = new VerificationEmailRequest("unknown");
    mockMvc
        .perform(
            post(Path.AUTH + "/verification-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationMailRequest)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("API_ERROR_USER_NOT_FOUND")));
  }

  @Test
  void sendVerificationMail_throwConflict_userAlreadyActivated() throws Exception {
    final var verificationMailRequest = new VerificationEmailRequest("user");
    mockMvc
        .perform(
            post(Path.AUTH + "/verification-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationMailRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("API_ERROR_USER_ACTIVE")));
  }

  @Test
  void verifyEmail() throws Exception {
    final var user = userRepository.findByUsername("deactivated2").orElseThrow();
    final var token = verificationTokenService.create(user.getId());
    final var verifyEmailRequest = new VerifyEmailRequest(token);
    mockMvc
        .perform(
            post(Path.AUTH + "/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyEmailRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void verifyEmail_throwBadRequest_invalidToken() throws Exception {
    final var user = User.builder().id(100L).build();
    final var token = verificationTokenService.create(user.getId());
    final var verifyEmailRequest = new VerifyEmailRequest(token);
    mockMvc
        .perform(
            post(Path.AUTH + "/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyEmailRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("API_ERROR_AUTH_TOKEN_INVALID")));
  }

  @Test
  void verifyEmail_throwConflict_userAlreadyActivated() throws Exception {
    final var user = userRepository.findByUsername("user").orElseThrow();
    final var token = verificationTokenService.create(user.getId());
    final var verifyEmailRequest = new VerifyEmailRequest(token);
    mockMvc
        .perform(
            post(Path.AUTH + "/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyEmailRequest)))
        .andExpect(status().isConflict())
        .andExpect(content().string(containsString("API_ERROR_USER_ACTIVE")));
  }
}
