package com.supermarket.erp.controller;

import com.supermarket.erp.service.InventoryService;
import com.supermarket.erp.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final LocationService locationService;

    public InventoryController(InventoryService inventoryService, LocationService locationService) {
        this.inventoryService = inventoryService;
        this.locationService = locationService;
    }

    @GetMapping
    public String viewInventory(@RequestParam(required = false) Long locationId, Model model) {
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("selectedLocationId", locationId);
        model.addAttribute("inventory", locationId != null
                ? inventoryService.getInventoryByLocation(locationId)
                : inventoryService.getAllInventory());
        return "inventory/list";
    }
}
