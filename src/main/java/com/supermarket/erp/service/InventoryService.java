package com.supermarket.erp.service;

import com.supermarket.erp.entity.Inventory;

import java.util.List;

public interface InventoryService {

    List<Inventory> getAllInventory();

    List<Inventory> getInventoryByLocation(Long locationId);

    List<Inventory> getInventoryByProduct(Long productId);

    int getQuantityOnHand(Long productId, Long locationId);

    /**
     * Increases stock of a product at a location, creating the Inventory row if needed.
     * Used when goods are received against a GRN.
     */
    Inventory increaseStock(Long productId, Long locationId, int quantity);

    /**
     * Decreases stock of a product at a location. Throws if there isn't enough on hand.
     * Used when a sale is completed.
     */
    Inventory decreaseStock(Long productId, Long locationId, int quantity);

}
