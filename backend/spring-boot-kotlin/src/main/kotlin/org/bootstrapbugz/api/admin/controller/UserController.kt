package org.bootstrapbugz.api.admin.controller

import jakarta.validation.Valid
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest
import org.bootstrapbugz.api.admin.payload.request.UserRequest
import org.bootstrapbugz.api.admin.service.UserService
import org.bootstrapbugz.api.shared.constants.Path
import org.bootstrapbugz.api.user.payload.dto.UserDTO
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("adminUserController")
@RequestMapping(Path.ADMIN_USERS)
class UserController(private val userService: UserService) {

  @PostMapping
  fun create(@RequestBody @Valid saveRequest: UserRequest): ResponseEntity<UserDTO> {
    return ResponseEntity(userService.create(saveRequest), HttpStatus.CREATED)
  }

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

  @PutMapping("/{id}")
  fun update(
    @PathVariable("id") id: Long,
    @RequestBody @Valid saveRequest: UserRequest
  ): ResponseEntity<UserDTO> {
    return ResponseEntity.ok(userService.update(id, saveRequest))
  }

  @DeleteMapping("/{id}")
  fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
    userService.delete(id)
    return ResponseEntity.noContent().build()
  }

  @PatchMapping("/{id}")
  fun patch(
    @PathVariable("id") id: Long,
    @Valid @RequestBody patchUserRequest: PatchUserRequest
  ): ResponseEntity<UserDTO> {
    return ResponseEntity.ok(userService.patch(id, patchUserRequest))
  }
}
