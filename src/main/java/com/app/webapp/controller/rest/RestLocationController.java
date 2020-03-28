package com.app.webapp.controller.rest;

import com.app.webapp.exception.LocationNotFoundException;
import com.app.webapp.model.Location;
import com.app.webapp.service.ILocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestLocationController {
    private final ILocationService locationService;

    public RestLocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> findAll() {
        List<Location> locations = locationService.findAll();
        if (locations.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No locations.", new LocationNotFoundException());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> findById(@PathVariable("id") Long id) {
        Location location = locationService.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found.", new LocationNotFoundException()));
        return ResponseEntity.ok(location);
    }

    //TODO: ResponseEntity.created()
    @PostMapping("/locations")
    public ResponseEntity<Location> create(@Valid @RequestBody Location location) {
        return ResponseEntity.ok(locationService.save(location));
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<Location> edit(@PathVariable("id") Long id, @Valid @RequestBody Location location) {
        if (locationService.existsById(id)) {
            location.setId(id);
            return ResponseEntity.ok(locationService.save(location));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct location id.", new LocationNotFoundException());
        }
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (locationService.existsById(id)) {
            locationService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct location id.", new LocationNotFoundException());
        }
    }

    @DeleteMapping("/locations")
    public ResponseEntity<?> deleteAll() {
        locationService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
