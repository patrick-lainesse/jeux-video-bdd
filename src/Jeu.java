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

public class Jeu implements Comparable<Jeu> {

    final private String fabricant;
    final private String titre;
    final private String cote;
    final private Collection<String> consoles;

    /*****************************************************************************************************
     * ### Section ajoutée pour le TP2 ###
     *
     * CONSTANTES STRING
     * enum pour stocker des constantes String qui sont réutilisées à plusieurs endroits dans le code.
     *****************************************************************************************************/

    // TODO: utiliser dans Jeu.java et Bdd.java, voir si toujours pertinent avec MySQL
    /**
     * enum contenant la liste des attributs d'un jeu, ainsi que l'enum qui est associé à cet attribut lorsqu'il existe.
     */
    public enum Attributs {
        FABRICANT("Fabricant", null),
        TITRE("Titre", null),
        COTE("Cote", Cotes.values()),
        CONSOLES("Consoles", Consoles.values());

        private final String attribut;
        final Enum[] values;

        Attributs(String attribut, Enum[] values) {
            this.attribut = attribut;
            this.values = values;
        }

        public String getAttribut() {
            return attribut;
        }

        /*  Fonction essentielle de cet enum, elle retourne l'énumération des attributs associés à un des enum contenus
         *  dans Jeu.
         *
         *  @return     Un tableau des valeurs possibles associés à l'attribut de Jeu */
        // TODO: Utiliser Enum Set serait probablement plus performant
        public Enum[] getValues() {
            return values;
        }

        @Override
        public String toString() {
            return attribut;
        }
    }

    public enum Cotes {
        E("E"),
        PG("PG"),
        T("T"),
        M("M");

        private final String cote;

        Cotes(String cote) {
            this.cote = cote;
        }

        public String getCote() {
            return cote;
        }

        @Override
        public String toString() {
            return cote;
        }
    }

    public enum Consoles {
        PC("PC", "PC"),
        GAMECUBE("GameCube", "GAMECUBE"),
        MAC("Mac", "MAC"),
        PS2("Playstation 2", "PS2"),
        PS3("Playstation 3", "PS3"),
        PS4("Playstation 4", "PS4"),
        SWITCH("Switch", "SWITCH"),
        WIIU("Wii U", "WIIU"),
        X360("Xbox 360", "X360"),
        XONE("Xbox One", "XONE");

        private final String console;
        private final String abbreviation;

        Consoles(String console, String abbreviation) {
            this.console = console;
            this.abbreviation = abbreviation;
        }

        @Override
        public String toString() {
            return console;
        }

        public String getConsole() {
            return console;
        }

        /*  Fonction qui retourne l'abbréviation associée au nom d'une console, nécessaire pour effectuer une
         *  recherche dans la base de données ou pour enregistrer la base de données en format .txt
         *
         *  @return     L'abbréviation de la console en format String */
        public String getAbbreviation() {
            return abbreviation;
        }

        /*  Surcharge de la méthode. Permet d'être appelée statiquement pour comparer un String à l'ensemble
         *  des constantes de l'enum Consoles.
         *
         *  @return     L'abbréviation de la console en format String */
        public static String getAbbreviation(String console) {
            for(Consoles c : Consoles.values()) {
                if(console.equals(c.toString())) return c.abbreviation;
            }
            return null;
        }

        public static Collection<String> getAbbreviation(Collection<String> listeConsoles) {

            Collection<String> listeTraduite = new LinkedHashSet<>();

            for (String nomConsole : listeConsoles) {
                listeTraduite.add(Consoles.getAbbreviation(nomConsole));
            }

            return listeTraduite;
        }
    }

    /*****************************************************************************************************
     * CODE PRINCIPAL DE LA CLASSE JEU
     *****************************************************************************************************/
    public Jeu(String fabricant, String titre, String cote) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        consoles = new LinkedHashSet<>();
    }

    public Jeu(String fabricant, String titre, String cote, Collection<String> consoles) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        this.consoles = consoles;
    }

    /* Ajoute une console à la liste des consoles du jeu.
     * On peut simplement ajouter des consoles à la liste sans vérifier si elles s'y retrouvent déjà, car
     * l'ajout d'un élément déjà présent dans un LinkedHashSet ne change pas l'ordre des éléments s'y trouvant.
     *
     * @parm console    Nom de la console à ajouter à la liste */
    // TODO: addConsole, probablement dans une option "modifier jeu" de GUI. Pour l'instant, utilisé par Bdd.addBdd()
    public void addConsole(String console) {
        consoles.add(console);
    }

    /* Méthode toString() pour une liste de consoles associées au jeu, facilite l'écriture à l'écran ou dans un fichier txt
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
        //Format d'un jeu: EA;NHL 2020;E;PS4,WIIU,XONE,PC
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
     * Fonction pour faciliter la transposition à l'interface graphique (GUI)
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

    /* Ajoute chaque Jeu contenu dans un ArrayList à un vecteur de vecteurs de String,
     * afin de les afficher dans un JTable.
     *
     * @return	Un vecteur de vecteurs de String représentant les attributs de chaque jeu */
    //public static Vector<Vector<String>> vectoriserArrayList(ArrayList<Jeu> arrayList) {
    public static Vector<Vector<String>> vectoriserArrayList(Collection<Jeu> arrayList) {

        Vector<Vector<String>> vecteurJeu = new Vector<>();

        for (Jeu jeu: arrayList) {
            vecteurJeu.add(jeu.vectoriser());
        }

        return vecteurJeu;
    }
}