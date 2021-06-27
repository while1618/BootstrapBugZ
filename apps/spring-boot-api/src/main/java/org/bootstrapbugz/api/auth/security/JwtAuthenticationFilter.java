package org.bootstrapbugz.api.auth.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.response.LoginResponse;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.handling.CustomFilterExceptionHandler;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserMapper userMapper;
  private final MessageService messageService;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      UserMapper userMapper,
      MessageService messageService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userMapper = userMapper;
    this.messageService = messageService;
    this.setFilterProcessesUrl(Path.AUTH + "/login");
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      LoginRequest loginRequest =
          new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
              loginRequest.getUsernameOrEmail(), loginRequest.getPassword(), new ArrayList<>());
      return authenticationManager.authenticate(authenticationToken);
    } catch (IOException | AuthenticationException | ResourceNotFoundException e) {
      CustomFilterExceptionHandler.handleException(response, getMessageBasedOnException(e));
    }
    return null;
  }

  private String getMessageBasedOnException(Exception e) {
    if (e instanceof DisabledException) return messageService.getMessage("user.notActivated");
    else if (e instanceof LockedException) return messageService.getMessage("user.locked");
    else return messageService.getMessage("login.invalid");
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication auth)
      throws IOException {
    User user = AuthUtil.userPrincipalToUser((UserPrincipal) auth.getPrincipal());
    String ipAddress = AuthUtil.getUserIpAddress(request);
    String refreshToken = jwtService.findRefreshToken(user.getUsername(), ipAddress);
    if (refreshToken == null)
      refreshToken = jwtService.createRefreshToken(user.getUsername(), ipAddress);
    final LoginResponse loginResponse =
        new LoginResponse()
            .setToken(jwtService.createToken(user.getUsername(), JwtPurpose.ACCESSING_RESOURCES))
            .setRefreshToken(refreshToken)
            .setUser(userMapper.userToUserResponse(user));
    writeToResponse(response, loginResponse);
  }

  private void writeToResponse(HttpServletResponse response, LoginResponse loginResponse)
      throws IOException {
    final String jsonLoginResponse = new Gson().toJson(loginResponse);
    PrintWriter out = response.getWriter();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    out.print(jsonLoginResponse);
    out.flush();
  }
}
