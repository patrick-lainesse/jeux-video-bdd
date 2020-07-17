/*//////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						creer.sql
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						https://www.mysqltutorial.org/mysql-create-database/
/////////////////////////////////////////////////////////////////////////////*/

DROP TABLE IF EXISTS jeu;

CREATE TABLE jeu
(
    fabricant VARCHAR(20) NOT NULL,
    nom       VARCHAR(50) NOT NULL,
    cote      VARCHAR(5) DEFAULT 'E',
    console   VARCHAR(50) NOT NULL,
    PRIMARY KEY (nom, fabricant),
    CONSTRAINT niveau_cote CHECK (cote IN ('E', 'PG', 'T', 'M')),
    CONSTRAINT nom_console CHECK (console IN
                                  ('PC', 'GAMECUBE', 'MAC', 'PS2', 'PS3', 'PS4', 'SWITCH', 'WIIU', 'X360', 'XONE'))
)