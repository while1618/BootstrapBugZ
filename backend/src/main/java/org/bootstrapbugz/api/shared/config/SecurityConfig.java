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
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
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

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilter(
            new AuthenticationFilter(
                authenticationManager(), accessTokenService, refreshTokenService, messageService))
        .addFilter(
            new AuthorizationFilter(
                authenticationManager(), accessTokenService, userDetailsService))
        .exceptionHandling()
        .authenticationEntryPoint(customAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
        .antMatchers(AUTH_WHITELIST)
        .permitAll()
        .antMatchers(USERS_WHITELIST)
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
