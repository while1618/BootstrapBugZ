package com.app.webapp.service;

import com.app.webapp.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LocationService {
    Page<Location> findAllLocations(Integer page);
    List<Location> findAllLocations();
    Location findLocationById(Long id);
    void createLocation(Location location);
    void editLocation(Location location);
    void deleteLocation(Long id);
}
