package com.supermarket.erp.controller;

import com.supermarket.erp.entity.Product;
import com.supermarket.erp.entity.PurchaseOrder;
import com.supermarket.erp.entity.PurchaseOrderItem;
import com.supermarket.erp.entity.User;
import com.supermarket.erp.service.ProductService;
import com.supermarket.erp.service.PurchaseOrderService;
import com.supermarket.erp.service.SupplierService;
import com.supermarket.erp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final UserService userService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService,
                                    SupplierService supplierService,
                                    ProductService productService,
                                    UserService userService) {
        this.purchaseOrderService = purchaseOrderService;
        this.supplierService = supplierService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    public String listPurchaseOrders(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.getAllPurchaseOrders());
        return "purchase-orders/list";
    }

    @GetMapping("/view/{id}")
    public String viewPurchaseOrder(@PathVariable Long id, Model model) {
        model.addAttribute("po", purchaseOrderService.getPurchaseOrderById(id));
        return "purchase-orders/view";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("po", new PurchaseOrder());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("formTitle", "New Purchase Order");
        return "purchase-orders/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(id);
        if (!po.isEditable()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This purchase order can no longer be edited because goods have already been received against it.");
            return "redirect:/purchase-orders";
        }
        model.addAttribute("po", po);
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("formTitle", "Edit Purchase Order");
        return "purchase-orders/form";
    }

    @PostMapping("/save")
    public String savePurchaseOrder(@RequestParam(required = false) Long id,
                                     @RequestParam Long supplierId,
                                     @RequestParam(required = false)
                                     @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                                     LocalDate orderDate,
                                     @RequestParam("productIds") List<Long> productIds,
                                     @RequestParam("orderedQuantities") List<Integer> orderedQuantities,
                                     @RequestParam("unitPrices") List<BigDecimal> unitPrices,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {

        if (productIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Add at least one line item to the purchase order.");
            return "redirect:/purchase-orders/new";
        }

        PurchaseOrder po = (id != null) ? purchaseOrderService.getPurchaseOrderById(id) : new PurchaseOrder();
        po.setSupplier(supplierService.getSupplierById(supplierId));
        po.setOrderDate(orderDate != null ? orderDate : LocalDate.now());

        if (po.getId() == null) {
            User creator = userService.getByUsername(authentication.getName());
            po.setCreatedBy(creator);
        }

        List<PurchaseOrderItem> items = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.getProductById(productIds.get(i));
            items.add(new PurchaseOrderItem(product, orderedQuantities.get(i), unitPrices.get(i)));
        }
        po.getItems().clear();
        po.getItems().addAll(items);

        purchaseOrderService.savePurchaseOrder(po);
        redirectAttributes.addFlashAttribute("successMessage",
                "Purchase order " + po.getPoNumber() + " saved successfully.");
        return "redirect:/purchase-orders";
    }

    @GetMapping("/approve/{id}")
    public String approvePurchaseOrder(@PathVariable Long id, Authentication authentication,
                                        RedirectAttributes redirectAttributes) {
        User approver = userService.getByUsername(authentication.getName());
        purchaseOrderService.approvePurchaseOrder(id, approver.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Purchase order approved.");
        return "redirect:/purchase-orders/view/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deletePurchaseOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseOrderService.deletePurchaseOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Purchase order deleted successfully.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/purchase-orders";
    }
}
