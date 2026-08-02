package com.supermarket.erp.service;

import com.supermarket.erp.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    boolean barcodeExists(String barcode);

    String generateBarcode();

}
