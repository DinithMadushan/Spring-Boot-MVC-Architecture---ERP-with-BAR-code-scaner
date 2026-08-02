package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.Product;
import com.supermarket.erp.repository.ProductRepository;
import com.supermarket.erp.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SecureRandom random = new SecureRandom();

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean barcodeExists(String barcode) {
        return productRepository.existsByBarcode(barcode);
    }

    /**
     * Generates a unique 12-digit numeric barcode (EAN-like) not already in use.
     */
    @Override
    public String generateBarcode() {
        String candidate;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                sb.append(random.nextInt(10));
            }
            candidate = sb.toString();
        } while (productRepository.existsByBarcode(candidate));
        return candidate;
    }
}
