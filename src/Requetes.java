import java.sql.*;
import java.util.*;

// TODO: connection pool
public class Requetes {

    // TODO: à changer sur DESI
    public static final String DRIVER = "org.mariadb.jdbc.Driver";
    //public static final String DRIVER = "com.mysql.jdbc.Driver";
    //public static final String URL = "jdbc:mysql://localhost";
    public static final String URL = "jdbc:mariadb://localhost:3306/lainessp_jeu";

    public static final String USERNAME = UserData.login;
    public static final String PASSWORD = UserData.passwd;

    private static Connection connexion;

    // Sert à stocker des jeux pour ajouter à la base de données ou pour accueillir temporairement les résultats
    // des requêtes SQL
    private Bdd baseDeDonnees;

    /* Ouvre une connexion à la base de données pour permettre une requête. */
    private static void connecter() {
        // TODO: ne semble pas nécessaire pour les versions récentes de Java, vérifier sur DESI
        /*try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // TODO, ça commence à être trop de try/catch
            e.printStackTrace();
        }*/

        {
            try {
                connexion = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                //connexion.close();
            } catch (SQLException throwables) {
                // TODO exception
                throwables.printStackTrace();
            } finally {
                // TODO: nécessaire?
            }
        }
    }

    /* Ferme la connexion à la base de données. */
    // TODO: Possible de forcer une déconnexion dès qu'il y a eu connexion? Thread peut-être?
    private static void deconnecter() {
        try {
            connexion.close();
        } catch (SQLException throwables) {
            // TODO: Message d'erreur GUI
            throwables.printStackTrace();
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
            // TODO: message d'erreur GUI
            throwables.printStackTrace();
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
                // TODO: message d'erreur GUI
                throwables.printStackTrace();
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
            // TODO: message erreur GUI
            throwables.printStackTrace();
        } finally {
            // TODO: Pourrait retourner un String à GUI...
        }

        // Ajouter chaque au LinkedHashSet
        try {
            while (resultSet.next()) {
                listeJeux.add(extraireParametres(resultSet));
            }
        } catch (SQLException throwables) {
            // TODO: message erreur GUI
            throwables.printStackTrace();
        } finally {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                // TODO: message erreur GUI
                throwables.printStackTrace();
            }
        }

        return listeJeux;
    }

    // TODO: définir mes propre exception pour pouvoir utiliser avec GUI?
    /* Recherche un jeu dans la base de données
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
            throwables.printStackTrace();
            // TODO: message d'erreur GUI
        }

        if (resultSet != null) {
            while (resultSet.next()) {

                // Récupérer la liste des consoles et convertir en ArrayList pour utiliser le constructeur de la classe Jeu
                String[] tableauConsoles = resultSet.getString("console").split(",");
                List<String> listConsoles = Arrays.asList(tableauConsoles);

                // TODO: constructeur prenant un resultset en paramètre
                // Construire un objet Jeu pour passer comme résultat de la méthode
                resultat = new Jeu(resultSet.getString("fabricant"),
                        resultSet.getString("nom"),
                        resultSet.getString("cote"),
                        listConsoles);
            }
            resultSet.close();
        }
        return resultat;
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
            throwables.printStackTrace();
            // TODO: message d'erreur
        }
    }

    /* Élimine toutes les entrées présentes dans la table jeu.
     * La requête DROP TABLE n'est pas utilisée car ne fonctionne pas sur le serveur de la DESI.
     * TODO: tester drop table sur DESI */
    // TODO: return boolean pour que GUI affiche un message d'erreur
    public static void effacer() {

        String requete = "DELETE from jeu";

        try {
            connecter();
            Statement statement = connexion.createStatement();
            statement.execute(requete);
            statement.close();
            deconnecter();
        } catch (SQLException throwables) {
            // TODO: message erreur GUI
            throwables.printStackTrace();
        } finally {
            // TODO: Pourrait retourner un String à GUI...
        }
    }

    // TODO: Pour appeler le constructeur de Jeu sans avoir à lui faire gérer les SQL Exceptions
    private static Jeu extraireParametres(ResultSet uneEntree) {

        Jeu nouveauJeu = null;

        // Récupérer la liste des consoles et convertir en ArrayList pour utiliser le constructeur de la classe Jeu
        String[] tableauConsoles = new String[0];
        try {
            tableauConsoles = uneEntree.getString("console").split(",");
            List<String> listConsoles = Arrays.asList(tableauConsoles);

            // TODO: constructeur prenant un resultset en paramètre
            // Construire un objet Jeu pour passer comme résultat de la méthode
            nouveauJeu = new Jeu(uneEntree.getString("fabricant"),
                    uneEntree.getString("nom"),
                    uneEntree.getString("cote"),
                    listConsoles);
        } catch (SQLException throwables) {
            // TODO: message erreur
            throwables.printStackTrace();
        }
        return nouveauJeu;
    }
}
