///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	TP1.java
// Fichier:						Jeu.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								https://www.journaldev.com/878/java-write-to-file
//////////////////////////////////////////////////////////////////////////////

import java.util.*;

// pour la recherche, on pourrait aussi construire un objet bidon
// et donc prévoir des constructeurs pour objets bidons selon tel paramètre ???
public class Jeu implements Comparable<Jeu> {

    private String fabricant;
    private String titre;
    private String cote;
    private Collection<String> consoles;

    public Jeu(String fabricant, String titre, String cote) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        consoles = new LinkedHashSet<>();
    }

    /* Ajoute une console à la liste des consoles du jeu.
     * On peut simplement ajouter des consoles à la liste sans vérifier si elles s'y retrouvent déjà, car
     * l'ajout d'un élément déjà présent dans un LinkedHashSet ne change pas l'ordre des éléments s'y trouvant.
     * @parm console    Nom de la console à ajouter à la liste */
    // ??? à tester
    // prévoir une méthode pour ajouter plusieurs consoles??? penser à addAll JavaDocs ???
    public void addConsole(String console) {
        consoles.add(console);
    }

    /* toString pour une liste de consoles associées au jeu, facilite l'écriture à l'écran ou dans un fichier txt
     * format: PS4,XONE,PC,MAC,SWITCH */
    private String printConsoles(Collection<String> consoles) {

        StringBuilder liste = new StringBuilder();
        for (String console : consoles) {
            liste.append(console).append(",");
        }
        if (liste.length() > 0) {
            liste = new StringBuilder(liste.substring(0, liste.length() - 1));
        }
        return liste.toString();
    }
    // vérifier si possibilité de bug si on n'ajoute pas de liste de consoles à un jeu????

    /*****************************************************************************************************
     * REDÉFINITIONS
     * Méthodes pour permettre différentes opérations sur la base de données (tri, recherche, etc.)
     *****************************************************************************************************/
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

    /* Nécessaire puisque equals est redéfinie et les jeux seront placés dans un LinkedHashSet
     * Seuls le fabricant et le titre des jeux sont utilisés, soient les même paramètres utilisés
     * pour la comparaison avec equals() */
    @Override
    public int hashCode() {
        return fabricant.hashCode() * 444 + titre.hashCode() * 19;
    }

    /*****************************************************************************************************
     * GET & SET
     * Méthodes get et set pour les paramètres de la classe
     *****************************************************************************************************/
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
}