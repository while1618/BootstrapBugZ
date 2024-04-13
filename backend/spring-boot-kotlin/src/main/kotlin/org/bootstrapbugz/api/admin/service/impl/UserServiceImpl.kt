package org.bootstrapbugz.api.admin.service.impl

import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest
import org.bootstrapbugz.api.admin.payload.request.UserRequest
import org.bootstrapbugz.api.admin.service.UserService
import org.bootstrapbugz.api.auth.jwt.service.AccessTokenService
import org.bootstrapbugz.api.auth.jwt.service.RefreshTokenService
import org.bootstrapbugz.api.shared.error.exception.ConflictException
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException
import org.bootstrapbugz.api.shared.message.service.MessageService
import org.bootstrapbugz.api.user.mapper.UserMapper
import org.bootstrapbugz.api.user.model.Role.RoleName
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.repository.RoleRepository
import org.bootstrapbugz.api.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service("adminUserService")
@PreAuthorize("hasAuthority('ADMIN')")
class UserServiceImpl(
  private val userRepository: UserRepository,
  private val roleRepository: RoleRepository,
  private val accessTokenService: AccessTokenService,
  private val refreshTokenService: RefreshTokenService,
  private val bCryptPasswordEncoder: PasswordEncoder,
  private val messageService: MessageService
) : UserService {
  override fun create(request: UserRequest): UserDTO {
    if (userRepository.existsByUsername(request.username))
      throw ConflictException(messageService.getMessage("username.exists"))
    if (userRepository.existsByEmail(request.email))
      throw ConflictException(messageService.getMessage("email.exists"))

    val user =
      User(
        firstName = request.firstName,
        lastName = request.lastName,
        username = request.username,
        email = request.email,
        password = bCryptPasswordEncoder.encode(request.password),
        active = request.active,
        lock = request.lock,
        roles = roleRepository.findAllByNameIn(request.roleNames).toSet()
      )

    return UserMapper.userToAdminUserDTO(userRepository.save(user))
  }

  override fun findAll(pageable: Pageable): List<UserDTO> {
    return userRepository.findAll(pageable).stream().map(UserMapper::userToAdminUserDTO).toList()
  }

  override fun findById(id: Long): UserDTO {
    return userRepository.findWithRolesById(id).map(UserMapper::userToAdminUserDTO).orElseThrow {
      ResourceNotFoundException(messageService.getMessage("user.notFound"))
    }
  }

  override fun update(id: Long, request: UserRequest): UserDTO {
    val user =
      userRepository.findWithRolesById(id).orElseThrow {
        NoSuchElementException("No user found with ID $id")
      }
    user.apply {
      firstName = request.firstName
      lastName = request.lastName
      setUsername(this, request.username)
      setEmail(this, request.email)
      setPassword(this, request.password)
      setActive(this, request.active)
      setLock(this, request.lock)
      setRoles(this, request.roleNames)
    }
    return UserMapper.userToAdminUserDTO(userRepository.save(user))
  }

  override fun patch(id: Long, patchUserRequest: PatchUserRequest): UserDTO {
    val user =
      userRepository.findWithRolesById(id).orElseThrow {
        ResourceNotFoundException(messageService.getMessage("user.notFound"))
      }
    user.apply {
      patchUserRequest.firstName?.let { firstName = it }
      patchUserRequest.lastName?.let { lastName = it }
      patchUserRequest.username?.let { setUsername(this, it) }
      patchUserRequest.email?.let { setEmail(this, it) }
      patchUserRequest.password?.let { setPassword(this, it) }
      patchUserRequest.active?.let { setActive(this, it) }
      patchUserRequest.lock?.let { setLock(this, it) }
      patchUserRequest.roleNames?.let { setRoles(this, it) }
    }
    return UserMapper.userToAdminUserDTO(userRepository.save(user))
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

  private fun setEmail(user: User, email: String?) {
    if (user.email == email) return
    if (userRepository.existsByEmail(email!!))
      throw ConflictException("email", messageService.getMessage("email.exists"))
    user.email = email
  }

  private fun setPassword(user: User, password: String?) {
    if (bCryptPasswordEncoder.matches(password, user.password)) return
    user.password = bCryptPasswordEncoder.encode(password)
    deleteAuthTokens(user.id)
  }

  private fun setActive(user: User, active: Boolean) {
    if (user.active == active) return
    user.active = active
    if (!active) deleteAuthTokens(user.id)
  }

  private fun setLock(user: User, lock: Boolean) {
    if (user.lock == lock) return
    user.lock = lock
    if (lock) deleteAuthTokens(user.id)
  }

  private fun setRoles(user: User, roleNames: Set<RoleName>) {
    val roles = roleRepository.findAllByNameIn(roleNames).toSet()
    if (user.roles == roles) return
    user.roles = roles
    deleteAuthTokens(user.id)
  }

  override fun delete(id: Long) {
    deleteAuthTokens(id)
    userRepository.deleteById(id)
  }
}
