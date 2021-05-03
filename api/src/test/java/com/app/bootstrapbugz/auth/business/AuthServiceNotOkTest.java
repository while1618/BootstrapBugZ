package com.app.bootstrapbugz.auth.business;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.app.bootstrapbugz.auth.request.ForgotPasswordRequest;
import com.app.bootstrapbugz.auth.request.ResendConfirmationEmailRequest;
import com.app.bootstrapbugz.auth.request.ResetPasswordRequest;
import com.app.bootstrapbugz.auth.service.impl.AuthServiceImpl;
import com.app.bootstrapbugz.shared.error.exception.ForbiddenException;
import com.app.bootstrapbugz.shared.error.exception.ResourceNotFound;
import com.app.bootstrapbugz.jwt.util.JwtUtilities;
import com.app.bootstrapbugz.user.mapper.UserMapperImpl;
import com.app.bootstrapbugz.user.model.Role;
import com.app.bootstrapbugz.user.model.RoleName;
import com.app.bootstrapbugz.user.model.User;
import com.app.bootstrapbugz.user.repository.RoleRepository;
import com.app.bootstrapbugz.user.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceNotOkTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private MessageSource messageSource;
  @Mock private JwtUtilities jwtUtilities;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;

  @InjectMocks private AuthServiceImpl authService;

  private User getUser() {
    return new User()
        .setId(1L)
        .setFirstName("User")
        .setLastName("User")
        .setUsername("user")
        .setEmail("user@localhost.com")
        .setPassword(bCryptPasswordEncoder.encode("qwerty123"))
        .setActivated(true)
        .setRoles(Set.of(new Role(1L, RoleName.USER)));
  }

  private void authentication(boolean withUser) {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    if (withUser) when(authentication.getName()).thenReturn("user");
  }

  @Test
  void confirmRegistration_invalidToken_forbidden() {
    assertThrows(ForbiddenException.class, () -> authService.confirmRegistration(""));
  }

  @Test
  void resendConfirmationEmail_userAlreadyActivated_forbidden() {
    User user = getUser();
    when(userRepository.findByUsernameOrEmail("user@localhost.com", "user@localhost.com"))
        .thenReturn(Optional.ofNullable(user));
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("user@localhost.com");
    assertThrows(
        ForbiddenException.class,
        () -> authService.resendConfirmationEmail(resendConfirmationEmailRequest));
  }

  @Test
  void resendConfirmationEmail_notFound() {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("notFound");
    assertThrows(
        ResourceNotFound.class,
        () -> authService.resendConfirmationEmail(resendConfirmationEmailRequest));
  }

  @Test
  void forgotPassword_notFound() {
    ForgotPasswordRequest forgotPasswordRequest =
        new ForgotPasswordRequest("notFound@localhost.com");
    assertThrows(ResourceNotFound.class, () -> authService.forgotPassword(forgotPasswordRequest));
  }

  @Test
  void resetPassword_invalidToken_forbidden() {
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest("", "qwerty123", "qwerty123");
    assertThrows(ForbiddenException.class, () -> authService.resetPassword(resetPasswordRequest));
  }

  @Test
  void logout_noAuth_userNotFound() {
    authentication(false);
    assertThrows(ResourceNotFound.class, () -> authService.logout());
  }
}
