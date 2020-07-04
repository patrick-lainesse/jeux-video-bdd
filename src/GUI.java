import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame implements ActionListener {

    private JMenuBar menu;

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

        // Ajouts au menu base de données
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
        //In response to a button click:
        int returnVal = fileChooser.showOpenDialog(menu);

        Bdd baseDeDonnees = new Bdd();
        //baseDeDonnées.loadBdd(fichierBdd);

    }

    public static void main(String[] args) {
        new GUI(250, 300, "Boutique de jeux");
    }
}
