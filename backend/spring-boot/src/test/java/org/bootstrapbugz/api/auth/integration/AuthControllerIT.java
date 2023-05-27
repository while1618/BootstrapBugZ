package org.bootstrapbugz.api.auth.integration;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bootstrapbugz.api.auth.jwt.service.impl.ConfirmRegistrationTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.ResetPasswordTokenServiceImpl;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.config.DatabaseContainers;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.email.service.EmailService;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class AuthControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ConfirmRegistrationTokenServiceImpl confirmRegistrationTokenService;
  @Autowired private ResetPasswordTokenServiceImpl resetPasswordService;
  @Autowired private UserRepository userRepository;

  @MockBean private EmailService emailService;

  @Test
  void signUp() throws Exception {
    final var signUpRequest =
        SignUpRequest.builder()
            .firstName("Test")
            .lastName("Test")
            .username("test")
            .email("test@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty123")
            .build();
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("test"));
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void signUp_throwBadRequest_invalidParameters() throws Exception {
    final var signUpRequest =
        SignUpRequest.builder()
            .firstName("User1")
            .lastName("User1")
            .username("user")
            .email("user@localhost")
            .password("qwerty123")
            .confirmPassword("qwerty1234")
            .build();
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Invalid first name.")))
        .andExpect(content().string(containsString("Invalid last name.")))
        .andExpect(content().string(containsString("Username already exists.")))
        .andExpect(content().string(containsString("Email already exists.")))
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void resendConfirmationEmail() throws Exception {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("deactivated");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNoContent());
    verify(emailService, times(1))
        .sendHtmlEmail(any(String.class), any(String.class), any(String.class));
  }

  @Test
  void resendConfirmationEmail_throwResourceNotFound_userNotFound() throws Exception {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("unknown");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("User not found.")));
  }

  @Test
  void resendConfirmationEmail_throwForbidden_userAlreadyActivated() throws Exception {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("user");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("User already activated.")));
  }

  @Test
  void confirmRegistration() throws Exception {
    final var user = userRepository.findByUsername("deactivated").orElseThrow();
    final var token = confirmRegistrationTokenService.create(user.getId());
    final var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    mockMvc
        .perform(
            put(Path.AUTH + "/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void confirmRegistration_throwForbidden_invalidToken() throws Exception {
    final var user = User.builder().id(100L).build();
    final var token = confirmRegistrationTokenService.create(user.getId());
    final var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    mockMvc
        .perform(
            put(Path.AUTH + "/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("Invalid token.")));
  }

  @Test
  void confirmRegistration_throwForbidden_userAlreadyActivated() throws Exception {
    final var user = userRepository.findByUsername("user").orElseThrow();
    final var token = confirmRegistrationTokenService.create(user.getId());
    final var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    mockMvc
        .perform(
            put(Path.AUTH + "/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("User already activated.")));
  }

  @Test
  void refreshToken() throws Exception {
    final var refreshToken =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, "update1").refreshToken();
    final var refreshTokenRequest = new RefreshTokenRequest(refreshToken);
    mockMvc
        .perform(
            post(Path.AUTH + "/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void signOut() throws Exception {
    final var signInDTO = IntegrationTestUtil.signIn(mockMvc, objectMapper, "delete1");
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
        .andExpect(status().isNoContent());
    invalidAccessToken(signInDTO.accessToken());
    invalidRefreshToken(signInDTO.refreshToken());
  }

  @Test
  void signOutFromAllDevices() throws Exception {
    final var signInDTO = IntegrationTestUtil.signIn(mockMvc, objectMapper, "delete2");
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
        .andExpect(status().isNoContent());
    invalidAccessToken(signInDTO.accessToken());
    invalidRefreshToken(signInDTO.refreshToken());
  }

  private void invalidAccessToken(String accessToken) throws Exception {
    mockMvc
        .perform(
            get(Path.AUTH + "/signed-in-user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, accessToken))
        .andExpect(status().isUnauthorized())
        .andExpect(
            content()
                .string(containsString("Full authentication is required to access this resource")));
  }

  private void invalidRefreshToken(String refreshToken) throws Exception {
    final var refreshTokenRequest = new RefreshTokenRequest(refreshToken);
    mockMvc
        .perform(
            post(Path.AUTH + "/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString("Invalid token.")));
  }

  @Test
  void forgotPassword() throws Exception {
    final var forgotPasswordRequest = new ForgotPasswordRequest("update4@localhost");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
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
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(containsString("User not found.")));
  }

  @Test
  void resetPassword() throws Exception {
    final var user = userRepository.findByUsername("update4").orElseThrow();
    final var token = resetPasswordService.create(user.getId());
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isNoContent());
    final var signInRequest = new SignInRequest("update4", "qwerty1234");
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void resetPassword_throwBadRequest_passwordsDoNotMatch() throws Exception {
    final var user = userRepository.findByUsername("update4").orElseThrow();
    final var token = resetPasswordService.create(user.getId());
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty123", "qwerty1234");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Passwords do not match.")));
  }

  @Test
  void resetPassword_throwForbidden_invalidToken() throws Exception {
    final var user = User.builder().id(100L).build();
    final var token = resetPasswordService.create(user.getId());
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isForbidden())
        .andExpect(content().string(containsString("Invalid token.")));
  }

  @Test
  void retrieveSignedInUser() throws Exception {
    final var signInDTO = IntegrationTestUtil.signIn(mockMvc, objectMapper, "user");
    mockMvc
        .perform(
            get(Path.AUTH + "/signed-in-user")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("user"));
  }

  @Test
  void usernameAvailability() throws Exception {
    mockMvc
        .perform(
            get(Path.AUTH + "/username-availability")
                .param("username", "user")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
  }

  @Test
  void emailAvailability() throws Exception {
    mockMvc
        .perform(
            get(Path.AUTH + "/email-availability")
                .param("email", "available@localhost")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }
}
