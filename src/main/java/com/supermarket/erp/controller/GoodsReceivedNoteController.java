package com.supermarket.erp.controller;

import com.supermarket.erp.entity.PurchaseOrder;
import com.supermarket.erp.entity.User;
import com.supermarket.erp.service.GoodsReceivedNoteService;
import com.supermarket.erp.service.LocationService;
import com.supermarket.erp.service.PurchaseOrderService;
import com.supermarket.erp.service.UserService;
import org.springframework.security.core.Authentication;
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
    private final LocationService locationService;
    private final UserService userService;

    public GoodsReceivedNoteController(GoodsReceivedNoteService grnService,
                                        PurchaseOrderService purchaseOrderService,
                                        LocationService locationService,
                                        UserService userService) {
        this.grnService = grnService;
        this.purchaseOrderService = purchaseOrderService;
        this.locationService = locationService;
        this.userService = userService;
    }

    @GetMapping
    public String listGrns(Model model) {
        model.addAttribute("grns", grnService.getAllGrns());
        return "grn/list";
    }

    @GetMapping("/view/{id}")
    public String viewGrn(@PathVariable Long id, Model model) {
        model.addAttribute("grn", grnService.getGrnById(id));
        return "grn/view";
    }

    @GetMapping("/new")
    public String choosePurchaseOrder(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.getReceivablePurchaseOrders());
        return "grn/select-po";
    }

    @GetMapping("/new/{poId}")
    public String showReceiveForm(@PathVariable Long poId, Model model, RedirectAttributes redirectAttributes) {
        PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(poId);
        if (!po.isReceivable()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This purchase order has already been fully received or was cancelled.");
            return "redirect:/grn/new";
        }
        model.addAttribute("po", po);
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("today", LocalDate.now());
        return "grn/form";
    }

    @PostMapping("/save")
    public String saveGrn(@RequestParam Long purchaseOrderId,
                           @RequestParam Long locationId,
                           @RequestParam(required = false)
                           @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                           LocalDate receivedDate,
                           @RequestParam("poItemIds") List<Long> poItemIds,
                           @RequestParam("receivedQuantities") List<Integer> receivedQuantities,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        Map<Long, Integer> quantitiesByPoItemId = new HashMap<>();
        for (int i = 0; i < poItemIds.size(); i++) {
            quantitiesByPoItemId.put(poItemIds.get(i), receivedQuantities.get(i));
        }

        User receiver = userService.getByUsername(authentication.getName());

        try {
            var grn = grnService.receiveGoods(purchaseOrderId, receiver.getId(), locationId, receivedDate, quantitiesByPoItemId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "GRN " + grn.getGrnNumber() + " recorded and stock updated.");
            return "redirect:/grn";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/grn/new/" + purchaseOrderId;
        }
    }
}
