import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

public class GUI extends JFrame implements ActionListener {

    private final JMenuBar menu;
    private final Container container;

    JScrollPane tableauScrollPane = new JScrollPane();
    private Bdd baseDeDonnees;

    /**
     * Texte des options des menus
     */
    private static final String BDD = "Base de donn\u00e9es";
    private static final String RECHERCHE = "Recherche";
    private static final String CHARGER = "Charger base de donn\u00e9es";
    private static final String AFFICHER = "Afficher";
    private static final String AJOUT_FICHIER = "Ajouter fichier de base de donn\u00e9es";

    /**
     * Messages pouvant s'afficher dans le programme
     */
    private static final String ANNULE = "Annul\u00e9 par l'utilisateur.";

    // TODO: En-tête
    public GUI(String titre) {

        // Ouvrir le programme pour qu'il occupe les trois quarts de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 3 / 4, screenSize.height * 3 / 4);

        setTitle(titre);
        container = getContentPane();
        container.setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }
                          }
        );

        // Créer une barre de menu et les différents menus principaux
        menu = new JMenuBar();
        JMenu menuBaseDonnees = new JMenu(BDD);
        JMenu menuRecherche = new JMenu(RECHERCHE);

        // Ajouter des options au menu base de données
        menuBaseDonnees.add(new JMenuItem(CHARGER));
        menuBaseDonnees.add(new JMenuItem(AFFICHER));
        menuBaseDonnees.add(new JMenuItem(AJOUT_FICHIER));

        // Ajouter des écouteurs d'événements sur les options du menu
        for (int i = 0; i < menuBaseDonnees.getItemCount(); i++) {
            menuBaseDonnees.getItem(i).addActionListener(this);
        }

        // Ajouter les menus à la barre de menus
        menu.add(menuBaseDonnees);
        menu.add(menuRecherche);
        menu.addNotify();   // TODO: Comprendre à quoi sert cette ligne

        setJMenuBar(menu);
        setVisible(true);
    }

    // TODO: En-tête
    @Override
    public void actionPerformed(ActionEvent e) {

        //final JFileChooser fileChooser = new JFileChooser();

        // Déterminer quelle option du menu a été sélectionnée
        switch (((JMenuItem) e.getSource()).getText()) {
            case CHARGER:
                String fichier = choisirFichier();
                if (fichier != ANNULE) {
                    baseDeDonnees = new Bdd();
                    baseDeDonnees.loadBdd(fichier);
                } else System.out.println(ANNULE);
                afficherBdd();
                break;

            case AFFICHER:
                afficherBdd();
                break;

            case AJOUT_FICHIER:
                String fichierAjout = choisirFichier();
                if (fichierAjout != ANNULE) {
                    baseDeDonnees.addBdd(fichierAjout);
                } else System.out.println(ANNULE);
                afficherBdd();
                break;
        }
    }

    // TODO: En-tête
    // Construction de la table à partir de la base de données
    public void afficherBdd() {

        if (tableauScrollPane.getParent() != null) {
            container.remove(tableauScrollPane);
        }

        // TODO: Mieux de sortir ce code d'ici pour pas le refaire à chaque fois
        Vector<String> nomColonnes = new Vector<>();
        nomColonnes.add("Titre");
        nomColonnes.add("Fabricant");
        nomColonnes.add("Cote");
        nomColonnes.add("Consoles");

        Vector<Vector<String>> lignes = baseDeDonnees.vectoriser();

        // TODO: Problème de superposition
        JTable table = new JTable(lignes, nomColonnes);
        tableauScrollPane = new JScrollPane(table);

        container.add(tableauScrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    public String choisirFichier() {
        final JFileChooser fileChooser = new JFileChooser();

        int returnVal = fileChooser.showOpenDialog(menu);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();

            return fichier.getAbsolutePath();
        } else return ANNULE;
    }

    public static void main(String[] args) {
        new GUI("Boutique de jeux vidéo");
    }
}
