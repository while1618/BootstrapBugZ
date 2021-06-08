package org.bootstrapbugz.api.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bootstrapbugz.api.auth.dto.LoginDto;
import org.bootstrapbugz.api.auth.request.LoginRequest;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.shared.error.handling.CustomFilterExceptionHandler;
import org.bootstrapbugz.api.user.dto.UserDto;
import org.bootstrapbugz.api.user.dto.UserDto.RoleDto;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
  private final MessageSource messageSource;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      JwtService jwtService,
      MessageSource messageSource) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.messageSource = messageSource;
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
    if (e instanceof DisabledException)
      return messageSource.getMessage("user.notActivated", null, LocaleContextHolder.getLocale());
    else if (e instanceof LockedException)
      return messageSource.getMessage("user.locked", null, LocaleContextHolder.getLocale());
    else return messageSource.getMessage("login.invalid", null, LocaleContextHolder.getLocale());
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication auth)
      throws IOException {
    final UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
    final LoginDto loginDto =
        new LoginDto()
            .setToken(
                JwtUtil.BEARER
                    + jwtService.createToken(
                        userPrincipal.getUsername(), JwtPurpose.ACCESSING_RESOURCES))
            .setRefreshToken(jwtService.createRefreshToken(userPrincipal.getUsername()))
            .setUser(userPrincipalToUserDto(userPrincipal));
    writeToResponse(response, loginDto);
  }

  private UserDto userPrincipalToUserDto(UserPrincipal userPrincipal) {
    return new UserDto()
        .setId(userPrincipal.getId())
        .setFirstName(userPrincipal.getFirstName())
        .setLastName(userPrincipal.getLastName())
        .setUsername(userPrincipal.getUsername())
        .setEmail(userPrincipal.getEmail())
        .setActivated(userPrincipal.isEnabled())
        .setNonLocked(userPrincipal.isAccountNonLocked())
        .setRoles(
            userPrincipal.getAuthorities().stream()
                .map(authority -> new RoleDto(authority.getAuthority()))
                .collect(Collectors.toSet()));
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
