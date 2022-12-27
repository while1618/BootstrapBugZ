package org.bootstrapbugz.api.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.service.impl.ConfirmRegistrationTokenServiceImpl;
import org.bootstrapbugz.api.auth.jwt.service.impl.ForgotPasswordTokenServiceImpl;
import org.bootstrapbugz.api.auth.payload.dto.RefreshTokenDto;
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
import org.bootstrapbugz.api.shared.error.response.ErrorResponse;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDto;
import org.bootstrapbugz.api.user.payload.dto.UserDto;
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
class AuthControllerIT extends DatabaseContainers {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ConfirmRegistrationTokenServiceImpl confirmRegistrationTokenService;
  @Autowired private ForgotPasswordTokenServiceImpl forgotPasswordTokenService;

  @Test
  void itShouldSignUp() throws Exception {
    var signUpRequest =
        new SignUpRequest(
            "Test", "Test", "test", "test@bootstrapbugz.com", "qwerty123", "qwerty123");
    var roleDtos = Set.of(new RoleDto(RoleName.USER.name()));
    var expectedUserDto =
        new UserDto(8L, "Test", "Test", "test", "test@bootstrapbugz.com", false, true, roleDtos);
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    var actualUserDto =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), UserDto.class);
    assertThat(actualUserDto).isEqualTo(expectedUserDto);
  }

  @Test
  void signUpShouldThrowBadRequest_invalidParameters() throws Exception {
    var signUpRequest =
        new SignUpRequest(
            "Test1", "Test1", "user", "user@bootstrapbugz.com", "qwerty123", "qwerty12");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("firstName", "Invalid first name.");
    expectedErrorResponse.addDetails("lastName", "Invalid last name.");
    expectedErrorResponse.addDetails("username", "Username already exists.");
    expectedErrorResponse.addDetails("email", "Email already exists.");
    expectedErrorResponse.addDetails("Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldResendConfirmationEmail() throws Exception {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("not.activated");
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
    var expectedErrorResponse = new ErrorResponse(HttpStatus.NOT_FOUND);
    expectedErrorResponse.addDetails("User not found.");
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
    var expectedErrorResponse = new ErrorResponse(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("User already activated.");
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
  void itShouldConfirmRegistration() throws Exception {
    String token = confirmRegistrationTokenService.create(3L); // not.activated
    var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    mockMvc
        .perform(
            put(Path.AUTH + "/confirm-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalidToken() throws Exception {
    String token = confirmRegistrationTokenService.create(10L); // unknown
    var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/confirm-registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
            .andExpect(status().isForbidden());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("Invalid token.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() throws Exception {
    String token = confirmRegistrationTokenService.create(2L); // user
    var confirmRegistrationRequest = new ConfirmRegistrationRequest(token);
    var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/confirm-registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(confirmRegistrationRequest)))
            .andExpect(status().isForbidden());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("User already activated.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldRefreshToken() throws Exception {
    var signInDto = TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var refreshTokenRequest = new RefreshTokenRequest(signInDto.getRefreshToken());
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    var refreshTokenDto =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), RefreshTokenDto.class);
    assertThat(refreshTokenDto.getAccessToken()).isNotEqualTo(signInDto.getAccessToken());
    assertThat(refreshTokenDto.getRefreshToken()).isNotEqualTo(signInDto.getRefreshToken());
  }

  @Test
  void itShouldSignOut() throws Exception {
    var signInDto = TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDto.getAccessToken()))
        .andExpect(status().isNoContent());
    jwtShouldBeInvalid(signInDto.getAccessToken());
    refreshTokenShouldBeInvalid(signInDto.getRefreshToken());
  }

  private void jwtShouldBeInvalid(String token) throws Exception {
    var expectedErrorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED);
    expectedErrorResponse.addDetails("Full authentication is required to access this resource");
    var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/signed-in-user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, token))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  private void refreshTokenShouldBeInvalid(String refreshToken) throws Exception {
    var refreshTokenRequest = new RefreshTokenRequest(refreshToken);
    var expectedErrorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED);
    expectedErrorResponse.addDetails("Invalid token.");
    var resultActions =
        mockMvc
            .perform(
                post(Path.AUTH + "/refresh-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(refreshTokenRequest)))
            .andExpect(status().isUnauthorized());
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldSignOutFromAllDevices() throws Exception {
    var signInDto = TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    mockMvc
        .perform(
            post(Path.AUTH + "/sign-out-from-all-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AuthUtil.AUTH_HEADER, signInDto.getAccessToken()))
        .andExpect(status().isNoContent());
    jwtShouldBeInvalid(signInDto.getAccessToken());
    refreshTokenShouldBeInvalid(signInDto.getRefreshToken());
  }

  @Test
  void itShouldForgotPassword() throws Exception {
    var forgotPasswordRequest = new ForgotPasswordRequest("user@bootstrapbugz.com");
    mockMvc
        .perform(
            post(Path.AUTH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() throws Exception {
    var forgotPasswordRequest = new ForgotPasswordRequest("unknown@bootstrapbugz.com");
    var expectedErrorResponse = new ErrorResponse(HttpStatus.NOT_FOUND);
    expectedErrorResponse.addDetails("User not found.");
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
    String token = forgotPasswordTokenService.create(5L); // for.update.1
    var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    mockMvc
        .perform(
            put(Path.AUTH + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
        .andExpect(status().isNoContent());
    TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("for.update.1", "qwerty1234"));
  }

  @Test
  void resetPasswordShouldThrowBadRequest_passwordsDoNotMatch() throws Exception {
    String token = forgotPasswordTokenService.create(6L); // for.update.2
    var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty123", "qwerty1234");
    var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isBadRequest());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST);
    expectedErrorResponse.addDetails("Passwords do not match.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalidToken() throws Exception {
    String token = forgotPasswordTokenService.create(10L); // unknown
    var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    var resultActions =
        mockMvc
            .perform(
                put(Path.AUTH + "/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(resetPasswordRequest)))
            .andExpect(status().isForbidden());
    var expectedErrorResponse = new ErrorResponse(HttpStatus.FORBIDDEN);
    expectedErrorResponse.addDetails("Invalid token.");
    TestUtil.checkErrorMessages(expectedErrorResponse, resultActions);
  }

  @Test
  void itShouldRetrieveSignedInUser() throws Exception {
    var signInDto = TestUtil.signIn(mockMvc, objectMapper, new SignInRequest("user", "qwerty123"));
    var roleDtos = Set.of(new RoleDto(RoleName.USER.name()));
    var expectedUserDto =
        new UserDto(2L, "User", "User", "user", "user@bootstrapbugz.com", true, true, roleDtos);
    var resultActions =
        mockMvc
            .perform(
                get(Path.AUTH + "/signed-in-user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AuthUtil.AUTH_HEADER, signInDto.getAccessToken()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    var actualUserDto =
        objectMapper.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), UserDto.class);
    assertThat(actualUserDto).isEqualTo(expectedUserDto);
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
