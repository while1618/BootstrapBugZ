package org.bootstrapbugz.api.admin.service

import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest
import org.bootstrapbugz.api.admin.payload.request.UserRequest
import org.bootstrapbugz.api.shared.generic.crud.CrudService
import org.bootstrapbugz.api.user.payload.dto.UserDTO

interface UserService : CrudService<UserDTO, UserRequest> {
    fun patch(id: Long?, patchUserRequest: PatchUserRequest): UserDTO
}
