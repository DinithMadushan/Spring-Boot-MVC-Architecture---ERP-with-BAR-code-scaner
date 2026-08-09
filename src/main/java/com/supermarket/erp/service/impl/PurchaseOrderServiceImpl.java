package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.PurchaseOrder;
import com.supermarket.erp.entity.PurchaseOrderItem;
import com.supermarket.erp.entity.PurchaseOrderStatus;
import com.supermarket.erp.repository.PurchaseOrderRepository;
import com.supermarket.erp.service.PurchaseOrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + id));
    }

    @Override
    public List<PurchaseOrder> getReceivablePurchaseOrders() {
        return purchaseOrderRepository.findByStatusIn(
                List.of(PurchaseOrderStatus.PENDING, PurchaseOrderStatus.PARTIALLY_RECEIVED));
    }

    @Override
    public PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getId() == null) {
            purchaseOrder.setPoNumber(generatePoNumber());
            if (purchaseOrder.getOrderDate() == null) {
                purchaseOrder.setOrderDate(LocalDate.now());
            }
            purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
        }
        // Keep the bidirectional link consistent before saving (cascade needs this set)
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            item.setPurchaseOrder(purchaseOrder);
        }
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = getPurchaseOrderById(id);
        if (!po.isEditable()) {
            throw new IllegalStateException(
                    "Cannot delete a purchase order that already has goods received against it.");
        }
        purchaseOrderRepository.deleteById(id);
    }

    @Override
    public String generatePoNumber() {
        int year = Year.now().getValue();
        long countThisYear = purchaseOrderRepository.countByOrderDateBetween(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        return String.format("PO-%d-%04d", year, countThisYear + 1);
    }
}
