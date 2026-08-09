package com.supermarket.erp.repository;

import com.supermarket.erp.entity.PurchaseOrder;
import com.supermarket.erp.entity.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findByStatusIn(List<PurchaseOrderStatus> statuses);

    long countByOrderDateBetween(java.time.LocalDate start, java.time.LocalDate end);

}
