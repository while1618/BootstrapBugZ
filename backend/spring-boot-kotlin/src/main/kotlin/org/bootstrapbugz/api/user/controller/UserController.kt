package org.bootstrapbugz.api.user.controller

import jakarta.validation.Valid
import org.bootstrapbugz.api.shared.constants.Path
import org.bootstrapbugz.api.shared.payload.dto.AvailabilityDTO
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest
import org.bootstrapbugz.api.user.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Path.USERS)
class UserController(private val userService: UserService) {
  @GetMapping
  fun findAll(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") limit: Int
  ): ResponseEntity<List<UserDTO>> {
    return ResponseEntity.ok(userService.findAll(PageRequest.of(page, limit, Sort.by("id"))))
  }

  @GetMapping("/{id}")
  fun findById(@PathVariable("id") id: Long): ResponseEntity<UserDTO> {
    return ResponseEntity.ok(userService.findById(id))
  }

  @GetMapping("/username/{username}")
  fun findByUsername(@PathVariable("username") username: String): ResponseEntity<UserDTO> {
    return ResponseEntity.ok(userService.findByUsername(username))
  }

  @PostMapping("/username/availability")
  fun usernameAvailability(
    @Valid @RequestBody request: UsernameAvailabilityRequest
  ): ResponseEntity<AvailabilityDTO> {
    return ResponseEntity.ok(userService.usernameAvailability(request))
  }

  @PostMapping("/email/availability")
  fun emailAvailability(
    @Valid @RequestBody request: EmailAvailabilityRequest
  ): ResponseEntity<AvailabilityDTO> {
    return ResponseEntity.ok(userService.emailAvailability(request))
  }
}
