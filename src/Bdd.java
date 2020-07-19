///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						Bdd.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								https://www.journaldev.com/878/java-write-to-file
//////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.io.*;

// TODO: renommer pour que ce soit plutôt un modèle relié aux fichiers txt
public class Bdd {

    /* Lit une liste de jeux contenue dans un fichier .txt et les ajoute à la base de données
     * ou ajoute les consoles associées à ce jeu s'il s'y trouve déjà.
     * Utilise un BufferedReader pour améliorer la performance à la lecture.
     *
     * @param nomFile		Le nom du fichier .txt à lire */
    public void ajouterTXT(String nomFile) {

        FileReader fr = null;
        boolean existeFichier = true;
        boolean finFichier = false;

        try {
            fr = new FileReader(nomFile);
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Probleme d'ouverture du fichier " + nomFile);
            existeFichier = false;
        }

        if (existeFichier) {
            BufferedReader entree = new BufferedReader(fr);

            while (!finFichier) {
                String ligne = null;        // null si fin de fichier
                try {
                    ligne = entree.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

    /* Vérifie si le nom de fichier passé en paramètre est valide, et si oui, vide la base de données puis fait appel
     * à ajouterTXT() pour lire le fichier passé en paramètre.
     *
     * @param nomFile		Le nom du fichier .txt à lire */
    public void nouvelleBaseTXT(String nomFile) {

        boolean existeFichier = true;

        try {
            new FileReader(nomFile);
        } catch (java.io.FileNotFoundException e) {
            // TODO: modifier pour GUI
            System.out.println("Probleme d'ouverture du fichier " + nomFile);
            existeFichier = false;
        }

        if (existeFichier) {
            // Vide la base de données sur le serveur SQL
            Requetes.effacer();
            ajouterTXT(nomFile);
        }
    }

    /* Écrit les informations de la base de données dans un fichier .txt sous le format:
     * Fabricant;Titre;COTE;LISTE DES CONSOLES
     *
     * @param nomFichier		Le nom du fichier .txt à créer */
    public void enregistrerTXT(String nomFichier) {

        File fichier = new File(nomFichier);
        FileWriter fr = null;

        try {
            fr = new FileWriter(fichier);
            fr.write(this.transcrirePourFichier());

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: GUI erreur
            System.out.println("Erreur lors de l'écriture du fichier.");
        } finally {
            try {
                assert fr != null;
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erreur lors de la tentative du fermeture du fichier après l'écriture.");
            }
        }
    }

    /* Retranscrit chacun des jeux au format .txt prédéfini pour la lecture/écriture.
     * Utilise un StringBuilder pour améliorer la performance à l'écriture
     *
     * @return  Un String de plusieurs ligne, où chaque jeu a ce format: Fabricant;Titre;COTE;LISTE DES CONSOLES */
    public String transcrirePourFichier() {

        StringBuilder epeler = new StringBuilder();

        for (Jeu jeuCourant : Requetes.listerDB()) {
            epeler.append(jeuCourant.toString()).append("\n");
        }
        return epeler.toString();
    }


    /*****************************************************************************************************
     * Fonctions utiles pour transposer à l'interface graphique (GUI)
     *****************************************************************************************************/

    /* Parcourt la base de données et ajoute un vecteur de String pour chacun des jeux, dans le but
     * de l'afficher dans un JTable.
     *
     * @return	Un vecteur de jeux, dont chacun de ses attributs sont regroupés dans un vecteur de String */
    public Vector<Vector<String>> vectoriser() {

        Vector<Vector<String>> vecteurJeu = new Vector<>();

        for (Jeu jeuCourant : Requetes.listerDB()) {
            vecteurJeu.add(jeuCourant.vectoriser());
        }

        return vecteurJeu;
    }
}