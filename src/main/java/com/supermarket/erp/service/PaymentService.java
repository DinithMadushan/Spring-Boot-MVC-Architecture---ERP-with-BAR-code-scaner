package com.supermarket.erp.service;

import com.supermarket.erp.entity.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> getPaymentsForReceipt(Long receiptId);

    List<Payment> getAllPayments();

}
