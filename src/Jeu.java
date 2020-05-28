///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	TP1.java
// Fichier:						Jeu.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						aucune
//////////////////////////////////////////////////////////////////////////////

import java.util.*;

public class Jeu implements Comparable<Jeu> {

    private String fabricant;
    private String titre;
    private String cote;
    private Collection<String> consoles;

    public Jeu(String fabricant, String titre, String cote) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        consoles = new LinkedHashSet<String>();
    }

    /*
    * On peut simplement ajouter des consoles à la liste sans vérifier si elles s'y retrouvent déjà, car selon
    * la documentation d'Oracle, le rajout d'un élément déjà présent dans un LinkedHashSet ne change pas l'ordre existant
    */
    // ??? à tester
    public void addConsole(String console) {
        consoles.add(console);
    }

    /**
     * Redéfinition des méthodes pour permettre l'implémentation de l'interface Comparable,
     * afin de permettre le tri des jeux dans la base de données
     */
    @Override
    public boolean equals(Object obj) {
        Jeu autre;
        if (obj instanceof Jeu) {
            autre = (Jeu) obj;
            return fabricant.equals(autre.fabricant) && titre.equals(autre.titre);
        }
        return false;
    }

    @Override
    public int compareTo(Jeu o) {
        int res = titre.compareTo(o.titre);

        if (res == 0) {
            res = fabricant.compareTo(o.fabricant);
        }
        return res;
    }

    @Override
    public String toString() {
        //EA;NHL 2020;E;PS4,WIIU,XONE,PC
        return fabricant + ";" + titre + ";" + cote + ";" + printConsoles(consoles);
    }
// hashcode à redéfinir?????

    private String printConsoles(Collection<String> consoles) {

        String liste = "";
        for (String console : consoles) {
            // String builder??????
            liste += console + ",";
        }
        if (liste.length() > 0) {
            liste = liste.substring(0, liste.length() - 1);
        }
        return liste;
    }
    // vérifier si possibilité de bug si on n'ajoute pas de liste de consoles à un jeu????


    /**
     * Début des get et set pour tous les paramètres de la classe
     */
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getFabricant() {
        return fabricant;
    }

    public void setFabricant(String fabricant) {
        this.fabricant = fabricant;
    }

    public String getCote() {
        return cote;
    }

    public void setCote(String cote) {
        this.cote = cote;
    }

    public Collection<String> getConsoles() {
        return consoles;
    }

    public void setConsoles(Collection<String> consoles) {
        this.consoles = consoles;
    }

    /**
     * Fin des méthodes get et set pour tous les paramètres de la classe
     */
}