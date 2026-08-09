package com.supermarket.erp.service;

import com.supermarket.erp.entity.GoodsReceivedNote;

import java.util.List;
import java.util.Map;

public interface GoodsReceivedNoteService {

    List<GoodsReceivedNote> getAllGrns();

    GoodsReceivedNote getGrnById(Long id);

    /**
     * Records a GRN against a purchase order: creates the GRN, marks each
     * received PurchaseOrderItem's receivedQuantity, increases Product stock
     * for each item, and updates the PurchaseOrder's overall status.
     *
     * @param purchaseOrderId       the PO being received against
     * @param receivedBy            name of the person receiving the goods
     * @param receivedDate          date goods were received
     * @param quantitiesByPoItemId  map of PurchaseOrderItem id -> quantity received now
     */
    GoodsReceivedNote receiveGoods(Long purchaseOrderId, String receivedBy,
                                    java.time.LocalDate receivedDate,
                                    Map<Long, Integer> quantitiesByPoItemId);

    String generateGrnNumber();

}
