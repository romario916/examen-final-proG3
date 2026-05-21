CREATE TABLE consultant (
    id VARCHAR(50) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    grade VARCHAR(50) NOT NULL,
    date_embauche DATE NOT NULL
);

CREATE TABLE client (
    id VARCHAR(50) PRIMARY KEY,
    raison_sociale VARCHAR(100) NOT NULL,
    secteur VARCHAR(50) NOT NULL,
    email_contact VARCHAR(100) NOT NULL
);

CREATE TABLE mission (
    id VARCHAR(50) PRIMARY KEY,
    description TEXT NOT NULL,
    client_id VARCHAR(50) REFERENCES client(id),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL
);

CREATE TABLE assignment (
    id VARCHAR(50) PRIMARY KEY,
    mission_id VARCHAR(50) REFERENCES mission(id),
    consultant_id VARCHAR(50) REFERENCES consultant(id),
    jours_prevus INT NOT NULL,
    tjm NUMERIC(15, 2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    statut VARCHAR(50) NOT NULL
);

CREATE TABLE timesheet (
    id VARCHAR(50) PRIMARY KEY,
    consultant_id VARCHAR(50) REFERENCES consultant(id),
    semaine VARCHAR(20) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    soumis_le TIMESTAMP,
    comment TEXT,
    validated_at TIMESTAMP,
    UNIQUE (consultant_id, semaine)
);

CREATE TABLE invoice (
    id VARCHAR(50) PRIMARY KEY,
    number VARCHAR(50) UNIQUE NOT NULL,
    mission_id VARCHAR(50) REFERENCES mission(id),
    period VARCHAR(20) NOT NULL,
    total_amount NUMERIC(15, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    issue_date TIMESTAMP NOT NULL
);

CREATE TABLE timesheet_entry (
    id SERIAL PRIMARY KEY,
    timesheet_id VARCHAR(50) REFERENCES timesheet(id) ON DELETE CASCADE,
    consultant_id VARCHAR(50) REFERENCES consultant(id),
    week VARCHAR(20),
    entry_date DATE NOT NULL,
    mission_id VARCHAR(50) REFERENCES mission(id),
    day_fraction NUMERIC(3, 1) NOT NULL,
    invoice_id VARCHAR(50) REFERENCES invoice(id) ON DELETE SET NULL
);

CREATE TABLE invoice_line (
    id SERIAL PRIMARY KEY,
    invoice_id VARCHAR(50) REFERENCES invoice(id) ON DELETE CASCADE,
    consultant_id VARCHAR(50) REFERENCES consultant(id),
    jours_factures INT NOT NULL,
    tjm NUMERIC(15, 2) NOT NULL,
    sous_total NUMERIC(15, 2) NOT NULL
);