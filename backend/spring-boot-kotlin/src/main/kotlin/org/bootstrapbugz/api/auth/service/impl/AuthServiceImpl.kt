package org.bootstrapbugz.api.auth.service.impl

import java.lang.Boolean
import kotlin.RuntimeException
import kotlin.String
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService
import org.bootstrapbugz.api.auth.jwt.service.ResetPasswordTokenService
import org.bootstrapbugz.api.auth.jwt.service.VerificationTokenService
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil
import org.bootstrapbugz.api.auth.payload.dto.AuthTokensDTO
import org.bootstrapbugz.api.auth.payload.request.AuthTokensRequest
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest
import org.bootstrapbugz.api.auth.payload.request.RegisterUserRequest
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest
import org.bootstrapbugz.api.auth.payload.request.VerificationEmailRequest
import org.bootstrapbugz.api.auth.payload.request.VerifyEmailRequest
import org.bootstrapbugz.api.auth.service.AuthService
import org.bootstrapbugz.api.auth.util.AuthUtil
import org.bootstrapbugz.api.shared.error.exception.BadRequestException
import org.bootstrapbugz.api.shared.error.exception.ConflictException
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.mapper.UserMapper
import org.bootstrapbugz.api.user.model.Role.RoleName
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.repository.RoleRepository
import org.bootstrapbugz.api.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
  private val userRepository: UserRepository,
  private val roleRepository: RoleRepository,
  private val messageService: MessageService,
  private val bCryptPasswordEncoder: PasswordEncoder,
  private val authenticationManager: AuthenticationManager,
  private val accessTokenService: AccessTokenService,
  private val refreshTokenService: RefreshTokenService,
  private val verificationTokenService: VerificationTokenService,
  private val resetPasswordTokenService: ResetPasswordTokenService
) : AuthService {
  override fun register(registerUserRequest: RegisterUserRequest): UserDTO {
    if (userRepository.existsByUsername(registerUserRequest.username))
      throw ConflictException(messageService.getMessage("username.exists"))
    if (userRepository.existsByEmail(registerUserRequest.email))
      throw ConflictException(messageService.getMessage("email.exists"))
    val user = userRepository.save(createUser(registerUserRequest))
    val token = verificationTokenService.create(user.id)
    verificationTokenService.sendToEmail(user, token)
    return UserMapper.userToProfileUserDTO(user)
  }

  override fun authenticate(
    authTokensRequest: AuthTokensRequest,
    ipAddress: String
  ): AuthTokensDTO {
    val auth =
      UsernamePasswordAuthenticationToken(
        authTokensRequest.usernameOrEmail,
        authTokensRequest.password,
        ArrayList()
      )
    authenticationManager.authenticate(auth)
    val user =
      userRepository.findWithRolesByUsername(auth.name).orElseThrow {
        UnauthorizedException(messageService.getMessage("auth.invalid"))
      }
    val roleDTOs = UserMapper.rolesToRoleDTOs(user.roles)
    val accessToken = accessTokenService.create(user.id, roleDTOs)
    val refreshToken =
      refreshTokenService
        .findByUserIdAndIpAddress(user.id, ipAddress)
        .orElse(refreshTokenService.create(user.id, roleDTOs, ipAddress))
    return AuthTokensDTO(accessToken, refreshToken)
  }

  private fun createUser(registerUserRequest: RegisterUserRequest): User {
    val roles =
      roleRepository.findByName(RoleName.USER).orElseThrow {
        RuntimeException(messageService.getMessage("role.notFound"))
      }
    return User(
      firstName = registerUserRequest.firstName,
      lastName = registerUserRequest.lastName,
      username = registerUserRequest.username,
      email = registerUserRequest.email,
      password = bCryptPasswordEncoder.encode(registerUserRequest.password),
      roles = setOf(roles)
    )
  }

  override fun deleteTokens(accessToken: String, ipAddress: String) {
    if (!AuthUtil.isSignedIn()) return
    val id = AuthUtil.findSignedInUser().id
    refreshTokenService.deleteByUserIdAndIpAddress(id, ipAddress)
    accessTokenService.invalidate(accessToken)
  }

  override fun deleteTokensOnAllDevices() {
    if (!AuthUtil.isSignedIn()) return
    val id = AuthUtil.findSignedInUser().id
    refreshTokenService.deleteAllByUserId(id)
    accessTokenService.invalidateAllByUserId(id)
  }

  override fun refreshTokens(refreshToken: String, ipAddress: String): AuthTokensDTO {
    refreshTokenService.check(refreshToken)
    val userId = JwtUtil.getUserId(refreshToken)
    val roleDTOs = JwtUtil.getRoleDTOs(refreshToken)
    refreshTokenService.delete(refreshToken)
    val newAccessToken = accessTokenService.create(userId, roleDTOs)
    val newRefreshToken = refreshTokenService.create(userId, roleDTOs, ipAddress)
    return AuthTokensDTO(newAccessToken, newRefreshToken)
  }

  override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
    val user =
      userRepository.findByEmail(forgotPasswordRequest.email).orElseThrow {
        ResourceNotFoundException(messageService.getMessage("user.notFound"))
      }
    val token = resetPasswordTokenService.create(user.id)
    resetPasswordTokenService.sendToEmail(user, token)
  }

  override fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
    val user =
      userRepository.findById(JwtUtil.getUserId(resetPasswordRequest.token)).orElseThrow {
        BadRequestException("token", messageService.getMessage("token.invalid"))
      }
    resetPasswordTokenService.check(resetPasswordRequest.token!!)
    user.password = bCryptPasswordEncoder.encode(resetPasswordRequest.password)
    accessTokenService.invalidateAllByUserId(user.id)
    refreshTokenService.deleteAllByUserId(user.id)
    userRepository.save(user)
  }

  override fun sendVerificationMail(request: VerificationEmailRequest) {
    val user =
      userRepository
        .findByUsernameOrEmail(request.usernameOrEmail, request.usernameOrEmail)
        .orElseThrow { ResourceNotFoundException(messageService.getMessage("user.notFound")) }
    if (Boolean.TRUE == user.active)
      throw ConflictException(messageService.getMessage("user.active"))
    val token = verificationTokenService.create(user.id)
    verificationTokenService.sendToEmail(user, token)
  }

  override fun verifyEmail(verifyEmailRequest: VerifyEmailRequest) {
    val user =
      userRepository.findById(JwtUtil.getUserId(verifyEmailRequest.token)).orElseThrow {
        BadRequestException("token", messageService.getMessage("token.invalid"))
      }
    if (Boolean.TRUE == user.active)
      throw ConflictException(messageService.getMessage("user.active"))
    verificationTokenService.check(verifyEmailRequest.token)
    user.active = true
    userRepository.save(user)
  }
}
