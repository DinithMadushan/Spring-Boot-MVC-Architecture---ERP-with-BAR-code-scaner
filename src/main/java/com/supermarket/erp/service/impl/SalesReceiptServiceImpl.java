package com.supermarket.erp.service.impl;

import com.supermarket.erp.entity.*;
import com.supermarket.erp.repository.PaymentRepository;
import com.supermarket.erp.repository.SalesReceiptRepository;
import com.supermarket.erp.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
public class SalesReceiptServiceImpl implements SalesReceiptService {

    private final SalesReceiptRepository salesReceiptRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;
    private final UserService userService;
    private final LocationService locationService;
    private final InventoryService inventoryService;

    public SalesReceiptServiceImpl(SalesReceiptRepository salesReceiptRepository,
                                    PaymentRepository paymentRepository,
                                    ProductService productService,
                                    UserService userService,
                                    LocationService locationService,
                                    InventoryService inventoryService) {
        this.salesReceiptRepository = salesReceiptRepository;
        this.paymentRepository = paymentRepository;
        this.productService = productService;
        this.userService = userService;
        this.locationService = locationService;
        this.inventoryService = inventoryService;
    }

    @Override
    public List<SalesReceipt> getAllReceipts() {
        return salesReceiptRepository.findAll();
    }

    @Override
    public SalesReceipt getReceiptById(Long id) {
        return salesReceiptRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales receipt not found with id: " + id));
    }

    @Override
    @Transactional
    public SalesReceipt completeSale(Long cashierId, Long locationId,
                                      List<Long> productIds, List<Integer> quantities, List<BigDecimal> unitPrices,
                                      BigDecimal discount, PaymentMethod paymentMethod) {

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("A sale needs at least one item.");
        }

        User cashier = userService.getUserById(cashierId);
        Location location = locationService.getLocationById(locationId);

        SalesReceipt receipt = new SalesReceipt();
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setCashier(cashier);
        receipt.setLocation(location);
        receipt.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        receipt.setPaymentMethod(paymentMethod);
        receipt.setStatus(ReceiptStatus.COMPLETED);
        receipt.setCreatedAt(LocalDateTime.now());

        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.getProductById(productIds.get(i));
            Integer qty = quantities.get(i);
            BigDecimal price = unitPrices.get(i);

            // Will throw IllegalArgumentException if not enough stock at this location
            inventoryService.decreaseStock(product.getId(), locationId, qty);

            SalesReceiptItem item = new SalesReceiptItem(product, qty, price);
            item.setReceipt(receipt);
            receipt.getItems().add(item);
        }

        SalesReceipt saved = salesReceiptRepository.save(receipt);

        // Record a matching Payment for the full receipt total (single-payment checkout)
        Payment payment = new Payment(saved, paymentMethod, saved.getTotalAmount());
        paymentRepository.save(payment);

        return saved;
    }

    @Override
    public String generateReceiptNumber() {
        int year = Year.now().getValue();
        long countThisYear = salesReceiptRepository.countByCreatedAtBetween(
                LocalDateTime.of(year, 1, 1, 0, 0), LocalDateTime.of(year, 12, 31, 23, 59, 59));
        return String.format("RCPT-%d-%05d", year, countThisYear + 1);
    }
}
