package com.app.webapp.controller;

import com.app.webapp.model.Location;
import com.app.webapp.service.ILocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class LocationController {
    private final ILocationService locationService;

    public LocationController(ILocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/locations")
    public ModelAndView locations(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        ModelAndView modelAndView = new ModelAndView("location/locations");
        return modelAndView.addObject("locations", locationService.findAllLocations(page));
    }

    @GetMapping("/location/create")
    public ModelAndView createLocation() {
        ModelAndView modelAndView = new ModelAndView("location/create");
        modelAndView.addObject("location", new Location());

        return modelAndView;
    }

    @PostMapping("/location/create")
    public ModelAndView createLocation(@Valid @ModelAttribute("location") Location location, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("location/create");
        } else {
            locationService.createLocation(location);
            modelAndView.setViewName("redirect:/locations");
        }

        return modelAndView;
    }

    @GetMapping("/location/edit/{id}")
    public String editLocation(@PathVariable("id") Long id, Model model) {
        if (!model.containsAttribute("location"))
            model.addAttribute("location", locationService.findLocationById(id));

        return "location/edit";
    }

    @PostMapping("/location/edit")
    public ModelAndView editLocation(@Valid @ModelAttribute("location") Location location, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.location", bindingResult);
            redirectAttributes.addFlashAttribute("location", location);
            modelAndView.setViewName("redirect:/location/edit/" + location.getId());
        } else {
            locationService.editLocation(location);
            modelAndView.setViewName("redirect:/locations");
        }

        return modelAndView;
    }

    @GetMapping("/location/delete/{id}")
    public String deleteLocation(@PathVariable("id") Long id) {
        locationService.deleteById(id);
        return "redirect:/locations";
    }
}
