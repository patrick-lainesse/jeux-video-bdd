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
    private JFrame jFrame;
    private Bdd baseDeDonnees;

    public GUI(int largeur, int hauteur, String titre) {

        setTitle(titre);
        setSize(largeur, hauteur);

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
        setVisible(true);   // remplace show(), deprecated depuis JDK 1.5
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        //((JMenuItem)e.getSource()).getText();

        final JFileChooser fileChooser = new JFileChooser();

        //if (((JMenuItem)e.getSource()).getText() == "Charger base de donn\u00e9es") {
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
                System.out.println("Les infos sur Vampyr : ");
                afficherJeu(baseDeDonnees, "FOCUS", "Vampyr");

                String[] columnNames = {"First Name",
                        "Last Name",
                        "Sport",
                        "# of Years",
                        "Vegetarian"};

                //Its data is initialized and stored in a two-dimensional Object array:
                Object[][] data = {
                        {"Kathy", "Smith",
                                "Snowboarding", 5, Boolean.FALSE},
                        {"John", "Doe",
                                "Rowing", 3, Boolean.TRUE},
                        {"Sue", "Black",
                                "Knitting", 2, Boolean.FALSE},
                        {"Jane", "White",
                                "Speed reading", 20, Boolean.TRUE},
                        {"Joe", "Brown",
                                "Pool", 10, Boolean.FALSE}
                };

                //Then the Table is constructed using these data and columnNames:
                JTable table = new JTable(data, columnNames);
                table.setBounds(30,40,200,300);
                JScrollPane scrollPane = new JScrollPane(table);
                jFrame = new JFrame();
                jFrame.add(scrollPane);
                jFrame.setSize(300,400);
                jFrame.setVisible(true);

                /*add(scrollPane);

                table.setFillsViewportHeight(true);
                JPanel container = new JPanel();

                container.setLayout(new BorderLayout());
                container.add(table.getTableHeader(), BorderLayout.PAGE_START);
                container.add(table, BorderLayout.CENTER);

                add(container, BorderLayout.SOUTH);*/

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
        new GUI(250, 300, "Boutique de jeux");
    }
}
