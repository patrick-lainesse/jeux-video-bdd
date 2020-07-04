import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends Frame implements ActionListener {

    private MenuBar menu;

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
        menu = new MenuBar();
        MenuItem menuItemCourant;       // servira à ajouter les différents éléments au menu
        Menu baseDonnees = new Menu("Base de donn\u00e9es");
        Menu jeux = new Menu("Recherche");

        // Option charger base de données à partir d'un fichier
        menuItemCourant = new MenuItem("Charger base de donn\u00e9es");
        menuItemCourant.addActionListener(this);

        // Ajouts au menu base de données
        baseDonnees.add(menuItemCourant);

        // Ajouts des sous-menus à la barre de menu
        menu.add(baseDonnees);
        menu.add(jeux);
        menu.addNotify();

        setMenuBar(menu);
        setVisible(true);   // remplace show(), deprecated depuis JDK 1.5
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new GUI(250, 300, "Boutique de jeux");
    }
}
