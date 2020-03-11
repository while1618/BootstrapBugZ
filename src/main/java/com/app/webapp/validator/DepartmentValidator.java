package com.app.webapp.validator;

import com.app.webapp.model.Department;
import com.app.webapp.service.DepartmentService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class DepartmentValidator implements Validator {
    private final DepartmentService departmentService;
    private final MessageSource messageSource;

    public DepartmentValidator(DepartmentService departmentService, MessageSource messageSource) {
        this.departmentService = departmentService;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Department.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Department department = (Department) o;
        List<Department> departments = departmentService.findAllDepartments();
        if (!departments.contains(department))
            errors.rejectValue("department", "employee.department",messageSource.getMessage("Valid.department", null, LocaleContextHolder.getLocale()));
    }
}
