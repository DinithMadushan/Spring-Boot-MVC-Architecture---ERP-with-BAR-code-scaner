package com.supermarket.erp.config;

import com.supermarket.erp.entity.Supplier;
import com.supermarket.erp.repository.SupplierRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.ParseException;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SupplierFormatter supplierFormatter;

    public WebConfig(SupplierFormatter supplierFormatter) {
        this.supplierFormatter = supplierFormatter;
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addFormatter(supplierFormatter);
    }

    /**
     * Two-way binding between the <select> dropdown on the product form and the
     * Supplier entity: parses the submitted id back into a managed Supplier
     * (so saving a Product links it correctly), and prints a Supplier as its id
     * so Thymeleaf can pre-select the right <option> when editing a product.
     * This is what makes the Product <-> Supplier integration work through MVC binding.
     */
    @Component
    public static class SupplierFormatter implements Formatter<Supplier> {

        private final SupplierRepository supplierRepository;

        public SupplierFormatter(SupplierRepository supplierRepository) {
            this.supplierRepository = supplierRepository;
        }

        @Override
        public Supplier parse(@NonNull String text, @NonNull Locale locale) throws ParseException {
            if (text.isBlank()) {
                return null;
            }
            return supplierRepository.findById(Long.valueOf(text))
                    .orElseThrow(() -> new ParseException("Unknown supplier id: " + text, 0));
        }

        @Override
        @NonNull
        public String print(Supplier supplier, @NonNull Locale locale) {
            return supplier == null || supplier.getId() == null ? "" : supplier.getId().toString();
        }
    }
}
