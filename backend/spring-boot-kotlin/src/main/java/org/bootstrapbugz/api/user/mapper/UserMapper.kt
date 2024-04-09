package org.bootstrapbugz.api.user.mapper

import org.bootstrapbugz.api.auth.security.UserPrincipal
import org.bootstrapbugz.api.user.model.Role
import org.bootstrapbugz.api.user.model.User
import org.bootstrapbugz.api.user.payload.dto.RoleDTO
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface UserMapper {
    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }

    fun rolesToRoleDTOs(roles: Set<Role>): Set<RoleDTO>

    @Mapping(source = "roles", target = "roleDTOs")
    fun userToAdminUserDTO(user: User): UserDTO

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lock", ignore = true)
    @Mapping(target = "roleDTOs", ignore = true)
    fun userToProfileUserDTO(user: User): UserDTO

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lock", ignore = true)
    @Mapping(target = "roleDTOs", ignore = true)
    fun userPrincipalToProfileUserDTO(userPrincipal: UserPrincipal): UserDTO

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "lock", ignore = true)
    @Mapping(target = "roleDTOs", ignore = true)
    fun userToSimpleUserDTO(user: User): UserDTO
}
