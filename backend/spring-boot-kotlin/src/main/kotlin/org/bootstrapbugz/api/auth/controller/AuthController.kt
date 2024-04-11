package org.bootstrapbugz.api.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil
import org.bootstrapbugz.api.auth.payload.dto.AuthTokensDTO
import org.bootstrapbugz.api.auth.payload.request.AuthTokensRequest
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest
import org.bootstrapbugz.api.auth.payload.request.RefreshAuthTokensRequest
import org.bootstrapbugz.api.auth.payload.request.RegisterUserRequest
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest
import org.bootstrapbugz.api.auth.payload.request.VerificationEmailRequest
import org.bootstrapbugz.api.auth.payload.request.VerifyEmailRequest
import org.bootstrapbugz.api.auth.service.AuthService
import org.bootstrapbugz.api.auth.util.AuthUtil
import org.bootstrapbugz.api.shared.constants.Path
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Path.AUTH)
class AuthController(private val authService: AuthService) {
  @PostMapping("/register")
  fun register(
    @Valid @RequestBody registerUserRequest: RegisterUserRequest
  ): ResponseEntity<UserDTO> {
    return ResponseEntity(authService.register(registerUserRequest), HttpStatus.CREATED)
  }

  @PostMapping("/tokens")
  fun authenticate(
    @Valid @RequestBody authTokensRequest: AuthTokensRequest,
    request: HttpServletRequest
  ): ResponseEntity<AuthTokensDTO> {
    val ipAddress = AuthUtil.getUserIpAddress(request)
    return ResponseEntity.ok(authService.authenticate(authTokensRequest, ipAddress))
  }

  @DeleteMapping("/tokens")
  fun deleteTokens(request: HttpServletRequest): ResponseEntity<Void> {
    val accessToken = JwtUtil.removeBearer(AuthUtil.getAccessTokenFromRequest(request))
    val ipAddress = AuthUtil.getUserIpAddress(request)
    authService.deleteTokens(accessToken, ipAddress)
    return ResponseEntity.noContent().build()
  }

  @DeleteMapping("/tokens/devices")
  fun deleteTokensOnAllDevices(): ResponseEntity<Void> {
    authService.deleteTokensOnAllDevices()
    return ResponseEntity.noContent().build()
  }

  @PostMapping("/tokens/refresh")
  fun refreshTokens(
    @Valid @RequestBody refreshAuthTokensRequest: RefreshAuthTokensRequest,
    request: HttpServletRequest
  ): ResponseEntity<AuthTokensDTO> {
    val refreshToken = JwtUtil.removeBearer(refreshAuthTokensRequest.refreshToken)
    val ipAddress = AuthUtil.getUserIpAddress(request)
    return ResponseEntity.ok(authService.refreshTokens(refreshToken, ipAddress))
  }

  @PostMapping("/password/forgot")
  fun forgotPassword(
    @Valid @RequestBody forgotPasswordRequest: ForgotPasswordRequest
  ): ResponseEntity<Void> {
    authService.forgotPassword(forgotPasswordRequest)
    return ResponseEntity.noContent().build()
  }

  @PostMapping("/password/reset")
  fun resetPassword(
    @Valid @RequestBody resetPasswordRequest: ResetPasswordRequest
  ): ResponseEntity<Void> {
    authService.resetPassword(resetPasswordRequest)
    return ResponseEntity.noContent().build()
  }

  @PostMapping("/verification-email")
  fun sendVerificationMail(
    @Valid @RequestBody verificationEmailRequest: VerificationEmailRequest
  ): ResponseEntity<Void> {
    authService.sendVerificationMail(verificationEmailRequest)
    return ResponseEntity.noContent().build()
  }

  @PostMapping("/verify-email")
  fun verifyEmail(
    @Valid @RequestBody verifyEmailRequest: VerifyEmailRequest
  ): ResponseEntity<Void> {
    authService.verifyEmail(verifyEmailRequest)
    return ResponseEntity.noContent().build()
  }
}
