package com.supermarket.erp.service;

import com.supermarket.erp.entity.PaymentMethod;
import com.supermarket.erp.entity.SalesReceipt;

import java.math.BigDecimal;
import java.util.List;

public interface SalesReceiptService {

    List<SalesReceipt> getAllReceipts();

    SalesReceipt getReceiptById(Long id);

    /**
     * Completes a sale: creates the SalesReceipt + line items, decreases Inventory
     * stock for each product sold at the given location, and records a matching Payment.
     *
     * @param cashierId      logged-in cashier's User id
     * @param locationId     the store/branch the sale is made at
     * @param productIds     product id for each line
     * @param quantities     quantity for each line (parallel to productIds)
     * @param unitPrices     unit price for each line (parallel to productIds)
     * @param discount       flat discount applied to the receipt total
     * @param paymentMethod  how the customer paid
     */
    SalesReceipt completeSale(Long cashierId, Long locationId,
                               List<Long> productIds, List<Integer> quantities, List<BigDecimal> unitPrices,
                               BigDecimal discount, PaymentMethod paymentMethod);

    String generateReceiptNumber();

}
