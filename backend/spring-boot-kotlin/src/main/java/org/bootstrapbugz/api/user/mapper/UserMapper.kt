package org.bootstrapbugz.api.user.mapper

import org.bootstrapbugz.api.auth.security.UserPrincipal
import org.bootstrapbugz.api.user.model.Role
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.payload.dto.RoleDTO
import org.bootstrapbugz.api.user.payload.dto.UserDTO

object UserMapper {
    fun rolesToRoleDTOs(roles: Set<Role>): Set<RoleDTO> {
        return roles.map { role -> RoleDTO(role.name.name) }.toSet()
    }

    fun userToAdminUserDTO(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            active = user.active,
            lock = user.lock,
            email = user.email,
            createdAt = user.createdAt,
            roleDTOs = rolesToRoleDTOs(user.roles)
        )
    }

    fun userToProfileUserDTO(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            email = user.email,
            createdAt = user.createdAt,
            roleDTOs = setOf(),
            active = false,
            lock = false
        )
    }

    fun userPrincipalToProfileUserDTO(userPrincipal: UserPrincipal): UserDTO {
        return UserDTO(
            id = userPrincipal.id,
            username = userPrincipal.username,
            roleDTOs = setOf(),
            active = false,
            lock = false,
            email = null,
            firstName = null,
            lastName = null,
            createdAt = null
        )
    }

    fun userToSimpleUserDTO(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            createdAt = user.createdAt,
            roleDTOs = setOf(),
            active = false,
            lock = false,
            email = null,
        )
    }
}
