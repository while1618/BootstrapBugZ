package com.app.webapp.validator.impl;

import com.app.webapp.model.Location;
import com.app.webapp.service.LocationService;
import com.app.webapp.validator.ValidLocation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class LocationValidator implements ConstraintValidator<ValidLocation, Location> {
    private final LocationService locationService;

    public LocationValidator(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public void initialize(ValidLocation constraintAnnotation) {

    }

    @Override
    public boolean isValid(Location location, ConstraintValidatorContext constraintValidatorContext) {
        List<Location> locations = locationService.findAll();
        return locations.contains(location);
    }
}
