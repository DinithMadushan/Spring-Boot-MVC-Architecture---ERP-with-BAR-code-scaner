package com.supermarket.erp.controller;

import com.supermarket.erp.entity.Product;
import com.supermarket.erp.service.ProductService;
import com.supermarket.erp.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final SupplierService supplierService;

    public ProductController(ProductService productService, SupplierService supplierService) {
        this.productService = productService;
        this.supplierService = supplierService;
    }

    // View Product List
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/list";
    }

    // Show Add Product form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        Product product = new Product();
        product.setBarcode(productService.generateBarcode());
        model.addAttribute("product", product);
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("formTitle", "Add Product");
        return "products/form";
    }

    // Show Edit Product form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("formTitle", "Edit Product");
        return "products/form";
    }

    // Add or Update Product
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        // Enforce barcode uniqueness (ignore the product's own current barcode when editing)
        boolean barcodeTaken = productService.getAllProducts().stream()
                .anyMatch(p -> p.getBarcode().equals(product.getBarcode())
                        && !p.getId().equals(product.getId()));
        if (barcodeTaken) {
            result.rejectValue("barcode", "duplicate", "This barcode is already assigned to another product");
        }

        if (result.hasErrors()) {
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("formTitle", product.getId() == null ? "Add Product" : "Edit Product");
            return "products/form";
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Product '" + product.getName() + "' saved successfully.");
        return "redirect:/products";
    }

    // Delete Product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        return "redirect:/products";
    }
}
