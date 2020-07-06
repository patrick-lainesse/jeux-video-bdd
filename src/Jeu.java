///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	TP1.java
// Fichier:						Jeu.java
// Session:						�t� 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								https://www.journaldev.com/878/java-write-to-file
//////////////////////////////////////////////////////////////////////////////

import java.util.*;

public class Jeu implements Comparable<Jeu> {

    final private String fabricant;
    final private String titre;
    final private String cote;
    final private Collection<String> consoles;

    /*****************************************************************************************************
     * CONSTANTES STRING
     * enum pour stocker des constantes String qui sont r�utilis�es � plusieurs endroits dans le code.
     * *** Section ajout�e pour le TP2 *** TODO: Ajouter au Readme
     *****************************************************************************************************/

    // TODO: utiliser dans Jeu.java et Bdd.java
    public enum AttributsJeu {
        FABRICANT("Fabricant"),
        TITRE("Titre"),
        COTE("Cote"),
        CONSOLES("Consoles");

        private final String attribut;

        AttributsJeu(String attribut) {
            this.attribut = attribut;
        }

        public String getAttribut() {
            return attribut;
        }
    }

    public enum Cotes {
        E("E"),
        PG("PG"),
        T("T"),
        M("M");

        private String cote;

        Cotes(String cote) {
            this.cote = cote;
        }

        public String getCote() {
            return cote;
        }
    }


    public Jeu(String fabricant, String titre, String cote) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        consoles = new LinkedHashSet<>();
    }

    /* Ajoute une console � la liste des consoles du jeu.
     * On peut simplement ajouter des consoles � la liste sans v�rifier si elles s'y retrouvent d�j�, car
     * l'ajout d'un �l�ment d�j� pr�sent dans un LinkedHashSet ne change pas l'ordre des �l�ments s'y trouvant.
     *
     * @parm console    Nom de la console � ajouter � la liste */
    public void addConsole(String console) {
        consoles.add(console);
    }

    /* M�thode toString() pour une liste de consoles associ�es au jeu, facilite l'�criture � l'�cran ou dans un fichier txt
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

    /*****************************************************************************************************
     * RED�FINITIONS
     * M�thodes pour permettre diff�rentes op�rations sur la base de donn�es (tri, recherche, etc.)
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
        //Format d'un jeu: EA;NHL 2020;E;PS4,WIIU,XONE,PC
        return fabricant + ";" + titre + ";" + cote + ";" + printConsoles(consoles);
    }

    /* N�cessaire puisque equals est red�finie et les jeux seront plac�s dans un LinkedHashSet
     * Seuls le fabricant et le titre des jeux sont utilis�s, soient les m�me param�tres utilis�s
     * pour la comparaison avec equals() */
    @Override
    public int hashCode() {
        return fabricant.hashCode() * 444 + titre.hashCode() * 19;
    }

    /*****************************************************************************************************
     * GET & SET
     *****************************************************************************************************/
    public String getTitre() {
        return titre;
    }

    public String getFabricant() {
        return fabricant;
    }

    public String getCote() {
        return cote;
    }

    public Collection<String> getConsoles() {
        return consoles;
    }

    /*****************************************************************************************************
     * Fonction utiles pour transposer � l'interface graphique (GUI)
     *****************************************************************************************************/

    /* Place chacun des attribut du jeu dans un vecteur de String, afin de faciliter son ajout sous forme d'une
     * ligne dans un JTable.
     *
     * @return	Un vecteur String contenant chacun des attributs d'un jeu */
    public Vector<String> vectoriser() {

        Vector<String> ligne = new Vector<>();
        ligne.add(this.fabricant);
        ligne.add(this.titre);
        ligne.add(this.cote);
        ligne.add(this.printConsoles(this.getConsoles()));

        return ligne;
    }
}