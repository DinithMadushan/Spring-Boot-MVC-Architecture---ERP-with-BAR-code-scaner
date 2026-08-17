package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.Payment;
import com.supermarket.erp.repository.PaymentRepository;
import com.supermarket.erp.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> getPaymentsForReceipt(Long receiptId) {
        return paymentRepository.findByInvoiceId(receiptId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
