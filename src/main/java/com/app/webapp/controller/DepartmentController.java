package com.app.webapp.controller;

import com.app.webapp.model.Department;
import com.app.webapp.service.IDepartmentService;
import com.app.webapp.service.ILocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class DepartmentController {
    private final IDepartmentService departmentService;
    private final ILocationService locationService;

    public DepartmentController(IDepartmentService departmentService, ILocationService locationService) {
        this.departmentService = departmentService;
        this.locationService = locationService;
    }

    @GetMapping("/departments")
    public ModelAndView departments(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        ModelAndView modelAndView = new ModelAndView("department/departments");
        return modelAndView.addObject("departments", departmentService.findAllDepartments(page));
    }

    @GetMapping("/department/create")
    public ModelAndView createDepartment() {
        ModelAndView modelAndView = new ModelAndView("department/create");
        modelAndView.addObject("department", new Department());
        modelAndView.addObject("locations", locationService.findAll());

        return modelAndView;
    }

    @PostMapping("/department/create")
    public ModelAndView createDepartment(@Valid @ModelAttribute("department") Department department, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("locations", locationService.findAll());
            modelAndView.setViewName("department/create");
        } else {
            departmentService.createDepartment(department);
            modelAndView.setViewName("redirect:/departments");
        }

        return modelAndView;
    }

    @GetMapping("/department/edit/{id}")
    public String editEmployee(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("department"))
            model.addAttribute("department", departmentService.findDepartmentById(id));
        model.addAttribute("locations", locationService.findAll());

        return "department/edit";
    }

    @PostMapping("/department/edit")
    public ModelAndView editDepartment(@Valid @ModelAttribute("department") Department department, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.department", bindingResult);
            redirectAttributes.addFlashAttribute("department", department);
            modelAndView.setViewName("redirect:/department/edit/" + department.getId());
        } else {
            departmentService.editDepartment(department);
            modelAndView.setViewName("redirect:/departments");
        }

        return modelAndView;
    }

    @GetMapping("/department/delete/{id}")
    public String deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteById(id);
        return "redirect:/departments";
    }
}
