package com.supermarket.erp.repository;

import com.supermarket.erp.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    boolean existsByEmailIgnoreCase(String email);

}
