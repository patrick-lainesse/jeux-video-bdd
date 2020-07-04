import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.*;

public class GUI extends JFrame implements ActionListener {

    private JMenuBar menu;
    private Container container;
    private Bdd baseDeDonnees;
    Dimension screenSize;

    public GUI(String titre) {

        // Ouvrir en quart d'écran
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 3 / 4, screenSize.height * 3 / 4);

        setTitle(titre);

        container = getContentPane();
        //container.setLayout(new FlowLayout());
        container.setLayout(new BorderLayout());


        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }
                          }
        );

        // Barre de menu de l'application
        menu = new JMenuBar();
        JMenuItem menuItemCourant;       // servira à ajouter les différents éléments au menu
        JMenu baseDonnees = new JMenu("Base de donn\u00e9es");
        JMenu jeux = new JMenu("Recherche");

        // Option charger base de données à partir d'un fichier
        menuItemCourant = new JMenuItem("Charger base de donn\u00e9es");
        menuItemCourant.addActionListener(this);

        // Ajout au menu base de données
        baseDonnees.add(menuItemCourant);

        // Option afficher la base de données chargée
        menuItemCourant = new JMenuItem("Afficher");
        menuItemCourant.addActionListener(this);
        baseDonnees.add(menuItemCourant);

        // Ajouts des sous-menus à la barre de menu
        menu.add(baseDonnees);
        menu.add(jeux);
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

            case "Charger base de donn\u00e9es":
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

            case "Afficher":
                String[] colonnes = {"Titre", "Fabricant", "Cote", "Consoles"};
                Object[][] data = new Object[50][4];    // TODO: Trouver manière adaptative

                int x = 0;
                for (Collection<Jeu> collectionJeux : baseDeDonnees.getBaseDeDonnees().values()) {
                    for (Jeu jeu : collectionJeux) {
                        data[x][0] = jeu.getTitre();
                        data[x][1] = jeu.getFabricant();
                        data[x][2] = jeu.getCote();
                        data[x][3] = "";

                        Collection<String> consoles = jeu.getConsoles();
                        for (String console : consoles) {
                            data[x][3] += console + ", ";
                        }
                        x++;
                    }
                }

                // Construction de la table à partir de la base de données
                JTable table = new JTable(data, colonnes);
                JScrollPane scrollPane = new JScrollPane(table);

                container.add(scrollPane, BorderLayout.CENTER);
                setVisible(true);
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
