package org.bootstrapbugz.api.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.payload.dto.SignInDTO;
import org.bootstrapbugz.api.auth.payload.request.SignInRequest;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.error.handling.CustomFilterExceptionHandler;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
              signInRequest.usernameOrEmail(), signInRequest.password(), new ArrayList<>());
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
    final var userDTO =
        UserMapper.INSTANCE.userPrincipalToUserDTO((UserPrincipal) auth.getPrincipal());
    final var ipAddress = AuthUtil.getUserIpAddress(request);
    final var accessToken = accessTokenService.create(userDTO.id(), userDTO.roleDTOs());
    final var refreshToken = getRefreshToken(userDTO.id(), userDTO.roleDTOs(), ipAddress);
    final var signInDTO =
        SignInDTO.builder()
            .accessToken(JwtUtil.addBearer(accessToken))
            .refreshToken(JwtUtil.addBearer(refreshToken))
            .userDTO(userDTO)
            .build();
    writeToResponse(response, signInDTO);
  }

  private String getRefreshToken(Long userId, Set<RoleDTO> roleDTOs, String ipAddress) {
    final var refreshToken = refreshTokenService.findByUserAndIpAddress(userId, ipAddress);
    if (refreshToken == null) return refreshTokenService.create(userId, roleDTOs, ipAddress);
    return refreshToken;
  }

  private void writeToResponse(HttpServletResponse response, SignInDTO signInDTO)
      throws IOException {
    final var jsonMapper =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
            .build();
    final var json = jsonMapper.writeValueAsString(signInDTO);
    final var out = response.getWriter();
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    out.print(json);
    out.flush();
  }
}
