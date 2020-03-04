package com.app.webapp.controller;

import com.app.webapp.model.Location;
import com.app.webapp.service.LocationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestLocationController {
    private final LocationServiceImpl locationService;

    public RestLocationController(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Location>> findAll() {
        List<Location> locations = locationService.findAllLocations();
        if (locations.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<Location> findById(@PathVariable("id") Long id) {
        Optional<Location> location = locationService.findById(id);
        if (!location.isPresent())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(location.get());
    }

    @PostMapping("/locations")
    public ResponseEntity<Location> create(@Valid @RequestBody Location location) {
        return ResponseEntity.ok(locationService.save(location));
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<Location> edit(@PathVariable("id") Long id, @Valid @RequestBody Location location) {
        if (!locationService.findById(id).isPresent())
            return ResponseEntity.badRequest().build();
        location.setId(id);
        return ResponseEntity.ok(locationService.save(location));
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        if (!locationService.findById(id).isPresent())
            return ResponseEntity.badRequest().build();
        locationService.deleteLocation(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/locations")
    public ResponseEntity<HttpStatus> deleteAll() {
        locationService.deleteAll();
        return ResponseEntity.ok().build();
    }
}
