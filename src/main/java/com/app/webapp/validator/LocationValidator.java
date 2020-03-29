package com.app.webapp.validator;

import com.app.webapp.model.Location;
import com.app.webapp.service.ILocationService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class LocationValidator implements ConstraintValidator<ValidLocation, Location> {
    private final ILocationService locationService;

    public LocationValidator(ILocationService locationService) {
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
