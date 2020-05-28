///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	TP1.java
// Fichier:						Jeu.java
// Session:						�t� 2020
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
    * On peut simplement ajouter des consoles � la liste sans v�rifier si elles s'y retrouvent d�j�, car selon
    * la documentation d'Oracle, le rajout d'un �l�ment d�j� pr�sent dans un LinkedHashSet ne change pas l'ordre existant
    */
    // ??? � tester
    public void addConsole(String console) {
        consoles.add(console);
    }

    /**
     * Red�finition des m�thodes pour permettre l'impl�mentation de l'interface Comparable,
     * afin de permettre le tri des jeux dans la base de donn�es
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
// hashcode � red�finir?????

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
    // v�rifier si possibilit� de bug si on n'ajoute pas de liste de consoles � un jeu????


    /**
     * D�but des get et set pour tous les param�tres de la classe
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
     * Fin des m�thodes get et set pour tous les param�tres de la classe
     */
}