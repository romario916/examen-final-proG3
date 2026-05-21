package com.example.demo.repository;

import com.example.demo.config.DatabaseConfig;
import com.example.demo.model.AssignmentInput;
import com.example.demo.model.Consultant;
import com.example.demo.model.Grade;
import org.springframework.stereotype.Repository;

import java.sql.*;


@Repository
public class AssignmentRepository {

    
    private final DatabaseConfig databaseConfig;

    
    public AssignmentRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Consultant findConsultantById(String consultantId) {
        String sql = "SELECT id, name, grade FROM consultant WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consultantId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Consultant(
                        rs.getString("id"),
                        rs.getString("name"),
                        Grade.valueOf(rs.getString("grade"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean exists(String missionId, String consultantId) {
        String sql = "SELECT 1 FROM assignment WHERE mission_id = ? AND consultant_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, missionId);
            stmt.setString(2, consultantId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalPlannedDaysForPeriod(String consultantId, Date start, Date end) {
        
        String sql = "SELECT SUM(planned_days) FROM assignment WHERE consultant_id = ? AND status = 'ACTIVE' " +
                     "AND NOT (end_date < ? OR start_date > ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, consultantId);
            stmt.setDate(2, start);
            stmt.setDate(3, end);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveOrUpdate(String missionId, String consultantId, AssignmentInput input, boolean exists) {
        String sql;
        if (exists) {
            sql = "UPDATE assignment SET planned_days = ?, negotiated_rate = ?, start_date = ?, end_date = ? WHERE mission_id = ? AND consultant_id = ?";
        } else {
            sql = "INSERT INTO assignment (planned_days, negotiated_rate, start_date, end_date, status, mission_id, consultant_id, created_at) VALUES (?, ?, ?, ?, 'ACTIVE', ?, ?, NOW())";
        }

        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, input.getPlannedDays());
            stmt.setLong(2, input.getNegotiatedDailyRate());
            stmt.setDate(3, Date.valueOf(input.getStartDate()));
            stmt.setDate(4, Date.valueOf(input.getEndDate()));
            stmt.setString(5, missionId);
            stmt.setString(6, consultantId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'écriture SQL lors de l'affectation", e);
        }
    }
}