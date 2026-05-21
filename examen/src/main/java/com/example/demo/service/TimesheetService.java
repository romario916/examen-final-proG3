package com.example.demo.service;

import com.example.demo.model.TimesheetEntry;
import com.example.demo.model.TimesheetInput;
import com.example.demo.repository.TimesheetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class TimesheetService {
    private final TimesheetRepository repository;

    public TimesheetService(TimesheetRepository repository) {
        this.repository = repository;
    }

    public void submitTimesheet(String consultantId, String week, TimesheetInput input) {
        String currentStatus = repository.getTimesheetStatus(consultantId, week);
        
        // RÈGLE MÉTIER : Non modifiable si déjà validée
        if ("VALIDATED".equals(currentStatus)) {
            throw new IllegalStateException("Le timesheet est déjà validé et ne peut plus être modifié.");
        }

        // RÈGLE MÉTIER : La somme des fractions de journée par jour ne doit pas dépasser 1.0
        Map<LocalDate, Double> dailyTotals = new HashMap<>();
        for (TimesheetEntry entry : input.getEntries()) {
            dailyTotals.put(entry.getDate(), dailyTotals.getOrDefault(entry.getDate(), 0.0) + entry.getDayFraction());
            if (dailyTotals.get(entry.getDate()) > 1.0) {
                throw new IllegalArgumentException("La charge totale déclarée pour le " + entry.getDate() + " dépasse 1.0.");
            }
        }

        // Sauvegarde avec l'état SUBMITTED
        repository.saveTimesheet(consultantId, week, "SUBMITTED", input.getEntries());
    }

    public void validateTimesheet(String consultantId, String week, String outcome, String comment) {
        // Enregistre la validation ou le rejet par le responsable
        repository.updateStatus(consultantId, week, outcome, comment);
    }
}
