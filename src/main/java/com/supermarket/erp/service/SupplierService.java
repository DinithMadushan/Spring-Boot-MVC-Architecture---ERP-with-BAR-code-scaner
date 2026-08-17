package com.supermarket.erp.service;

import com.supermarket.erp.entity.Supplier;

import java.util.List;

public interface SupplierService {

    List<Supplier> getAllSuppliers();

    Supplier getSupplierById(Long id);

    Supplier saveSupplier(Supplier supplier);

    void deleteSupplier(Long id);

    boolean hasLinkedPurchaseOrders(Long id);

}
