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
        String sql = "SELECT status FROM timesheet WHERE consultant_id = ? AND week = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consultantId);
            stmt.setString(2, week);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
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

        
            String upsertTimesheet = "INSERT INTO timesheet (consultant_id, week, status, submitted_at) VALUES (?, ?, ?, NOW()) " +
                                     "ON CONFLICT (consultant_id, week) DO UPDATE SET status = ?, submitted_at = NOW()";
            try (PreparedStatement stmt = conn.prepareStatement(upsertTimesheet)) {
                stmt.setString(1, consultantId);
                stmt.setString(2, week);
                stmt.setString(3, status);
                stmt.setString(4, status);
                stmt.executeUpdate();
            }

            
            String deleteEntries = "DELETE FROM timesheet_entry WHERE consultant_id = ? AND week = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteEntries)) {
                stmt.setString(1, consultantId);
                stmt.setString(2, week);
                stmt.executeUpdate();
            }

            // 3. Insérer les nouvelles lignes
            String insertEntry = "INSERT INTO timesheet_entry (consultant_id, week, entry_date, mission_id, day_fraction) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertEntry)) {
                for (TimesheetEntry entry : entries) {
                    stmt.setString(1, consultantId);
                    stmt.setString(2, week);
                    stmt.setDate(3, Date.valueOf(entry.getDate()));
                    stmt.setString(4, entry.getMissionId());
                    stmt.setDouble(5, entry.getDayFraction());
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
        String sql = "UPDATE timesheet SET status = ?, comment = ?, validated_at = NOW() WHERE consultant_id = ? AND week = ?";
        
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