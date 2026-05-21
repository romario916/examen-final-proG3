-- Création de la base de données avec un nom adapté au sujet
CREATE DATABASE "techadvisor_db";

-- Création d'un utilisateur dédié pour la gestion de l'application
CREATE USER "techadvisor_manager" WITH PASSWORD '123456';

-- Attribution de tous les privilèges sur la base
GRANT ALL PRIVILEGES ON DATABASE techadvisor_db TO techadvisor_manager;

-- Définition de l'utilisateur comme propriétaire de la base
ALTER DATABASE techadvisor_db OWNER TO techadvisor_manager;