package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.Inventory;
import com.supermarket.erp.entity.Location;
import com.supermarket.erp.entity.Product;
import com.supermarket.erp.repository.InventoryRepository;
import com.supermarket.erp.service.InventoryService;
import com.supermarket.erp.service.LocationService;
import com.supermarket.erp.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    private final LocationService locationService;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                 ProductService productService,
                                 LocationService locationService) {
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.locationService = locationService;
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<Inventory> getInventoryByLocation(Long locationId) {
        return inventoryRepository.findByLocationId(locationId);
    }

    @Override
    public List<Inventory> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public int getQuantityOnHand(Long productId, Long locationId) {
        return inventoryRepository.findByProductIdAndLocationId(productId, locationId)
                .map(Inventory::getQuantityOnHand)
                .orElse(0);
    }

    @Override
    public Inventory increaseStock(Long productId, Long locationId, int quantity) {
        Inventory inventory = findOrCreate(productId, locationId);
        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory decreaseStock(Long productId, Long locationId, int quantity) {
        Inventory inventory = findOrCreate(productId, locationId);
        if (inventory.getQuantityOnHand() < quantity) {
            Product product = productService.getProductById(productId);
            throw new IllegalArgumentException(
                    "Not enough stock for '" + product.getName() + "' at this location. Available: "
                            + inventory.getQuantityOnHand() + ", requested: " + quantity);
        }
        inventory.setQuantityOnHand(inventory.getQuantityOnHand() - quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    private Inventory findOrCreate(Long productId, Long locationId) {
        return inventoryRepository.findByProductIdAndLocationId(productId, locationId)
                .orElseGet(() -> {
                    Product product = productService.getProductById(productId);
                    Location location = locationService.getLocationById(locationId);
                    return new Inventory(product, location, 0);
                });
    }
}
