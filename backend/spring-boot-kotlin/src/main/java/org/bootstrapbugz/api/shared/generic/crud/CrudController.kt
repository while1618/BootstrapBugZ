package org.bootstrapbugz.api.shared.generic.crud

import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

abstract class CrudController<T, U> (val service: CrudService<T, U>) {

    @PostMapping
    fun create(@RequestBody saveRequest: @Valid U): ResponseEntity<T> {
        return ResponseEntity(service.create(saveRequest), HttpStatus.CREATED)
    }

    @GetMapping
    fun findAll(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "10") limit: Int): ResponseEntity<List<T>> {
        return ResponseEntity.ok(service.findAll(PageRequest.of(page, limit, Sort.by("id"))))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: Long): ResponseEntity<T> {
        return ResponseEntity.ok(service.findById(id))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody saveRequest: @Valid U): ResponseEntity<T> {
        return ResponseEntity.ok(service.update(id, saveRequest))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
