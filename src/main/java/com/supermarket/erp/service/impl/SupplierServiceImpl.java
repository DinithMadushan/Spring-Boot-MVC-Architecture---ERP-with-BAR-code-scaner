package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.Supplier;
import com.supermarket.erp.repository.SupplierRepository;
import com.supermarket.erp.service.SupplierService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
    }

    @Override
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    @Override
    public boolean hasLinkedPurchaseOrders(Long id) {
        Supplier supplier = getSupplierById(id);
        return supplier.getPurchaseOrders() != null && !supplier.getPurchaseOrders().isEmpty();
    }
}
