package org.bootstrapbugz.api.auth.security;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.payload.dto.AuthTokensDTO;
import org.bootstrapbugz.api.auth.payload.request.AuthTokensRequest;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.handling.CustomFilterExceptionHandler;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final MessageService messageService;

  public AuthenticationFilter(
      AuthenticationManager authenticationManager,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      MessageService messageService) {
    this.authenticationManager = authenticationManager;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.messageService = messageService;
    this.setFilterProcessesUrl(Path.AUTH + "/tokens");
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      final var authTokensRequest =
          JsonMapper.builder().build().readValue(request.getInputStream(), AuthTokensRequest.class);
      final var authToken =
          new UsernamePasswordAuthenticationToken(
              authTokensRequest.usernameOrEmail(), authTokensRequest.password(), new ArrayList<>());
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
    final var userPrincipal = (UserPrincipal) auth.getPrincipal();
    final var roleDTOs = authoritiesToRoleDTOs(userPrincipal.getAuthorities());
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var accessToken = accessTokenService.create(userPrincipal.getId(), roleDTOs);
    final var refreshToken = getRefreshToken(userPrincipal.getId(), roleDTOs, ipAddress);
    final var authTokensDTO =
        AuthTokensDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    writeToResponse(response, authTokensDTO);
  }

  private Set<RoleDTO> authoritiesToRoleDTOs(Collection<? extends GrantedAuthority> authorities) {
    return authorities.stream()
        .map(authority -> new RoleDTO(RoleName.valueOf(authority.getAuthority()).name()))
        .collect(Collectors.toSet());
  }

  private String getRefreshToken(Long userId, Set<RoleDTO> roleDTOs, String ipAddress) {
    return refreshTokenService
        .findByUserIdAndIpAddress(userId, ipAddress)
        .orElse(refreshTokenService.create(userId, roleDTOs, ipAddress));
  }

  private void writeToResponse(HttpServletResponse response, AuthTokensDTO authTokensDTO)
      throws IOException {
    final var json = new GsonBuilder().setPrettyPrinting().create().toJson(authTokensDTO);
    final var out = response.getWriter();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    out.print(json);
    out.flush();
  }
}
