package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;
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
import org.bootstrapbugz.api.shared.error.ErrorMessage;
import org.bootstrapbugz.api.shared.util.IntegrationTestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ConfirmRegistrationTokenServiceImpl confirmRegistrationTokenService;
  @Autowired private ResetPasswordTokenServiceImpl resetPasswordService;

  @Test
  void itShouldSignUp() throws Exception {
    final var signUpRequest =
        new SignUpRequest(
            "Test", "Test", "test", "test@bootstrapbugz.com", "qwerty123", "qwerty123");
    final var roleDTOs = Set.of(new RoleDTO(RoleName.USER.name()));
    final var expectedUserDTO =
        new UserDTO(
            8L,
            "Test",
            "Test",
            "test",
            "test@bootstrapbugz.com",
            false,
            true,
            LocalDateTime.now(),
            roleDTOs);
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    final var actualUserDTO =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), UserDTO.class);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void signUpShouldThrowBadRequest_invalidParameters() throws Exception {
    final var signUpRequest =
        new SignUpRequest(
            "Test1", "Test1", "user", "user@bootstrapbugz.com", "qwerty123", "qwerty12");
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isBadRequest());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("firstName", "Invalid first name.");
    expectedErrorResponse.addDetails("lastName", "Invalid last name.");
    expectedErrorResponse.addDetails("username", "Username already exists.");
    expectedErrorResponse.addDetails("email", "Email already exists.");
    expectedErrorResponse.addDetails("Passwords do not match.");
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldResendConfirmationEmail() throws Exception {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("not.activated");
    mockMvc
        .perform(
            post(Path.AUTH + "/resend-confirmation-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void resendConfirmationEmailShouldThrowResourceNotFound_userNotFound() throws Exception {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("unknown");
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.NOT_FOUND);
    expectedErrorResponse.addDetails("User not found.");
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/resend-confirmation-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
            .andExpect(status().isNotFound());
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void resendConfirmationEmailShouldThrowForbidden_userAlreadyActivated() throws Exception {
    final var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("user");
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("User already activated.");
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/resend-confirmation-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resendConfirmationEmailRequest)))
            .andExpect(status().isForbidden());
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldConfirmRegistration() throws Exception {
    final var token = confirmRegistrationTokenService.create(3L); // not.activated
    final var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    mockMvc
        .perform(
            put(Path.AUTH + "/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalidToken() throws Exception {
    final var token = confirmRegistrationTokenService.create(10L); // unknown
    final var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    final var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/confirm-registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
            .andExpect(status().isForbidden());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("Invalid token.");
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() throws Exception {
    final var token = confirmRegistrationTokenService.create(2L); // user
    final var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    final var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/confirm-registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
            .andExpect(status().isForbidden());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("User already activated.");
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldRefreshToken() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var refreshTokenRequest = new RefreshTokenRequest(signInDTO.refreshToken());
    mockMvc
        .perform(
            post(Path.AUTH + "/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void itShouldSignOut() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
        .andExpect(status().isNoContent());
    jwtShouldBeInvalid(signInDTO.accessToken());
    refreshTokenShouldBeInvalid(signInDTO.refreshToken());
  }

  private void jwtShouldBeInvalid(String token) throws Exception {
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.UNAUTHORIZED);
    expectedErrorResponse.addDetails("Full authentication is required to access this resource");
    final var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/signed-in-user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, token))
            .andExpect(status().isUnauthorized());
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  private void refreshTokenShouldBeInvalid(String refreshToken) throws Exception {
    final var refreshTokenRequest = new RefreshTokenRequest(refreshToken);
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.UNAUTHORIZED);
    expectedErrorResponse.addDetails("Invalid token.");
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isUnauthorized());
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldSignOutFromAllDevices() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
        .andExpect(status().isNoContent());
    jwtShouldBeInvalid(signInDTO.accessToken());
    refreshTokenShouldBeInvalid(signInDTO.refreshToken());
  }

  @Test
  void itShouldForgotPassword() throws Exception {
    final var forgotPasswordRequest = new ForgotPasswordRequest("user@bootstrapbugz.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() throws Exception {
    final var forgotPasswordRequest = new ForgotPasswordRequest("unknown@bootstrapbugz.com");
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.NOT_FOUND);
    expectedErrorResponse.addDetails("User not found.");
    final var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/forgot-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
            .andExpect(status().isNotFound());
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldResetPassword() throws Exception {
    final var token = resetPasswordService.create(5L); // for.update.1
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isNoContent());
    IntegrationTestUtil.signIn(
        mockMvc, objectMapper, new SignInRequest("for.update.1", "qwerty1234"));
  }

  @Test
  void resetPasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    final var token = resetPasswordService.create(6L); // for.update.2
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty123", "qwerty1234");
    final var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isBadRequest());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("Passwords do not match.");
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalidToken() throws Exception {
    final var token = resetPasswordService.create(10L); // unknown
    final var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    final var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isForbidden());
    final var expectedErrorResponse = new ErrorMessage(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("Invalid token.");
    IntegrationTestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldRetrieveSignedInUser() throws Exception {
    final var signInDTO =
        IntegrationTestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    final var roleDTOs = Set.of(new RoleDTO(RoleName.USER.name()));
    final var expectedUserDTO =
        new UserDTO(
            2L,
            "User",
            "User",
            "user",
            "user@bootstrapbugz.com",
            true,
            true,
            LocalDateTime.now(),
            roleDTOs);
    final var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/signed-in-user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, signInDTO.accessToken()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    final var actualUserDTO =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), UserDTO.class);
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  void itShouldCheckUsernameAvailability() throws Exception {
    mockMvc
        .perform(
            get(Path.AUTH + "/username-availability")
                .param("username", "user")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string("false"));
  }

  @Test
  void itShouldCheckEmailAvailability() throws Exception {
    mockMvc
        .perform(
            get(Path.AUTH + "/email-availability")
                .param("email", "available@bootstrapbugz.com")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string("true"));
  }
}
