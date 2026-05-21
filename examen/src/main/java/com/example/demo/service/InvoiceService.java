package com.example.demo.service;

import com.example.demo.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    private final InvoiceRepository repository;

    public InvoiceService(InvoiceRepository repository) {
        this.repository = repository;
    }

    public String generateMonthlyInvoice(String missionId, String period) {
        // 1. Calcul de la somme : (jours validés * TJM négocié)
        long totalAmount = repository.calculateInvoiceAmount(missionId, period);
        
        if (totalAmount <= 0) {
            throw new IllegalStateException("Aucune ligne de feuille de temps validée et éligible trouvée pour cette période.");
        }

        // 2. Création et retour du numéro de facture officiel généré
        return repository.createInvoice(missionId, period, totalAmount);
    }
}
