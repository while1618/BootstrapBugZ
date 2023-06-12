package org.bootstrapbugz.api.shared.config;

import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService;
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService;
import org.bootstrapbugz.api.auth.security.AuthenticationFilter;
import org.bootstrapbugz.api.auth.security.JWTFilter;
import org.bootstrapbugz.api.auth.security.user.details.ExtendedUserDetailsService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.error.handling.CustomAuthenticationEntryPoint;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
  private static final String[] AUTH_WHITELIST = {
    Path.AUTH + "/register",
    Path.AUTH + "/tokens",
    Path.AUTH + "/tokens/devices",
    Path.AUTH + "/tokens/refresh",
    Path.AUTH + "/password/forgot",
    Path.AUTH + "/password/reset",
    Path.AUTH + "/verification-email",
    Path.AUTH + "/verify-email",
  };
  private static final String[] USERS_WHITELIST = {
    Path.USERS, Path.USERS + "/**", Path.USERS + "/username/**"
  };
  private static final String[] SWAGGER_WHITELIST = {
    "/swagger-ui/**", "/v3/api-docs/**", "/openapi.yml"
  };
  private final ExtendedUserDetailsService userDetailsService;
  private final AccessTokenService accessTokenService;
  private final RefreshTokenService refreshTokenService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final MessageService messageService;

  public SecurityConfig(
      ExtendedUserDetailsService userDetailsService,
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
  public AuthenticationManager authManager() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(bCryptPasswordEncoder());
    return new ProviderManager(authProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers(AUTH_WHITELIST)
                    .permitAll()
                    .requestMatchers(USERS_WHITELIST)
                    .permitAll()
                    .requestMatchers(SWAGGER_WHITELIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilter(
            new AuthenticationFilter(
                authManager(), accessTokenService, refreshTokenService, messageService))
        .addFilter(new JWTFilter(authManager(), accessTokenService, userDetailsService))
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .cors(cors -> cors.configure(http))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }
}
