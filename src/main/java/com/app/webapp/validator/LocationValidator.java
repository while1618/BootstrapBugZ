package com.app.webapp.validator;

import com.app.webapp.model.Department;
import com.app.webapp.model.Location;
import com.app.webapp.service.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class LocationValidator implements ConstraintValidator<ValidLocation, Department> {
    @Autowired
    private ILocationService locationService;

    @Override
    public void initialize(ValidLocation constraintAnnotation) {

    }

    @Override
    public boolean isValid(Department department, ConstraintValidatorContext constraintValidatorContext) {
        List<Location> locations = locationService.findAll();
        return locations.contains(department.getLocation());
    }
}
