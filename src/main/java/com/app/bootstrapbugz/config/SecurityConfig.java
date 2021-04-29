package com.app.bootstrapbugz.config;

import com.app.bootstrapbugz.common.constants.Path;
import com.app.bootstrapbugz.common.error.handling.CustomAuthenticationEntryPoint;
import com.app.bootstrapbugz.jwt.security.JwtAuthenticationFilter;
import com.app.bootstrapbugz.jwt.security.JwtAuthorizationFilter;
import com.app.bootstrapbugz.jwt.util.JwtUtilities;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String[] SWAGGER_WHITELIST = {
    "/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs", "/webjars/**"
  };
  private static final String[] AUTH_WHITELIST = {
    Path.AUTH + "/login",
    Path.AUTH + "/sign-up",
    Path.AUTH + "/confirm-registration",
    Path.AUTH + "/resend-confirmation-email",
    Path.AUTH + "/forgot-password",
    Path.AUTH + "/reset-password"
  };
  private final UserDetailsService userDetailsService;
  private final JwtUtilities jwtUtilities;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final MessageSource messageSource;

  public SecurityConfig(
      @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
      JwtUtilities jwtUtilities,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
      MessageSource messageSource) {
    this.userDetailsService = userDetailsService;
    this.jwtUtilities = jwtUtilities;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.messageSource = messageSource;
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
            new JwtAuthenticationFilter(authenticationManager(), jwtUtilities, messageSource))
        .addFilter(
            new JwtAuthorizationFilter(authenticationManager(), jwtUtilities, userDetailsService))
        .exceptionHandling()
        .authenticationEntryPoint(customAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
        .antMatchers(SWAGGER_WHITELIST)
        .permitAll()
        .antMatchers(AUTH_WHITELIST)
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
