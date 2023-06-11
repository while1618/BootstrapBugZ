package org.bootstrapbugz.api.shared.generic.crud;

import java.util.List;

public interface CrudService<T, U> {
  T create(U saveRequest);

  List<T> findAll();

  T findById(Long id);

  T update(Long id, U saveRequest);

  void delete(Long id);
}
