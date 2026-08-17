package com.supermarket.erp.controller;

import com.supermarket.erp.entity.Supplier;
import com.supermarket.erp.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // View Supplier List
    @GetMapping
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "suppliers/list";
    }

    // Show Add Supplier form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("formTitle", "Add Supplier");
        return "suppliers/form";
    }

    // Show Edit Supplier form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.getSupplierById(id));
        model.addAttribute("formTitle", "Edit Supplier");
        return "suppliers/form";
    }

    // Add or Update Supplier
    @PostMapping("/save")
    public String saveSupplier(@Valid @ModelAttribute("supplier") Supplier supplier,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("formTitle", supplier.getId() == null ? "Add Supplier" : "Edit Supplier");
            return "suppliers/form";
        }
        supplierService.saveSupplier(supplier);
        redirectAttributes.addFlashAttribute("successMessage",
                "Supplier '" + supplier.getName() + "' saved successfully.");
        return "redirect:/suppliers";
    }

    // Delete Supplier
    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (supplierService.hasLinkedPurchaseOrders(id)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete this supplier because it still has purchase orders linked to it. " +
                    "Cancel or remove those purchase orders first.");
            return "redirect:/suppliers";
        }
        supplierService.deleteSupplier(id);
        redirectAttributes.addFlashAttribute("successMessage", "Supplier deleted successfully.");
        return "redirect:/suppliers";
    }
}
