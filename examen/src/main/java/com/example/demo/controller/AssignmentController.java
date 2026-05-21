package com.example.demo.controller;

import com.example.demo.model.AssignmentInput;
import com.example.demo.service.AssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/missions")
public class AssignmentController {
    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    @PutMapping("/{missionId}/assignments/{consultantId}")
    public ResponseEntity<?> putAssignment(
            @PathVariable String missionId,
            @PathVariable String consultantId,
            @RequestBody AssignmentInput input) {
        try {
            boolean isUpdated = service.assignConsultant(missionId, consultantId, input);
            
            if (isUpdated) {
                return ResponseEntity.ok(Map.of("message", "Assignment updated"));
            } else {
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Assignment created"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", "NOT_FOUND", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            // Code 409 Conflict en cas de violation des règles métiers (TJM < 80%, etc.)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", "BUSINESS_RULE_VIOLATION", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("code", "INTERNAL_SERVER_ERROR", "message", "Erreur serveur."));
        }
    }
}
