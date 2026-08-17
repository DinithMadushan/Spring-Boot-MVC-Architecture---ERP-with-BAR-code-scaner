package com.supermarket.erp.service;

import com.supermarket.erp.entity.GoodsReceivedNote;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface GoodsReceivedNoteService {

    List<GoodsReceivedNote> getAllGrns();

    GoodsReceivedNote getGrnById(Long id);

    /**
     * Records a GRN against a purchase order: creates the GRN, marks each
     * received PurchaseOrderItem's receivedQuantity, increases Inventory stock
     * for each item at the given location, and updates the PurchaseOrder's
     * overall status.
     *
     * @param purchaseOrderId       the PO being received against
     * @param receivedByUserId      id of the logged-in User receiving the goods
     * @param locationId            the location/warehouse stock is received into
     * @param receivedDate          date goods were received
     * @param quantitiesByPoItemId  map of PurchaseOrderItem id -> quantity received now
     */
    GoodsReceivedNote receiveGoods(Long purchaseOrderId, Long receivedByUserId, Long locationId,
                                    LocalDate receivedDate,
                                    Map<Long, Integer> quantitiesByPoItemId);

    String generateGrnNumber();

}
