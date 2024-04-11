package org.bootstrapbugz.api.user.controller

import jakarta.validation.Valid
import org.bootstrapbugz.api.shared.constants.Path
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.payload.request.ChangePasswordRequest
import org.bootstrapbugz.api.user.payload.request.PatchProfileRequest
import org.bootstrapbugz.api.user.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Path.PROFILE)
class ProfileController(private val profileService: ProfileService) {
  @GetMapping
  fun find(): ResponseEntity<UserDTO> {
    return ResponseEntity.ok(profileService.find())
  }

  @PatchMapping
  fun patch(@Valid @RequestBody patchProfileRequest: PatchProfileRequest): ResponseEntity<UserDTO> {
    return ResponseEntity.ok(profileService.patch(patchProfileRequest))
  }

  @DeleteMapping
  fun delete(): ResponseEntity<Void> {
    profileService.delete()
    return ResponseEntity.noContent().build()
  }

  @PatchMapping("/password")
  fun changePassword(
    @Valid @RequestBody changePasswordRequest: ChangePasswordRequest
  ): ResponseEntity<Void> {
    profileService.changePassword(changePasswordRequest)
    return ResponseEntity.noContent().build()
  }
}
