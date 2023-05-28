package org.bootstrapbugz.api.shared.generic.crud;

public interface CrudService<T, U> {
  T findById(Long id);

  T create(U saveRequest);

  T update(Long id, U saveRequest);

  void delete(Long id);
}
