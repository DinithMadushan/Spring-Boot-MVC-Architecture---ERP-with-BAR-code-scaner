package com.supermarket.erp.controller;

import com.supermarket.erp.entity.PurchaseOrder;
import com.supermarket.erp.service.GoodsReceivedNoteService;
import com.supermarket.erp.service.PurchaseOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/grn")
public class GoodsReceivedNoteController {

    private final GoodsReceivedNoteService grnService;
    private final PurchaseOrderService purchaseOrderService;

    public GoodsReceivedNoteController(GoodsReceivedNoteService grnService,
                                        PurchaseOrderService purchaseOrderService) {
        this.grnService = grnService;
        this.purchaseOrderService = purchaseOrderService;
    }

    // View GRN list
    @GetMapping
    public String listGrns(Model model) {
        model.addAttribute("grns", grnService.getAllGrns());
        return "grn/list";
    }

    // View a single GRN
    @GetMapping("/view/{id}")
    public String viewGrn(@PathVariable Long id, Model model) {
        model.addAttribute("grn", grnService.getGrnById(id));
        return "grn/view";
    }

    // Step 1: choose which PO to receive against
    @GetMapping("/new")
    public String choosePurchaseOrder(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.getReceivablePurchaseOrders());
        return "grn/select-po";
    }

    // Step 2: show the PO's outstanding items to receive
    @GetMapping("/new/{poId}")
    public String showReceiveForm(@PathVariable Long poId, Model model, RedirectAttributes redirectAttributes) {
        PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(poId);
        if (!po.isReceivable()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This purchase order has already been fully received or was cancelled.");
            return "redirect:/grn/new";
        }
        model.addAttribute("po", po);
        model.addAttribute("today", LocalDate.now());
        return "grn/form";
    }

    // Save the GRN — receives goods and updates Product stock + PO status
    @PostMapping("/save")
    public String saveGrn(@RequestParam Long purchaseOrderId,
                           @RequestParam String receivedBy,
                           @RequestParam(required = false)
                           @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                           LocalDate receivedDate,
                           @RequestParam("poItemIds") List<Long> poItemIds,
                           @RequestParam("receivedQuantities") List<Integer> receivedQuantities,
                           RedirectAttributes redirectAttributes) {

        Map<Long, Integer> quantitiesByPoItemId = new HashMap<>();
        for (int i = 0; i < poItemIds.size(); i++) {
            quantitiesByPoItemId.put(poItemIds.get(i), receivedQuantities.get(i));
        }

        try {
            var grn = grnService.receiveGoods(purchaseOrderId, receivedBy, receivedDate, quantitiesByPoItemId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "GRN " + grn.getGrnNumber() + " recorded and stock updated.");
            return "redirect:/grn";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/grn/new/" + purchaseOrderId;
        }
    }
}
