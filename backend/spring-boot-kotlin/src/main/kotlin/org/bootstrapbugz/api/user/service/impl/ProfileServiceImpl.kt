package org.bootstrapbugz.api.user.service.impl

import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService
import org.bootstrapbugz.api.auth.jwt.service.VerificationTokenService
import org.bootstrapbugz.api.auth.util.AuthUtil
import org.bootstrapbugz.api.shared.error.exception.BadRequestException
import org.bootstrapbugz.api.shared.error.exception.ConflictException
import org.bootstrapbugz.api.shared.error.exception.UnauthorizedException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.mapper.UserMapper
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest
import org.bootstrapbugz.api.user.payload.request.PatchProfileRequest
import org.bootstrapbugz.api.user.repository.UserRepository
import org.bootstrapbugz.api.user.service.ProfileService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ProfileServiceImpl(
  private val userRepository: UserRepository,
  private val messageService: MessageService,
  private val bCryptPasswordEncoder: PasswordEncoder,
  private val accessTokenService: AccessTokenService,
  private val refreshTokenService: RefreshTokenService,
  private val verificationTokenService: VerificationTokenService
) : ProfileService {
  override fun find(): UserDTO {
    return UserMapper.userPrincipalToProfileUserDTO(AuthUtil.findSignedInUser())
  }

  override fun patch(patchProfileRequest: PatchProfileRequest): UserDTO {
    val userId = AuthUtil.findSignedInUser().id
    val user =
      userRepository.findById(userId).orElseThrow {
        UnauthorizedException(messageService.getMessage("token.invalid"))
      }
    patchProfileRequest.firstName?.let { user.firstName = it }
    patchProfileRequest.lastName?.let { user.lastName = it }
    patchProfileRequest.username?.let { setUsername(user, it) }
    patchProfileRequest.email?.let { setEmail(user, it) }
    return UserMapper.userToProfileUserDTO(userRepository.save(user))
  }

  private fun deleteAuthTokens(userId: Long?) {
    accessTokenService.invalidateAllByUserId(userId)
    refreshTokenService.deleteAllByUserId(userId)
  }

  private fun setUsername(user: User, username: String) {
    if (user.username == username) return
    if (userRepository.existsByUsername(username))
      throw ConflictException("username", messageService.getMessage("username.exists"))
    user.username = username
  }

  private fun setEmail(user: User, email: String) {
    if (user.email == email) return
    if (userRepository.existsByEmail(email))
      throw ConflictException("email", messageService.getMessage("email.exists"))
    user.email = email
    user.active = false
    deleteAuthTokens(user.id)
    val token = verificationTokenService.create(user.id)
    verificationTokenService.sendToEmail(user, token)
  }

  override fun delete() {
    val userId = AuthUtil.findSignedInUser().id
    deleteAuthTokens(userId)
    userRepository.deleteById(userId)
  }

  override fun changePassword(changePasswordRequest: ChangePasswordRequest) {
    val userId = AuthUtil.findSignedInUser().id
    val user =
      userRepository.findById(userId).orElseThrow {
        UnauthorizedException(messageService.getMessage("token.invalid"))
      }
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.currentPassword, user.password))
      throw BadRequestException(
        "currentPassword",
        messageService.getMessage("currentPassword.wrong")
      )
    user.password = bCryptPasswordEncoder.encode(changePasswordRequest.newPassword)
    deleteAuthTokens(userId)
    userRepository.save(user)
  }
}
