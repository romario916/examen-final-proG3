package com.example.demo.service;

import com.example.demo.model.AssignmentInput;
import com.example.demo.model.Consultant;
import com.example.demo.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class AssignmentService {
    private final AssignmentRepository repository;

    public AssignmentService(AssignmentRepository repository) {
        this.repository = repository;
    }

    public boolean assignConsultant(String missionId, String consultantId, AssignmentInput input) {
        Consultant consultant = repository.findConsultantById(consultantId);
        if (consultant == null) {
            throw new IllegalArgumentException("Consultant introuvable.");
        }

        
        long minAllowedRate = (long) (consultant.getDefaultTJM() * 0.8);
        if (input.getNegotiatedDailyRate() < minAllowedRate) {
            throw new IllegalStateException("Violation règle métier : Le TJM négocié ne peut pas être inférieur à 80% du tarif du grade.");
        }

        
        double currentDays = repository.getTotalPlannedDaysForPeriod(
            consultantId, 
            Date.valueOf(input.getStartDate()), 
            Date.valueOf(input.getEndDate())
        );
        if (currentDays + input.getPlannedDays() > 30) { // Exemple basé sur une limite mensuelle arbitraire de jours ouvrés
            throw new IllegalStateException("Violation règle métier : La charge du consultant dépasse 100% sur la période.");
        }

        
        boolean alreadyExists = repository.exists(missionId, consultantId);
        
        repository.saveOrUpdate(missionId, consultantId, input, alreadyExists);
        
        return alreadyExists; 
    }
}
