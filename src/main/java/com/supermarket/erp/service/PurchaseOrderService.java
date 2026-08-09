package com.supermarket.erp.service;

import com.supermarket.erp.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {

    List<PurchaseOrder> getAllPurchaseOrders();

    PurchaseOrder getPurchaseOrderById(Long id);

    /**
     * Purchase orders that still have goods outstanding (PENDING or PARTIALLY_RECEIVED),
     * used to populate the "create GRN against this PO" dropdown.
     */
    List<PurchaseOrder> getReceivablePurchaseOrders();

    PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder);

    void deletePurchaseOrder(Long id);

    String generatePoNumber();

}
