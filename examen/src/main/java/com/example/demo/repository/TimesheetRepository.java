package com.example.demo.repository;

import com.example.demo.config.DatabaseConfig;
import com.example.demo.model.TimesheetEntry;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class TimesheetRepository {

    private final DatabaseConfig databaseConfig;

    public TimesheetRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public String getTimesheetStatus(String consultantId, String week) {
    
        String sql = "SELECT statut FROM timesheet WHERE consultant_id = ? AND semaine = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consultantId);
            stmt.setString(2, week);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("statut");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    public void saveTimesheet(String consultantId, String week, String status, List<TimesheetEntry> entries) {
        Connection conn = null;
        try {
            conn = databaseConfig.getConnection();
            conn.setAutoCommit(false); 

            
            String upsertTimesheet = "INSERT INTO timesheet (id, consultant_id, semaine, statut, soumis_le) " +
                                     "VALUES (?, ?, ?, ?, NOW()) " +
                                     "ON CONFLICT (consultant_id, semaine) DO UPDATE SET statut = ?, soumis_le = NOW()";
            try (PreparedStatement stmt = conn.prepareStatement(upsertTimesheet)) {
                String timesheetId = "t-" + consultantId + "-" + week; // Génération d'un ID unique si nécessaire
                stmt.setString(1, timesheetId);
                stmt.setString(2, consultantId);
                stmt.setString(3, week);
                stmt.setString(4, status);
                stmt.setString(5, status);
                stmt.executeUpdate();
            }

            
            String deleteEntries = "DELETE FROM timesheet_entry WHERE consultant_id = ? AND week = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteEntries)) {
                stmt.setString(1, consultantId);
                stmt.setString(2, week);
                stmt.executeUpdate();
            }

            
            String insertEntry = "INSERT INTO timesheet_entry (timesheet_id, consultant_id, week, entry_date, mission_id, day_fraction) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertEntry)) {
                String timesheetId = "t-" + consultantId + "-" + week;
                for (TimesheetEntry entry : entries) {
                    stmt.setString(1, timesheetId);
                    stmt.setString(2, consultantId);
                    stmt.setString(3, week);
                    stmt.setDate(4, Date.valueOf(entry.getDate()));
                    stmt.setString(5, entry.getMissionId());
                    stmt.setDouble(6, entry.getDayFraction());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Erreur de transaction SQL lors de l'enregistrement du timesheet", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public void updateStatus(String consultantId, String week, String status, String comment) {
        
        String sql = "UPDATE timesheet SET statut = ?, comment = ?, validated_at = NOW() WHERE consultant_id = ? AND semaine = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, comment);
            stmt.setString(3, consultantId);
            stmt.setString(4, week);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de mise à jour du statut de validation", e);
        }
    }
}