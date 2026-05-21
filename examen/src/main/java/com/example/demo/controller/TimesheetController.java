package com.example.demo.controller;

import com.example.demo.model.TimesheetInput;
import com.example.demo.model.ValidationInput;
import com.example.demo.service.TimesheetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/consultants")
public class TimesheetController {
    private final TimesheetService service;

    public TimesheetController(TimesheetService service) {
        this.service = service;
    }

    // Soumission du Timesheet
    @PutMapping("/{consultantId}/timesheets/{week}")
    public ResponseEntity<?> putTimesheet(
            @PathVariable String consultantId,
            @PathVariable String week,
            @RequestBody TimesheetInput input) {
        try {
            service.submitTimesheet(consultantId, week, input);
            return ResponseEntity.ok(Map.of("message", "Timesheet processed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("code", "ALREADY_VALIDATED", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", "INTERNAL_SERVER_ERROR", "message", "Erreur serveur."));
        }
    }

    // Validation ou Rejet du Timesheet
    @PutMapping("/{consultantId}/timesheets/{week}/validation")
    public ResponseEntity<?> putTimesheetValidation(
            @PathVariable String consultantId,
            @PathVariable String week,
            @RequestBody ValidationInput input) {
        try {
            service.validateTimesheet(consultantId, week, input.getOutcome(), input.getComment());
            return ResponseEntity.ok(Map.of("message", "Validation outcome recorded"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", "INTERNAL_SERVER_ERROR", "message", "Erreur lors de la validation."));
        }
    }
}
