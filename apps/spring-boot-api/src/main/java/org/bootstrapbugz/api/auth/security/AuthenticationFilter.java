package org.bootstrapbugz.api.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.payload.response.SignInResponse;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.handling.CustomFilterExceptionHandler;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final UserMapper userMapper;
  private final MessageService messageService;

  public AuthenticationFilter(
      AuthenticationManager authenticationManager,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      UserMapper userMapper,
      MessageService messageService) {
    this.authenticationManager = authenticationManager;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.userMapper = userMapper;
    this.messageService = messageService;
    this.setFilterProcessesUrl(Path.AUTH + "/sign-in");
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      final var signInRequest =
          new ObjectMapper().readValue(request.getInputStream(), SignInRequest.class);
      final var authToken =
          new UsernamePasswordAuthenticationToken(
              signInRequest.getUsernameOrEmail(), signInRequest.getPassword(), new ArrayList<>());
      return authenticationManager.authenticate(authToken);
    } catch (IOException | AuthenticationException | ResourceNotFoundException e) {
      handleException(response, e);
    }
    return null;
  }

  private void handleException(HttpServletResponse response, Exception e) {
    if (e instanceof DisabledException)
      CustomFilterExceptionHandler.handleException(
          response, messageService.getMessage("user.notActivated"), HttpStatus.FORBIDDEN);
    else if (e instanceof LockedException)
      CustomFilterExceptionHandler.handleException(
          response, messageService.getMessage("user.locked"), HttpStatus.FORBIDDEN);
    else
      CustomFilterExceptionHandler.handleException(
          response, messageService.getMessage("signIn.invalid"), HttpStatus.UNAUTHORIZED);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication auth)
      throws IOException {
    final var user = AuthUtil.userPrincipalToUser((UserPrincipal) auth.getPrincipal());
    final String ipAddress = AuthUtil.getUserIpAddress(request);
    final String accessToken = accessTokenService.create(user.getId(), user.getRoles());
    final String refreshToken = findRefreshToken(user.getId(), user.getRoles(), ipAddress);
    final var signInResponse =
        new SignInResponse()
            .setAccessToken(JwtUtil.addBearer(accessToken))
            .setRefreshToken(JwtUtil.addBearer(refreshToken))
            .setUser(userMapper.userToUserResponse(user));
    writeToResponse(response, signInResponse);
  }

  private String findRefreshToken(Long userId, Set<Role> roles, String ipAddress) {
    final String refreshToken = refreshTokenService.findByUserAndIpAddress(userId, ipAddress);
    if (refreshToken == null) return refreshTokenService.create(userId, roles, ipAddress);
    return refreshToken;
  }

  private void writeToResponse(HttpServletResponse response, SignInResponse signInResponse)
      throws IOException {
    final String jsonSignInResponse = new Gson().toJson(signInResponse);
    final var out = response.getWriter();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    out.print(jsonSignInResponse);
    out.flush();
  }
}
