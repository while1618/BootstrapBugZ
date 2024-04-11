package org.bootstrapbugz.api.user.service

import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest
import org.bootstrapbugz.api.user.payload.request.PatchProfileRequest

interface ProfileService {
    fun find(): UserDTO
    fun patch(patchProfileRequest: PatchProfileRequest): UserDTO
    fun delete()
    fun changePassword(changePasswordRequest: ChangePasswordRequest)
}
