///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						FichiersTXT.java
// Description:                 Classe qui gère les interactions avec les fichiers .txt
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								https://www.journaldev.com/878/java-write-to-file
//////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.io.*;

public class FichiersTXT {

    public static final String ERR_OUVERTURE = "Probl\u00E8me d'ouverture du fichier.";
    public static final String ERR_LECTURE = "Probl\u00E8me \u00E0 la lecture du fichier .txt.";
    public static final String ERR_ECRITURE = "Erreur lors de l'\u00E9criture du fichier.";

    /**
     * Lit une liste de jeux contenue dans un fichier .txt et les ajoute à la base de données
     * ou ajoute les consoles associées à ce jeu s'il s'y trouve déjà.
     * Utilise un BufferedReader pour améliorer la performance à la lecture.
     *
     * @param nomFile Le nom du fichier .txt à lire
     */
    public static void ajouterTXT(String nomFile) {

        FileReader fr = null;
        boolean existeFichier = true;
        boolean finFichier = false;

        try {
            fr = new FileReader(nomFile);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("ajouterTXT - new FileReader: " + e.getMessage());
            GUI.messageErreur(ERR_OUVERTURE);
            existeFichier = false;
        }

        if (existeFichier) {
            BufferedReader entree = new BufferedReader(fr);

            while (!finFichier) {
                String ligne = null;        // null si fin de fichier
                try {
                    ligne = entree.readLine();
                } catch (IOException e) {
                    System.out.println("ajouterTXT - entree.readLine: " + e.getMessage());
                    GUI.messageErreur(ERR_LECTURE);
                }
                if (ligne != null) {

					/*Format d'une entrée:	split(;)
					 						[0]	[1]		[2]		[3] -> consoles
					 						EA;NHL 2020;E;PS4,WIIU,XONE,PC */
                    String[] parametres = ligne.split(";");
                    String[] consoles = parametres[3].split(",");

                    Jeu nouveau = new Jeu(parametres[0], parametres[1], parametres[2]);
                    for (String console : consoles) {
                        nouveau.addConsole(console);
                    }

                    Requetes.inserer(nouveau);
                } else finFichier = true;
            }
            try {
                entree.close();
            } catch (IOException e) {
                System.out.println("ajouterTXT - entree.close(): " + e.getMessage());
                GUI.messageErreur(ERR_LECTURE);
            }
        }
    }

    /**
     * Vérifie si le nom de fichier passé en paramètre est valide, et si oui, vide la base de données puis fait appel
     * à ajouterTXT() pour lire le fichier passé en paramètre.
     *
     * @param nomFile Le nom du fichier .txt à lire
     */
    public static void nouvelleBaseTXT(String nomFile) {

        boolean existeFichier = true;

        try {
            new FileReader(nomFile);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("nouvellesBaseTXT: " + e.getMessage());
            GUI.messageErreur(ERR_OUVERTURE);
            existeFichier = false;
        }

        if (existeFichier) {
            // Vide la base de données sur le serveur SQL
            Requetes.viderDB();
            ajouterTXT(nomFile);
        }
    }

    /**
     * Écrit les informations de la base de données dans un fichier .txt sous le format:
     * Fabricant;Titre;COTE;LISTE DES CONSOLES
     *
     * @param nomFichier Le nom du fichier .txt à créer
     */
    public static void enregistrerTXT(String nomFichier) {

        File fichier = new File(nomFichier);
        FileWriter fr = null;

        try {
            fr = new FileWriter(fichier);
            fr.write(transcrirePourFichier());

        } catch (IOException e) {
            System.out.println("enregistrerTXT - fr.write(): " + e.getMessage());
            GUI.messageErreur(ERR_ECRITURE);
        } finally {
            try {
                assert fr != null;
                fr.close();
            } catch (IOException e) {
                System.out.println("enregistrerTXT - fr.close(): " + e.getMessage());
                GUI.messageErreur(ERR_ECRITURE);
            }
        }
    }

    /**
     * Retranscrit chacun des jeux au format .txt prédéfini pour la lecture/écriture.
     * Utilise un StringBuilder pour améliorer la performance à l'écriture
     *
     * @return Un String de plusieurs ligne, où chaque jeu a ce format: Fabricant;Titre;COTE;LISTE DES CONSOLES
     */
    public static String transcrirePourFichier() {

        StringBuilder epeler = new StringBuilder();

        for (Jeu jeuCourant : Requetes.listerDB()) {
            epeler.append(jeuCourant.toString()).append("\n");
        }
        return epeler.toString();
    }


    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + Fonctions utiles pour transposer à l'interface graphique (GUI)
     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * Parcourt la base de données et ajoute un vecteur de String pour chacun des jeux, dans le but
     * de l'afficher dans un JTable.
     *
     * @return Un vecteur de jeux, dont chacun de ses attributs sont regroupés dans un vecteur de String
     */
    public static Vector<Vector<String>> vectoriser() {

        Vector<Vector<String>> vecteurJeu = new Vector<>();
        for (Jeu jeuCourant : Requetes.listerDB()) {
            vecteurJeu.add(jeuCourant.vectoriser());
        }
        return vecteurJeu;
    }
}