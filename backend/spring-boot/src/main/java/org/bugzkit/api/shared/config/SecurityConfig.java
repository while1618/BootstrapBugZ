package org.bugzkit.api.shared.config;

import org.bugzkit.api.auth.security.JWTFilter;
import org.bugzkit.api.auth.security.UserDetailsServiceImpl;
import org.bugzkit.api.shared.constants.Path;
import org.bugzkit.api.shared.error.handling.CustomAuthenticationEntryPoint;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
  private static final String[] USERS_WHITELIST = {Path.USERS, Path.USERS + "/**"};
  private static final String[] SWAGGER_WHITELIST = {
    "/swagger-ui/**", "/v3/api-docs/**", "/openapi.yml"
  };
  private static final String[] ACTUATOR_WHITELIST = {"/actuator/**", "favicon.ico"};
  private final JWTFilter jwtFilter;
  private final UserDetailsServiceImpl userDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  public SecurityConfig(
      JWTFilter jwtFilter,
      UserDetailsServiceImpl userDetailsService,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
    this.jwtFilter = jwtFilter;
    this.userDetailsService = userDetailsService;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
  }

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(bCryptPasswordEncoder());
    return new ProviderManager(authProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(AUTH_WHITELIST)
                    .permitAll()
                    .requestMatchers(USERS_WHITELIST)
                    .permitAll()
                    .requestMatchers(SWAGGER_WHITELIST)
                    .permitAll()
                    .requestMatchers(ACTUATOR_WHITELIST)
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
        .cors(cors -> cors.configure(http))
        .build();
  }
}
