package org.bootstrapbugz.api.shared.generic.crud;

import java.util.List;

public interface CrudService<T, U> {
  List<T> findAll();

  T findById(Long id);

  T create(U saveRequest);

  T update(Long id, U saveRequest);

  void delete(Long id);
}
