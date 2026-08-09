package com.supermarket.erp.repository;

import com.supermarket.erp.entity.GoodsReceivedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface GoodsReceivedNoteRepository extends JpaRepository<GoodsReceivedNote, Long> {

    long countByReceivedDateBetween(LocalDate start, LocalDate end);

}
