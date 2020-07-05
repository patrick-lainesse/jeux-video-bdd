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
    private Bdd baseDeDonnees;

    /** Texte constant des options des menus */
    private static final String BDD = "Base de donn\u00e9es";
    private static final String RECHERCHE = "Recherche";
    private static final String CHARGER = "Charger base de donn\u00e9es";
    private static final String AFFICHER = "Afficher";
    private static final String AJOUT_FICHIER = "Ajouter fichier de base de donn\u00e9es";

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


    @Override
    public void actionPerformed(ActionEvent e) {

        //TODO
        // ((JMenuItem)e.getSource()).getText();

        final JFileChooser fileChooser = new JFileChooser();

        switch (((JMenuItem) e.getSource()).getText()) {

            //case "Charger base de donn\u00e9es":
            case CHARGER:
                int returnVal = fileChooser.showOpenDialog(menu);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File fichier = fileChooser.getSelectedFile();
                    baseDeDonnees = new Bdd();
                    baseDeDonnees.loadBdd(fichier.getAbsolutePath());
                    // TODO: Afficher message comme quoi ça a fonctionné
                } else {
                    System.out.println("Annul\u00e9 par l'utilisateur.");
                }
                break;

            case AFFICHER:
                // Construction de la table à partir de la base de données
                Vector<String> nomColonnes = new Vector<>();
                nomColonnes.add("Titre");
                nomColonnes.add("Fabricant");
                nomColonnes.add("Cote");
                nomColonnes.add("Consoles");

                Vector<Vector<String>> lignes = baseDeDonnees.vectoriser();

                JTable table = new JTable(lignes, nomColonnes);


                JScrollPane scrollPane = new JScrollPane(table);

                container.add(scrollPane, BorderLayout.CENTER);
                setVisible(true);
                break;

            case AJOUT_FICHIER:
                break;
        }
    }

    // Pour faire afficher un jeu.  À modifier pour le tp2
    public static void afficherJeu(TestInterface b, String fab, String titre) {
        Jeu aAfficher = b.getJeu(titre, fab);

        if (aAfficher != null)
            System.out.println(aAfficher);
        else
            System.out.println(titre + " n'est pas dans la banque de données");
    }

    public static void main(String[] args) {
        new GUI("Boutique de jeux");
    }
}
