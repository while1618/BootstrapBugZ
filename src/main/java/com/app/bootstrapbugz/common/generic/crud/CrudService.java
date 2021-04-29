package com.app.bootstrapbugz.common.generic.crud;

import com.app.bootstrapbugz.common.request.DeleteRequest;

public interface CrudService<T, U> {
  T findById(Long id);

  T create(U saveRequest);

  T update(Long id, U saveRequest);

  void delete(DeleteRequest deleteRequest);
}
