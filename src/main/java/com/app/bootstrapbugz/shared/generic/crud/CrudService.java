package com.app.bootstrapbugz.shared.generic.crud;

import com.app.bootstrapbugz.shared.generic.crud.request.DeleteRequest;

public interface CrudService<T, U> {
  T findById(Long id);

  T create(U saveRequest);

  T update(Long id, U saveRequest);

  void delete(DeleteRequest deleteRequest);
}
