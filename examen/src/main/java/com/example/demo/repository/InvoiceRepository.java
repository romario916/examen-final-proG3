package com.example.demo.repository;

import com.example.demo.config.DatabaseConfig;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.UUID;

@Repository
public class InvoiceRepository {

    private final DatabaseConfig databaseConfig;

    public InvoiceRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public long calculateInvoiceAmount(String missionId, String period) {
        // Corrections majeures apportées ici :
        // 1. a.negotiated_rate -> a.tjm (Colonne réelle de ta table assignment)
        // 2. te.week = t.week -> te.week = t.semaine (Jointure sur la table timesheet)
        // 3. t.status = 'VALIDATED' -> t.statut = 'VALIDATED'
        String sql = "SELECT SUM(te.day_fraction * a.tjm) FROM timesheet_entry te " +
                     "JOIN timesheet t ON te.consultant_id = t.consultant_id AND te.week = t.semaine " +
                     "JOIN assignment a ON te.mission_id = a.mission_id AND te.consultant_id = a.consultant_id " +
                     "WHERE te.mission_id = ? AND TO_CHAR(te.entry_date, 'YYYY-MM') = ? AND t.statut = 'VALIDATED' " +
                     "AND te.invoice_id IS NULL";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, missionId);
            stmt.setString(2, period);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public String createInvoice(String missionId, String period, long totalAmount) {
        String invoiceId = UUID.randomUUID().toString();
        String invoiceNumber = "INV-" + period + "-" + (int)(Math.random() * 10000);
        
        Connection conn = null;
        try {
            conn = databaseConfig.getConnection();
            conn.setAutoCommit(false);

            
            String insertInvoice = "INSERT INTO invoice (id, number, mission_id, period, total_amount, status, issue_date) VALUES (?, ?, ?, ?, ?, 'ISSUED', NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(insertInvoice)) {
                stmt.setString(1, invoiceId);
                stmt.setString(2, invoiceNumber);
                stmt.setString(3, missionId);
                stmt.setString(4, period);
                stmt.setLong(5, totalAmount);
                stmt.executeUpdate();
            }

            
            String flagEntries = "UPDATE timesheet_entry SET invoice_id = ? WHERE mission_id = ? AND TO_CHAR(entry_date, 'YYYY-MM') = ? " +
                                 "AND id IN (SELECT te2.id FROM timesheet_entry te2 JOIN timesheet t ON te2.consultant_id = t.consultant_id AND te2.week = t.semaine WHERE t.statut = 'VALIDATED')";
            try (PreparedStatement stmt = conn.prepareStatement(flagEntries)) {
                stmt.setString(1, invoiceId);
                stmt.setString(2, missionId);
                stmt.setString(3, period);
                stmt.executeUpdate();
            }

            conn.commit();
            return invoiceNumber;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Erreur lors de la génération de la facture", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}