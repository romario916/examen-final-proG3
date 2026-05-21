package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.sql.Connection;

@Component
public class DatabaseCheckRunner implements CommandLineRunner {

    private final DatabaseConfig databaseConfig;

    // Injection de ta configuration sans "static"
    public DatabaseCheckRunner(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("🔄 TENTATIVE DE CONNEXION À POSTGRESQL EN COURS...");
        System.out.println("==================================================");
        
        try (Connection conn = databaseConfig.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SUCCÈS : Connexion établie avec brio à techadvisor_db !");
                System.out.println("Le projet Spring Boot et PostgreSQL se parlent parfaitement.");
            }
        } catch (Exception e) {
            System.err.println("❌ ÉCHEC DE CONNEXION : Impossible de joindre la base !");
            System.err.println("Détail de l'erreur : " + e.getMessage());
            System.err.println("👉 Vérifie que : ");
            System.err.println("   1. Ton serveur PostgreSQL est bien démarré.");
            System.err.println("   2. La base 'techadvisor_db' existe.");
            System.err.println("   3. L'utilisateur et le mot de passe '123456' sont corrects.");
        }
        System.out.println("==================================================");
    }
}