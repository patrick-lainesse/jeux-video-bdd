import java.sql.*;
import java.util.*;

public class Requetes {

    // TODO: à changer sur DESI
    public static final String DRIVER = "org.mariadb.jdbc.Driver";
    //public static final String DRIVER = "com.mysql.jdbc.Driver";
    //public static final String URL = "jdbc:mysql://localhost";
    public static final String URL = "jdbc:mariadb://localhost:3306/lainessp_jeu";

    public static final String USERNAME = UserData.login;
    public static final String PASSWORD = UserData.passwd;

    public static final String ERR_CONNEXION = "Impossible de se connecter \u00E0 la base de donn\u00E9es.";
    public static final String ERR_DECONNEXION = "Un probl\u00E8me est survenu lors de la d\u00E9connexion \u00E0 la base de donn\u00E9es.";
    public static final String ERR_INSERER = "Un problème est survenu lors de l'insertion dans la base de données.";
    public static final String ERR_LECTURE_DB = "Probl\u00E8me lors de la lecture dans la base de donn\u00E9es";
    public static final String ERR_VIDER = "La base de donn\u00E9es n'a pu \u00EAtre vid\u00E9e correctement.";

    private static Connection connexion;

    /* Ouvre une connexion à la base de données pour permettre une requête. */
    private static void connecter() {
        // TODO: ne semble pas nécessaire pour les versions récentes de Java, vérifier sur DESI, sinon faire dans même try
        /*try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        {
            try {
                connexion = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException throwables) {
                System.out.println("connecter(): " + throwables.getMessage());
                GUI.messageErreur(ERR_CONNEXION);
            }
        }
    }

    /* Ferme la connexion à la base de données. */
    private static void deconnecter() {
        try {
            connexion.close();
        } catch (SQLException throwables) {
            System.out.println("deconnecter(): " + throwables.getMessage());
            GUI.messageErreur(ERR_DECONNEXION);
        }
    }

    /* Vérifie si le fabricant et/ou le jeu existe déjà dans la base de données.
     * Si le jeu s'y retrouve déjà, ajout des consoles à l'entrée existante.
     * Sinon, le jeu est ajouté à la base de données.
     *
     * @param unJeu		Le jeu à ajouter à la base de données */
    public static void inserer(Jeu nouveauJeu) {

        String requete = "INSERT INTO jeu VALUES (?,?,?,?)";
        Jeu jeuDB = null;

        // Vérifier si le jeu est déjà présent dans la base de données
        try {
            jeuDB = getJeu(nouveauJeu.getTitre(), nouveauJeu.getFabricant());
        } catch (SQLException throwables) {
            System.out.println("inserer - getJeu: " + throwables.getMessage());
            GUI.messageErreur(ERR_INSERER);
        }

        // Si le jeu est déjà dans la base de données, ajouter les nouvelles consoles s'il y a lieu
        if (jeuDB != null) {
            Collection<String> nouvellesConsoles = nouveauJeu.getConsoles();

            for (String console : nouvellesConsoles) {
                jeuDB.addConsole(console);
            }

            // Ajouter les consoles différentes à la base de données
            modifierConsoles(jeuDB);
        } else {
            // Si le jeu n'est pas trouvé, l'ajouter à la base de données
            try {
                connecter();
                PreparedStatement preparedStatement = connexion.prepareStatement(requete);
                preparedStatement.setString(1, nouveauJeu.getFabricant());
                preparedStatement.setString(2, nouveauJeu.getTitre());
                preparedStatement.setString(3, nouveauJeu.getCote());
                preparedStatement.setString(4, nouveauJeu.printConsoles());
                preparedStatement.executeQuery();
                preparedStatement.close();
                deconnecter();
            } catch (SQLException throwables) {
                System.out.println("inserer - query: " + throwables.getMessage());
                GUI.messageErreur(ERR_INSERER);
            }
        }
    }

    /* Effectue une requête à la base de données pour obtenir une liste de tous les jeux.
     *
     * @return		Un LinkedHashSet pour conserver l'ordre des jeux présents dans la base de données. */
    public static LinkedHashSet<Jeu> listerDB() {

        LinkedHashSet<Jeu> listeJeux = new LinkedHashSet<>();
        ResultSet resultSet = null;
        String requete = "SELECT * FROM jeu";

        try {
            connecter();
            Statement statement = connexion.createStatement();
            resultSet = statement.executeQuery(requete);
            statement.close();
            deconnecter();
        } catch (SQLException throwables) {
            System.out.println("listerDB: " + throwables.getMessage());
            GUI.messageErreur(ERR_LECTURE_DB);
        }
        return convertirResultSet(resultSet);
    }

    /* Recherche un jeu dans la base de données.
     *
     * @param titre			Le nom de ce jeu
     * @param fabricant		Le nom du fabricant pour ce jeu
     * @return		        L'objet Jeu correspondant à la recherche, null si non trouvé */
    public static Jeu getJeu(String titre, String fabricant) throws SQLException {

        ResultSet resultSet = null;
        Jeu resultat = null;
        String requete = "SELECT * FROM jeu WHERE nom LIKE ? AND fabricant LIKE ?";

        try {
            connecter();
            PreparedStatement preparedStatement = connexion.prepareStatement(requete);
            preparedStatement.setString(1, titre);
            preparedStatement.setString(2, fabricant);
            resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            deconnecter();
        } catch (SQLException throwables) {
            System.out.println("getJeu: " + throwables.getMessage());
            GUI.messageErreur(ERR_LECTURE_DB);
        }

        if (resultSet != null) {
            while (resultSet.next()) {
                resultat = jeuResultSet(resultSet);
            }
        }
        return resultat;
    }

    /* Effectue une requête paramétrée pour obtenir une sous-liste de jeux dans la base de données.
     *
     * @param attribut		Attribut utilisé pour restreindre la liste de jeux à obtenir de la base de données
     * @param parametre		La valeur de l'attribut à utiliser dans la requête paramétrée
     * @return		        Collection ordonnée contenant la liste des jeux associés à la requête, null si ne s'y trouve pas */
    public static LinkedHashSet<Jeu> obtenirListe(Jeu.Attributs attribut, String parametre) {

        String requete;

        requete = "SELECT * FROM jeu WHERE " + attribut.getEquivalentDB() + " LIKE ?";

        LinkedHashSet<Jeu> listeJeux = new LinkedHashSet<>();
        ResultSet resultSet = null;

        try {
            connecter();
            PreparedStatement preparedStatement = connexion.prepareStatement(requete);
            preparedStatement.setString(1, "%" + parametre + "%");
            resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            deconnecter();
        } catch (SQLException throwables) {
            System.out.println("obtenirListe:s " + throwables.getMessage());
            GUI.messageErreur(ERR_LECTURE_DB);
        }

        return convertirResultSet(resultSet);
    }

    /* Convertit le result set obtenu d'une requête à la base de données en LinkedHashSet de jeux.
     *
     * @param resultSet		Attribut utilisé pour restreindre la liste de jeux à obtenir de la base de données
     * @return		        Collection ordonnée contenant la liste des jeux associés à la requête, null si ne s'y trouve pas */
    private static LinkedHashSet<Jeu> convertirResultSet(ResultSet resultSet) {

        LinkedHashSet<Jeu> listeJeux = new LinkedHashSet<>();

        // Ajouter chaque jeu au LinkedHashSet
        try {
            while (resultSet.next()) {
                listeJeux.add(jeuResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            System.out.println("convertirResultat - add: " + throwables.getMessage());
            GUI.messageErreur(ERR_INSERER);
        } finally {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                System.out.println("convertirResultat - close: " + throwables.getMessage());
                GUI.messageErreur(ERR_DECONNEXION);
            }
        }
        return listeJeux;
    }


    /* Modifie la liste des consoles associées à un jeu déjà présent dans la base de données.
     *
     * @param jeuModifie    Objet jeu contenant la liste de consoles mise à jour */
    public static void modifierConsoles(Jeu jeuModifie) {

        String requete = "UPDATE jeu SET console = ? WHERE nom LIKE ? AND fabricant LIKE ?";

        try {
            connecter();
            PreparedStatement preparedStatement = connexion.prepareStatement(requete);
            preparedStatement.setString(1, jeuModifie.printConsoles());
            preparedStatement.setString(2, jeuModifie.getTitre());
            preparedStatement.setString(3, jeuModifie.getFabricant());
            preparedStatement.executeQuery();
            preparedStatement.close();
            deconnecter();
        } catch (SQLException throwables) {
            System.out.println("modifierConsole: " + throwables.getMessage());
            GUI.messageErreur(ERR_INSERER);
        }
    }

    /* Élimine toutes les entrées présentes dans la table jeu.
     * La requête DROP TABLE n'est pas utilisée car ne fonctionne pas sur le serveur de la DESI.
     * TODO: tester drop table sur DESI */
    public static void viderDB() {

        String requete = "DELETE from jeu";

        try {
            connecter();
            Statement statement = connexion.createStatement();
            statement.execute(requete);
            statement.close();
            deconnecter();
        } catch (SQLException throwables) {
            System.out.println("viderDB: " + throwables.getMessage());
            GUI.messageErreur(ERR_VIDER);
        }
    }

    /* Crée un jeu à partir d'un result set obtenu par une requête SQL.
     *
     * @param uneEntree     Un jeu obtenu en résultats d'une requête SQL
     * @return              Un objet Jeu créé à partir du resultat passé en paramètre */
    private static Jeu jeuResultSet(ResultSet uneEntree) {

        Jeu nouveauJeu = null;

        // Récupérer la liste des consoles et convertir en ArrayList pour utiliser le constructeur de la classe Jeu
        String[] tableauConsoles = new String[0];
        try {
            tableauConsoles = uneEntree.getString("consoles").split(",");
            List<String> listConsoles = Arrays.asList(tableauConsoles);

            // Construire un objet Jeu pour passer comme résultat de la méthode
            nouveauJeu = new Jeu(uneEntree.getString("fabricant"),
                    uneEntree.getString("nom"),
                    uneEntree.getString("cote"),
                    listConsoles);
        } catch (SQLException throwables) {
            System.out.println("extraireParametres: " + throwables.getMessage());
            GUI.messageErreur(ERR_LECTURE_DB);
        }
        return nouveauJeu;
    }
}
