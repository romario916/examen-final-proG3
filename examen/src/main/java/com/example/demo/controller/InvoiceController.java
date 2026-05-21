package com.example.demo.controller;

import com.example.demo.model.InvoiceCreationRequest;
import com.example.demo.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceCreationRequest request) {
        try {
            String invoiceNumber = service.generateMonthlyInvoice(request.getMissionId(), request.getPeriod());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Invoice created successfully", "invoiceNumber", invoiceNumber));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", "NO_ELIGIBLE_ENTRIES", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", "INTERNAL_SERVER_ERROR", "message", "Erreur lors de la génération de la facture."));
        }
    }
}
