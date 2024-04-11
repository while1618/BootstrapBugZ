package org.bootstrapbugz.api.shared.config

import org.bootstrapbugz.api.auth.security.JWTFilter
import org.bootstrapbugz.api.auth.security.UserDetailsServiceImpl
import org.bootstrapbugz.api.shared.constants.Path
import org.bootstrapbugz.api.shared.error.handling.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
  private val jwtFilter: JWTFilter,
  private val userDetailsService: UserDetailsServiceImpl,
  private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) {

  companion object {
    private val AUTH_WHITELIST =
      arrayOf(
        Path.AUTH + "/register",
        Path.AUTH + "/tokens",
        Path.AUTH + "/tokens/devices",
        Path.AUTH + "/tokens/refresh",
        Path.AUTH + "/password/forgot",
        Path.AUTH + "/password/reset",
        Path.AUTH + "/verification-email",
        Path.AUTH + "/verify-email"
      )
    private val USERS_WHITELIST = arrayOf(Path.USERS, Path.USERS + "/**")
    private val SWAGGER_WHITELIST = arrayOf("/swagger-ui/**", "/v3/api-docs/**", "/openapi.yml")
  }

  @Bean fun bCryptPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun authenticationManager(): AuthenticationManager {
    val authProvider =
      DaoAuthenticationProvider().apply {
        setUserDetailsService(userDetailsService)
        setPasswordEncoder(bCryptPasswordEncoder())
      }
    return ProviderManager(authProvider)
  }

  @Bean
  fun filterChain(http: HttpSecurity): SecurityFilterChain =
    http
      .csrf { it.disable() }
      .authorizeHttpRequests {
        it
          .requestMatchers(*AUTH_WHITELIST)
          .permitAll()
          .requestMatchers(*USERS_WHITELIST)
          .permitAll()
          .requestMatchers(*SWAGGER_WHITELIST)
          .permitAll()
          .anyRequest()
          .authenticated()
      }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
      .exceptionHandling { it.authenticationEntryPoint(customAuthenticationEntryPoint) }
      .cors { it.configure(http) }
      .build()
}
