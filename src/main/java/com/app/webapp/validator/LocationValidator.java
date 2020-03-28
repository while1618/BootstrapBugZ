package com.app.webapp.validator;

import com.app.webapp.model.Location;
import com.app.webapp.service.LocationService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class LocationValidator implements Validator {
    private final LocationService locationService;
    private final MessageSource messageSource;

    public LocationValidator(LocationService locationService, MessageSource messageSource) {
        this.locationService = locationService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Location.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Location location = (Location) o;
        List<Location> locations = locationService.findAll();
        if (!locations.contains(location))
            errors.rejectValue("location", "department.location",messageSource.getMessage("Valid.location", null, LocaleContextHolder.getLocale()));
    }
}
