package org.bootstrapbugz.api.shared.config;

import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.security.AuthenticationFilter;
import org.bootstrapbugz.api.auth.security.AuthorizationFilter;
import org.bootstrapbugz.api.auth.security.user.details.CustomUserDetailsService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.handling.CustomAuthenticationEntryPoint;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private static final String[] USERS_WHITELIST = {Path.USERS, Path.USERS + "/**"};
  private static final String[] AUTH_WHITELIST = {
    Path.AUTH + "/sign-up",
    Path.AUTH + "/resend-confirmation-email",
    Path.AUTH + "/confirm-registration",
    Path.AUTH + "/sign-in",
    Path.AUTH + "/refresh-token",
    Path.AUTH + "/forgot-password",
    Path.AUTH + "/reset-password",
    Path.AUTH + "/username-availability",
    Path.AUTH + "/email-availability"
  };
  private final CustomUserDetailsService userDetailsService;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final MessageService messageService;

  public SecurityConfig(
      CustomUserDetailsService userDetailsService,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
      MessageService messageService) {
    this.userDetailsService = userDetailsService;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.messageService = messageService;
  }

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests()
        .requestMatchers(AUTH_WHITELIST)
        .permitAll()
        .requestMatchers(USERS_WHITELIST)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .apply(
            CustomDSL.customDsl(
                userDetailsService, accessTokenService, refreshTokenService, messageService))
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(customAuthenticationEntryPoint)
        .and()
        .cors()
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    return http.build();
  }
}

class CustomDSL extends AbstractHttpConfigurer<CustomDSL, HttpSecurity> {
  private final CustomUserDetailsService userDetailsService;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final MessageService messageService;

  private CustomDSL(
      CustomUserDetailsService userDetailsService,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      MessageService messageService) {
    this.userDetailsService = userDetailsService;
    this.accessTokenService = accessTokenService;
    this.refreshTokenService = refreshTokenService;
    this.messageService = messageService;
  }

  public static CustomDSL customDsl(
      CustomUserDetailsService userDetailsService,
      AccessTokenService accessTokenService,
      RefreshTokenService refreshTokenService,
      MessageService messageService) {
    return new CustomDSL(
        userDetailsService, accessTokenService, refreshTokenService, messageService);
  }

  @Override
  public void configure(HttpSecurity http) {
    final var authenticationManager = http.getSharedObject(AuthenticationManager.class);
    http.addFilter(
        new AuthenticationFilter(
            authenticationManager, accessTokenService, refreshTokenService, messageService));
    http.addFilter(
        new AuthorizationFilter(authenticationManager, accessTokenService, userDetailsService));
  }
}
