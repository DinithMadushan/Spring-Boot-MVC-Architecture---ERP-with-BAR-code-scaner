package com.supermarket.erp.controller;

import com.supermarket.erp.entity.PaymentMethod;
import com.supermarket.erp.entity.User;
import com.supermarket.erp.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final SalesReceiptService salesReceiptService;
    private final ProductService productService;
    private final LocationService locationService;
    private final UserService userService;

    public SalesController(SalesReceiptService salesReceiptService, ProductService productService,
                            LocationService locationService, UserService userService) {
        this.salesReceiptService = salesReceiptService;
        this.productService = productService;
        this.locationService = locationService;
        this.userService = userService;
    }

    @GetMapping
    public String listReceipts(Model model) {
        model.addAttribute("receipts", salesReceiptService.getAllReceipts());
        return "sales/list";
    }

    @GetMapping("/view/{id}")
    public String viewReceipt(@PathVariable Long id, Model model) {
        model.addAttribute("receipt", salesReceiptService.getReceiptById(id));
        return "sales/view";
    }

    @GetMapping("/new")
    public String showSaleForm(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "sales/form";
    }

    @PostMapping("/save")
    public String completeSale(@RequestParam Long locationId,
                                @RequestParam("productIds") List<Long> productIds,
                                @RequestParam("quantities") List<Integer> quantities,
                                @RequestParam("unitPrices") List<BigDecimal> unitPrices,
                                @RequestParam(required = false, defaultValue = "0") BigDecimal discount,
                                @RequestParam PaymentMethod paymentMethod,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User cashier = userService.getByUsername(authentication.getName());
            var receipt = salesReceiptService.completeSale(cashier.getId(), locationId,
                    productIds, quantities, unitPrices, discount, paymentMethod);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Sale " + receipt.getReceiptNumber() + " completed.");
            return "redirect:/sales/view/" + receipt.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/sales/new";
        }
    }
}
