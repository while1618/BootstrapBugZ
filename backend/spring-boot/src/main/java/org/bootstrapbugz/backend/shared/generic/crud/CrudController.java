package org.bootstrapbugz.backend.shared.generic.crud;

import jakarta.validation.Valid;
import org.bootstrapbugz.backend.shared.logger.Logger;
import org.bootstrapbugz.backend.shared.payload.dto.PageableDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class CrudController<T, U> {
  private final CrudService<T, U> service;
  private final Logger logger;

  protected CrudController(CrudService<T, U> service, Logger logger) {
    this.service = service;
    this.logger = logger;
  }

  @PostMapping
  public ResponseEntity<T> create(@Valid @RequestBody U saveRequest) {
    logger.info("Called");
    final var response = new ResponseEntity<>(service.create(saveRequest), HttpStatus.CREATED);
    logger.info("Finished");
    return response;
  }

  @GetMapping
  public ResponseEntity<PageableDTO<T>> findAll(Pageable pageable) {
    logger.info("Called");
    final var response = ResponseEntity.ok(service.findAll(pageable));
    logger.info("Finished");
    return response;
  }

  @GetMapping("/{id}")
  public ResponseEntity<T> findById(@PathVariable("id") Long id) {
    logger.info("Called");
    final var response = ResponseEntity.ok(service.findById(id));
    logger.info("Finished");
    return response;
  }

  @PutMapping("/{id}")
  public ResponseEntity<T> update(@PathVariable("id") Long id, @Valid @RequestBody U saveRequest) {
    logger.info("Called");
    final var response = ResponseEntity.ok(service.update(id, saveRequest));
    logger.info("Finished");
    return response;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    logger.info("Called");
    service.delete(id);
    logger.info("Finished");
    return ResponseEntity.noContent().build();
  }
}
