package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.*;
import com.supermarket.erp.repository.GoodsReceivedNoteRepository;
import com.supermarket.erp.repository.PurchaseOrderRepository;
import com.supermarket.erp.service.GoodsReceivedNoteService;
import com.supermarket.erp.service.InventoryService;
import com.supermarket.erp.service.LocationService;
import com.supermarket.erp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

@Service
public class GoodsReceivedNoteServiceImpl implements GoodsReceivedNoteService {

    private final GoodsReceivedNoteRepository grnRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryService inventoryService;
    private final UserService userService;
    private final LocationService locationService;

    public GoodsReceivedNoteServiceImpl(GoodsReceivedNoteRepository grnRepository,
                                         PurchaseOrderRepository purchaseOrderRepository,
                                         InventoryService inventoryService,
                                         UserService userService,
                                         LocationService locationService) {
        this.grnRepository = grnRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @Override
    public List<GoodsReceivedNote> getAllGrns() {
        return grnRepository.findAll();
    }

    @Override
    public GoodsReceivedNote getGrnById(Long id) {
        return grnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GRN not found with id: " + id));
    }

    @Override
    @Transactional
    public GoodsReceivedNote receiveGoods(Long purchaseOrderId, Long receivedByUserId, Long locationId,
                                           LocalDate receivedDate, Map<Long, Integer> quantitiesByPoItemId) {

        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase order not found with id: " + purchaseOrderId));

        if (!po.isReceivable()) {
            throw new IllegalStateException("This purchase order is not open to receive goods against.");
        }

        User receivedBy = userService.getUserById(receivedByUserId);
        Location location = locationService.getLocationById(locationId);

        GoodsReceivedNote grn = new GoodsReceivedNote();
        grn.setGrnNumber(generateGrnNumber());
        grn.setPurchaseOrder(po);
        grn.setReceivedBy(receivedBy);
        grn.setLocation(location);
        grn.setReceivedDate(receivedDate != null ? receivedDate : LocalDate.now());

        boolean anyReceived = false;

        for (PurchaseOrderItem poItem : po.getItems()) {
            Integer qtyNow = quantitiesByPoItemId.get(poItem.getId());
            if (qtyNow == null || qtyNow <= 0) {
                continue;
            }
            if (qtyNow > poItem.getRemainingQuantity()) {
                throw new IllegalArgumentException(
                        "Received quantity for '" + poItem.getProduct().getName() +
                        "' exceeds the remaining ordered quantity (" + poItem.getRemainingQuantity() + ").");
            }

            GrnItem grnItem = new GrnItem(poItem, poItem.getProduct(), qtyNow);
            grnItem.setGrn(grn);
            grnItem.setReceivedDate(grn.getReceivedDate());
            grn.getItems().add(grnItem);

            poItem.setReceivedQuantity(poItem.getReceivedQuantity() + qtyNow);

            // Integration point: push stock into Inventory for this product + location
            inventoryService.increaseStock(poItem.getProduct().getId(), locationId, qtyNow);

            anyReceived = true;
        }

        if (!anyReceived) {
            throw new IllegalArgumentException("Enter a received quantity for at least one item.");
        }

        boolean allFullyReceived = po.getItems().stream()
                .allMatch(i -> i.getRemainingQuantity() == 0);
        po.setStatus(allFullyReceived ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        purchaseOrderRepository.save(po);

        return grnRepository.save(grn);
    }

    @Override
    public String generateGrnNumber() {
        int year = Year.now().getValue();
        long countThisYear = grnRepository.countByReceivedDateBetween(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
        return String.format("GRN-%d-%04d", year, countThisYear + 1);
    }
}
