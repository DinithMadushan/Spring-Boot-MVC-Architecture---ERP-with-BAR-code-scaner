package com.supermarket.erp.repository;

import com.supermarket.erp.entity.SalesReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesReceiptRepository extends JpaRepository<SalesReceipt, Long> {

    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

}
