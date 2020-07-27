DROP TABLE IF EXISTS jeu;

CREATE TABLE jeu
(
    fabricant VARCHAR(20) NOT NULL,
    nom       VARCHAR(50) NOT NULL,
    cote      VARCHAR(5) DEFAULT 'E',
    consoles   VARCHAR(50) NOT NULL,
    PRIMARY KEY (nom, fabricant),
    CONSTRAINT niveau_cote CHECK (cote IN ('E', 'PG', 'T', 'RP','M'))
)