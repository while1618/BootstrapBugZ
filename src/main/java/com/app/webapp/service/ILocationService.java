package com.app.webapp.service;

import com.app.webapp.model.Location;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ILocationService {
    Page<Location> findAllLocations(Integer page);
    Location findLocationById(Long id);
    void createLocation(Location location);
    void editLocation(Location location);


    List<Location> findAll();
    Optional<Location> findById(Long id);
    Location save(Location location);
    void deleteById(Long id);
    void deleteAll();
    boolean existsById(Long id);
}
