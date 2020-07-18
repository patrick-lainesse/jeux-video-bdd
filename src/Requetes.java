import java.sql.*;

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

    // TODO: constructeur??

    // TODO: en-tête
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

    private static void deconnecter() {
        try {
            connexion.close();
        } catch (SQLException throwables) {
            // TODO: Message d'erreur
            throwables.printStackTrace();
        }
    }

    // TODO: en-tête
    public static void delete() {

        String requete = "DELETE from jeu";

        try {
            connecter();
            Statement statement = connexion.createStatement();
            statement.execute(requete);
            statement.close();
            deconnecter();
        } catch (SQLException throwables) {
            // TODO: message erreur
            throwables.printStackTrace();
        } finally {
            // TODO: Pourrait retourner un String à GUI...
        }
    }

    // TODO: return boolean?
    public static void inserer(Jeu jeuCourant) {

        String requete = "INSERT INTO jeu VALUES (?,?,?,?)";

        try {
            connecter();
            //Statement statement = connexion.createStatement();
            PreparedStatement preparedStatement = connexion.prepareStatement(requete);
            // TODO: implémenter un getter dans Jeu qui retourne les attributs dans un tableau et mettre ici un for loop
            preparedStatement.setString(1, jeuCourant.getFabricant());
            preparedStatement.setString(2, jeuCourant.getTitre());
            preparedStatement.setString(3, jeuCourant.getCote());
            preparedStatement.setString(4, jeuCourant.printConsoles());
            preparedStatement.executeQuery();
            deconnecter();
        } catch (SQLException throwables) {
            // TODO: message d'erreur
            throwables.printStackTrace();
        }
    }
}
