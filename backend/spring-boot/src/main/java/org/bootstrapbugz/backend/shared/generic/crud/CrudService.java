package org.bootstrapbugz.backend.shared.generic.crud;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CrudService<T, U> {
  T create(U saveRequest);

  List<T> findAll(Pageable pageable);

  T findById(Long id);

  T update(Long id, U saveRequest);

  void delete(Long id);
}
