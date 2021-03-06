///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						Jeu.java
// Description:                 Classe pour repr�senter l'objet Jeu
// Session:						�t� 2020
//
// Auteur:						Patrick Lainesse
// Sources:						docs.oracle.com
//								https://www.journaldev.com/878/java-write-to-file
//////////////////////////////////////////////////////////////////////////////

import java.util.*;

public class Jeu implements Comparable<Jeu> {

    final private String fabricant;
    final private String titre;
    final private String cote;
    final private Collection<String> consoles;

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + CONSTANTES STRING
     + enum pour stocker des constantes String qui sont r�utilis�es � plusieurs endroits dans le code.
     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * enum contenant la liste des attributs d'un jeu, ainsi que l'enum qui est associ� � cet attribut lorsqu'il existe.
     * Permet de conserver les String associ�s � l'objet jeu en un seul endroit pour les modifier pour tout le projet.
     */
    public enum Attributs {
        FABRICANT("Fabricant", null, "fabricant"),
        TITRE("Titre", null, "nom"),
        COTE("Cote", Cotes.values(), "cote"),
        CONSOLES("Consoles", Consoles.values(), "consoles");

        private final String attribut;
        final Enum[] values;
        private final String equivalentDB;

        Attributs(String attribut, Enum[] values, String equivalentDB) {
            this.attribut = attribut;
            this.values = values;
            this.equivalentDB = equivalentDB;
        }

        public String getAttribut() {
            return attribut;
        }

        /**
         * Fonction essentielle de cet enum, elle retourne l'�num�ration des attributs associ�s � un des enum contenus
         * dans Jeu.
         *
         * @return Un tableau des valeurs possibles associ�s � l'attribut de Jeu
         */
        public Enum[] getValues() {
            return values;
        }

        public String getEquivalentDB() {
            return equivalentDB;
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
        RP("RP"),
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

        /**
         * Fonction qui retourne l'abbr�viation associ�e au nom d'une console, n�cessaire pour effectuer une
         * recherche dans la base de donn�es ou pour enregistrer la base de donn�es en format .txt
         *
         * @return L'abbr�viation de la console en format String
         */
        public String getAbbreviation() {
            return abbreviation;
        }

        /**
         * Surcharge de la m�thode. Permet d'�tre appel�e statiquement pour comparer un String � l'ensemble
         * des constantes de l'enum Consoles.
         *
         * @return L'abbr�viation de la console en format String
         */
        public static String getAbbreviation(String console) {
            for (Consoles c : Consoles.values()) {
                if (console.equals(c.toString())) return c.abbreviation;
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
    // Constructeur pour cr�er un jeu sans y ajouter de consoles associ�es
    public Jeu(String fabricant, String titre, String cote) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        consoles = new LinkedHashSet<>();
    }

    // Constructeur utilis� lorsque toutes les caract�ristiques d'un jeu sont fournies
    public Jeu(String fabricant, String titre, String cote, Collection<String> consoles) {
        this.fabricant = fabricant;
        this.titre = titre;
        this.cote = cote;
        this.consoles = new LinkedHashSet<>();
        this.consoles.addAll(consoles);
    }

    /**
     * Ajoute une console � la liste des consoles du jeu.
     * On peut simplement ajouter des consoles � la liste sans v�rifier si elles s'y retrouvent d�j�, car
     * l'ajout d'un �l�ment d�j� pr�sent dans un LinkedHashSet ne change pas l'ordre des �l�ments s'y trouvant.
     *
     * @param console    Nom de la console � ajouter � la liste
     */
    public void addConsole(String console) {
        consoles.add(console);
    }

    /**
     * M�thode toString() pour une liste de consoles associ�es au jeu, facilite l'�criture � l'�cran
     * ou dans un fichier txt. Utilis�e comme un getter pour l'autre d�finition de printConsoles().
     *
     * @param consoles Une collection de consoles en String
     * @return Un String sous le format suivant: PS4,XONE,PC,MAC,SWITCH
     */
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

    /**
     * M�thode toString() pour une liste de consoles associ�es au jeu lorsqu'aucun param�tres n'est fourni.
     *
     * @return Un String sous le format suivant: PS4,XONE,PC,MAC,SWITCH
     */
    public String printConsoles() {
        return printConsoles(consoles);
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

    /**
     * N�cessaire puisque equals est red�finie et les jeux seront plac�s dans un LinkedHashSet
     * Seuls le fabricant et le titre des jeux sont utilis�s, soient les m�me param�tres utilis�s
     * pour la comparaison avec equals()
     */
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

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + Fonction pour faciliter la transposition � l'interface graphique (GUI)
     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * Place chacun des attribut du jeu dans un vecteur de String, afin de faciliter son ajout sous forme d'une
     * ligne dans un JTable.
     *
     * @return Un vecteur String contenant chacun des attributs d'un jeu
     */
    public Vector<String> vectoriser() {

        Vector<String> ligne = new Vector<>();
        ligne.add(this.fabricant);
        ligne.add(this.titre);
        ligne.add(this.cote);
        ligne.add(this.printConsoles(this.getConsoles()));

        return ligne;
    }

    /**
     * Ajoute chaque Jeu contenu dans un ArrayList � un vecteur de vecteurs de String,
     * afin de les afficher dans un JTable.
     *
     * @return Un vecteur de vecteurs de String repr�sentant les attributs de chaque jeu
     */
    public static Vector<Vector<String>> vectoriserArrayList(Collection<Jeu> arrayList) {

        Vector<Vector<String>> vecteurJeu = new Vector<>();

        for (Jeu jeu : arrayList) {
            vecteurJeu.add(jeu.vectoriser());
        }

        return vecteurJeu;
    }
}