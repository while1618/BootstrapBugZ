package com.app.webapp.service;

import com.app.webapp.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LocationService {
    Page<Location> findAllLocations(Integer page);
    List<Location> findAllLocations();
    Location findLocationById(Long id);
    Optional<Location> findById(Long id);
    Location save(Location location);
    void deleteAll();
    void createLocation(Location location);
    void editLocation(Location location);
    void deleteLocation(Long id);
}
