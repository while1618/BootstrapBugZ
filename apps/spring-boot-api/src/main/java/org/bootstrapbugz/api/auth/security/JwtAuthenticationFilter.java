package org.bootstrapbugz.api.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bootstrapbugz.api.auth.dto.LoginDto;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.shared.error.handling.CustomFilterExceptionHandler;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
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
    } catch (IOException | AuthenticationException | ResourceNotFound e) {
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
    final LoginDto loginDto =
        new LoginDto()
            .setToken(jwtService.createToken(user.getUsername(), JwtPurpose.ACCESSING_RESOURCES))
            .setRefreshToken(refreshToken)
            .setUser(userMapper.userToUserDto(user));
    writeToResponse(response, loginDto);
  }

  private void writeToResponse(HttpServletResponse response, LoginDto loginDto) throws IOException {
    final String jwtDtoJson = new Gson().toJson(loginDto);
    PrintWriter out = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    out.print(jwtDtoJson);
    out.flush();
  }
}
