package com.supermarket.erp.controller;

import com.supermarket.erp.entity.Location;
import com.supermarket.erp.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public String listLocations(Model model) {
        model.addAttribute("locations", locationService.getAllLocations());
        return "locations/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("location", new Location());
        model.addAttribute("formTitle", "Add Location");
        return "locations/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("location", locationService.getLocationById(id));
        model.addAttribute("formTitle", "Edit Location");
        return "locations/form";
    }

    @PostMapping("/save")
    public String saveLocation(@jakarta.validation.Valid @org.springframework.web.bind.annotation.ModelAttribute("location") Location location,
                                org.springframework.validation.BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", location.getId() == null ? "Add Location" : "Edit Location");
            return "locations/form";
        }
        locationService.saveLocation(location);
        redirectAttributes.addFlashAttribute("successMessage", "Location saved successfully.");
        return "redirect:/locations";
    }

    @GetMapping("/delete/{id}")
    public String deleteLocation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        locationService.deleteLocation(id);
        redirectAttributes.addFlashAttribute("successMessage", "Location deleted successfully.");
        return "redirect:/locations";
    }
}
