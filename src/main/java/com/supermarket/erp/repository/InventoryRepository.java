package com.supermarket.erp.repository;

import com.supermarket.erp.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndLocationId(Long productId, Long locationId);

    List<Inventory> findByProductId(Long productId);

    List<Inventory> findByLocationId(Long locationId);

}
