package org.bootstrapbugz.api.shared.generic.crud

import org.springframework.data.domain.Pageable

interface CrudService<T, U> {
  fun create(saveRequest: U): T

  fun findAll(pageable: Pageable): List<T>

  fun findById(id: Long): T

  fun update(id: Long, saveRequest: U): T

  fun delete(id: Long)
}
