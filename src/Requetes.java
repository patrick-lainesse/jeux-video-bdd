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
            // TODO: Message d'erreur GUI
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
            // TODO: message d'erreur
            throwables.printStackTrace();
        }

        // Si le jeu est déjà dans la base de données, ajouter les nouvelles consoles s'il y a lieu
        // TODO: penser au cas où la cote est différente!
        if (jeuDB != null) {
            Collection<String> nouvellesConsoles = nouveauJeu.getConsoles();

            for (String console: nouvellesConsoles) {
                jeuDB.addConsole(console);
            }

            // TODO: requête update DB pour les nouveaux jeux
        }

        else {
            try {
                connecter();
                //Statement statement = connexion.createStatement();
                PreparedStatement preparedStatement = connexion.prepareStatement(requete);
                // TODO: implémenter un getter dans Jeu qui retourne les attributs dans un tableau et mettre ici un for loop
                preparedStatement.setString(1, nouveauJeu.getFabricant());
                preparedStatement.setString(2, nouveauJeu.getTitre());
                preparedStatement.setString(3, nouveauJeu.getCote());
                preparedStatement.setString(4, nouveauJeu.printConsoles());
                preparedStatement.executeQuery();
                deconnecter();
            } catch (SQLException throwables) {
                // TODO: message d'erreur
                throwables.printStackTrace();
            }
        }
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
            deconnecter();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            // TODO: message d'erreur
        }

        if (resultSet != null) {
            while (resultSet.next()) {

                // Récupérer la liste des consoles et convertir en ArrayList pour utiliser le constructeur de la classe Jeu
                String[] tableauConsoles = resultSet.getString("console").split(",");
                List<String> listConsoles = Arrays.asList(tableauConsoles);

                // Construire un objet Jeu pour passer comme résultat de la méthode
                resultat = new Jeu(resultSet.getString("fabricant"),
                        resultSet.getString("nom"),
                        resultSet.getString("cote"),
                        listConsoles);
            }
        }
        return resultat;
    }
}
