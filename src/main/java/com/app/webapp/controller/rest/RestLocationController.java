package com.app.webapp.controller.rest;

import com.app.webapp.assembler.LocationAssembler;
import com.app.webapp.error.exception.LocationNotFoundException;
import com.app.webapp.model.Location;
import com.app.webapp.service.ILocationService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestLocationController {
    private final ILocationService locationService;
    private final MessageSource messageSource;
    private final LocationAssembler locationAssembler;

    public RestLocationController(ILocationService locationService, MessageSource messageSource, LocationAssembler locationAssembler) {
        this.locationService = locationService;
        this.messageSource = messageSource;
        this.locationAssembler = locationAssembler;
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> findAll() {
        List<Location> locations = locationService.findAll();
        if (locations.isEmpty())
            throw new LocationNotFoundException(messageSource.getMessage("locations.notFound", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<EntityModel<Location>> findById(@PathVariable("id") Long id) {
        Location location = locationService.findById(id).orElseThrow(
                () -> new LocationNotFoundException(messageSource.getMessage("location.notFound", null, LocaleContextHolder.getLocale())));
        return ResponseEntity.ok(locationAssembler.toModel(location));
    }

    @PostMapping("/locations")
    public ResponseEntity<EntityModel<Location>> create(@Valid @RequestBody Location location) {
        EntityModel<Location> model = locationAssembler.toModel(locationService.save(location));
        return ResponseEntity
                .created(URI.create(model.getRequiredLink("self").getHref()))
                .body(model);
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<EntityModel<Location>> edit(@PathVariable("id") Long id, @Valid @RequestBody Location location) {
        if (!locationService.existsById(id))
            throw new LocationNotFoundException(messageSource.getMessage("location.wrongId", null, LocaleContextHolder.getLocale()));
        location.setId(id);
        return ResponseEntity.ok(locationAssembler.toModel(locationService.save(location)));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (!locationService.existsById(id))
            throw new LocationNotFoundException(messageSource.getMessage("location.wrongId", null, LocaleContextHolder.getLocale()));
        locationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/locations")
    public ResponseEntity<?> deleteAll() {
        locationService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
