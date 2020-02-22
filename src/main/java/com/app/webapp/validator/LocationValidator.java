package com.app.webapp.validator;

import com.app.webapp.model.Location;
import com.app.webapp.service.LocationServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class LocationValidator implements Validator {
    private final LocationServiceImpl locationService;

    public LocationValidator(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Location.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Location location = (Location) o;
        List<Location> locations = locationService.findAllLocations();
        if (!locations.contains(location))
            errors.rejectValue("location", "department.location","Please select valid location.");
    }
}
