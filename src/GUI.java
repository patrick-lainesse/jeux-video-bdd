///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						GUI.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/ActionDemoProject/src/misc/ActionDemo.java
//////////////////////////////////////////////////////////////////////////////

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.border.TitledBorder;

// TODO: Créer une fonction pour lancer un dialog box avec les infos du jeu lorsqu'on double clique un jeu dans le tableau
public class GUI extends JFrame implements ActionListener {

    /**
     * Panneaux principaux où s'affichent les différents éléments graphiques de l'appli.
     * Déclarés ici pour permettre aux différentes méthodes d'interagir avec eux lorsque nécessaire.
     */
    private final JMenuBar menu;
    private final Container container;

    // TODO: Réflichir à si c'est plus pertinent que ce soit global ou non.
    JPanel panelNorth;
    JPanel panelFormulaire;              // Affiche un formulaire pour saisir les informations 'un jeu
    JScrollPane tableauScrollPane;  // Affiche un tableau d'informations sur les jeux
    private Bdd baseDeDonnees;

    /**
     * Éléments de formulaires pouvant se retrouver dans le programme à un moment ou un autre.
     */

    CustomTxtField tfFabricant;
    CustomTxtField tfTitre;
    // TODO: ajouter les autres possibles

    /**
     * Texte des options des menus
     * TODO: Enum à la place? + séparer menus des items des menus
     */
/*
    public enum DescMenu {
         BDD("Base de donn\u00e9es"),
         RECHERCHE("Recherche"),
         CHARGER("Charger nouvelle base de donn\u00e9es"),
         RAFRAICHIR("Actualiser l'affichage de la base de donn\u00e9es"),
         AJOUT_FICHIER("Ajouter fichier de base de donn\u00e9es"),
         RECHERCHE_JEU("Rechercher un jeu"),
         AJOUT_JEU("+ Ajouter un nouveau jeu");

        private static String description;
        //private final String description;

        DescMenu(String description) {
            this.description = description;
        }

        public static String getDescription() {
            return description;
        }
    }*/
    private static final String BDD = "Base de donn\u00e9es";
    private static final String RECHERCHE = "Recherche";

    private static final String CHARGER = "Charger nouvelle base de donn\u00e9es";
    private static final String RAFRAICHIR = "Actualiser l'affichage de la base de donn\u00e9es";
    private static final String AJOUT_FICHIER = "Ajouter fichier de base de donn\u00e9es";
    private static final String RECHERCHE_JEU = "Rechercher un jeu";
    private static final String AJOUT_JEU = "+ Ajouter un nouveau jeu";
    public static final String RECHERCHE_CONSOLE = "Afficher les jeux par console";

    public static final String BTN_AJOUT_JEU = "Ajouter le jeu";
    public static final String BTN_RECHERCHER = "Rechercher";

    public static final String TITRE_CONTENU_BDD = "Jeux contenus dans la base de donn\u00e9es";
    public static final String TITRE_AJOUT_JEU = "Ajouter un jeu";
    public static final String TITRE_RECH_JEU = "Rechercher un jeu";
    public static final String TITRE_RESULTAT = "R\u00E9sultat de la recherche";


    /**
     * Messages pouvant s'afficher dans le programme
     */
    private static final String ANNULE = "Annul\u00e9 par l'utilisateur.";
    // TODO: Boîte de dialogue pour avertir que possible de perdre des données non sauvegardées

    /* Crée la fenêtre pour afficher le programme et initialise le menu principal.
     * Initialisé avec un container principal vide. */
    public GUI(String titre) {

        // Ouvrir le programme pour qu'il occupe les trois quarts de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 3 / 4, screenSize.height * 3 / 4);


        setTitle(titre);
        container = getContentPane();

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }
                          }
        );

        // TODO: créer une classe ou fonction pour générer le menu
        // Créer une barre de menu et les différents menus principaux
        menu = new JMenuBar();
        JMenu menuBaseDonnees = new JMenu(BDD);
        JMenu menuRecherche = new JMenu(RECHERCHE);
        JMenuItem menuItemCourant; // servira à ajouter les différents items aux menus

        menuItemCourant = new JMenuItem(CHARGER, KeyEvent.VK_N);
        menuItemCourant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuBaseDonnees.add(menuItemCourant);

        menuItemCourant = new JMenuItem(RAFRAICHIR);
        menuItemCourant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 2));
        menuBaseDonnees.add(menuItemCourant);

        menuItemCourant = new JMenuItem(AJOUT_FICHIER, KeyEvent.VK_F);
        menuItemCourant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, 2));
        menuBaseDonnees.add(menuItemCourant);

        menuItemCourant = new JMenuItem(AJOUT_JEU, KeyEvent.VK_PLUS);
        menuItemCourant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 2));
        menuBaseDonnees.add(menuItemCourant);

        menuItemCourant = new JMenuItem(RECHERCHE_JEU, KeyEvent.VK_J);
        menuItemCourant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, 2));
        menuRecherche.add(menuItemCourant);

        menuItemCourant = new JMenuItem(RECHERCHE_CONSOLE, KeyEvent.VK_M);
        menuItemCourant.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, 2));
        menuRecherche.add(menuItemCourant);

        // Ajouter des écouteurs d'événements sur les options du menu
        for (int i = 0; i < menuBaseDonnees.getItemCount(); i++) {
            menuBaseDonnees.getItem(i).addActionListener(this);
        }

        for (int i = 0; i < menuRecherche.getItemCount(); i++) {
            menuRecherche.getItem(i).addActionListener(this);
        }

        // Ajouter les menus à la barre de menus
        menu.add(menuBaseDonnees);
        menu.add(menuRecherche);

        setJMenuBar(menu);
        setVisible(true);

        // TODO: on ne peut pas sélectionner certaines options tant que pas charger une bdd
        //menuItemCourant.setEnabled(false);
        //menuItemCourant.setDisabledTextColor(Color.black);
    }

    /* Redirige vers les fonctions pertinentes selon la sélection qui est faite dans le menu principal.
     * @param   L'élément qui a été sélectionné. */
    @Override
    public void actionPerformed(ActionEvent e) {

        // Déterminer quelle option du menu a été sélectionnée
        switch (((JMenuItem) e.getSource()).getText()) {
            case CHARGER:
                String fichier = choisirFichier();
                if (!fichier.equals(ANNULE)) {
                    baseDeDonnees = new Bdd();
                    baseDeDonnees.loadBdd(fichier);
                    afficherBdd();
                } else System.out.println(ANNULE);
                break;
            case RAFRAICHIR:
                afficherBdd();
                break;
            case AJOUT_FICHIER:
                String fichierAjout = choisirFichier();
                if (!fichierAjout.equals(ANNULE)) {
                    baseDeDonnees.addBdd(fichierAjout);
                } else System.out.println(ANNULE);
                afficherBdd();
                break;
            case RECHERCHE_JEU:
                formRechercheJeu();
                break;
            case AJOUT_JEU:
                ajouterJeu();
                break;
            case RECHERCHE_CONSOLE:
                frameConsole();
                break;
        }
    }

    /* Parcourt la base de données pour l'ajouter à un vecteur et l'afficher dans un tableau. */
    public void afficherBdd() {
        Vector<Vector<String>> lignes = baseDeDonnees.vectoriser();
        afficherResultat(lignes, TITRE_CONTENU_BDD);
    }

    /* Crée un tableau qui prend toute la largeur du container principal pour y afficher
     * le résultat d'une recherche de jeu(x). */
    public void afficherResultat(Vector<Vector<String>> listeJeux, String titre) {

        // Crée l'en-tête du tableau
        Vector<String> nomColonnes = new Vector<>();
        for (Jeu.Attributs attribut : Jeu.Attributs.values()) {
            nomColonnes.add(attribut.getAttribut());
        }

        // Crée une table pour afficher le tableau
        JTable table = new JTable(listeJeux, nomColonnes);

        viderContainer();
        tableauScrollPane = new JScrollPane(table);
        titrerPanel(tableauScrollPane, titre);

        container.add(tableauScrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    /* Méthode qui saisit le fichier sélectionné par l'utilisateur
     * @return	Le path absolu du fichier sélectionné, en String. */
    public String choisirFichier() {
        final JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(menu);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();

            return fichier.getAbsolutePath();
        } else return ANNULE;
    }

    /* Fait apparaître un formulaire pour saisir un fabricant et le titre d'un jeu.
     * Fait également apparaître un bouton qui, lorsque cliqué, lance la recherche dans la base de données.
     * Affiche ensuite les informations sur le jeu s'il est trouvé. */
    public void formRechercheJeu() {

        preparerFormulaire(TITRE_RECH_JEU);

        // Ajouter TextField pour saisir les infos du jeu à rechercher
        tfFabricant = new CustomTxtField(Jeu.Attributs.FABRICANT.getAttribut());
        tfTitre = new CustomTxtField(Jeu.Attributs.TITRE.getAttribut());

        panelFormulaire.add(tfFabricant);
        panelFormulaire.add(tfTitre);

        // Ajouter un bouton pour lancer la recherche
        BoutonFlow bouton = new BoutonFlow(BTN_RECHERCHER);
        panelNorth.add(bouton, BorderLayout.EAST);

        setVisible(true);
    }

    /* Fait apparaître formulaire pour saisir les données d'un nouveau jeu à ajouter à la base de données.
     * Au clic sur le bouton, le jeu est ajouté à la base de données. */
    // TODO: Afficher message comme quoi ça a fonctionné et afficher le contenu de la bdd à nouveau?
    public void ajouterJeu() {

        preparerFormulaire(TITRE_AJOUT_JEU);

        // TextField pour saisir le fabricant et le titre du jeu
        CustomTxtField tfFabricant = new CustomTxtField(Jeu.Attributs.FABRICANT.getAttribut());
        CustomTxtField tfTitre = new CustomTxtField(Jeu.Attributs.TITRE.getAttribut());
        panelFormulaire.add(tfFabricant);
        panelFormulaire.add(tfTitre);

        // Boutons radio pour le choix de la cote
        RadioPanel radioPanel = new RadioPanel(Jeu.Attributs.COTE);
        panelFormulaire.add(radioPanel);

        BoutonFlow bouton = new BoutonFlow(BTN_AJOUT_JEU);
        panelNorth.add(bouton, BorderLayout.EAST);

        setVisible(true);
    }

    /* Ajoute un titre et en encadré pour un panel.
     * @param component     Le panneau sur lequel on veut apposer un titre
     * @param titre         Le titre à afficher */
    public void titrerPanel(JComponent component, String titre) {
        component.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), titre, TitledBorder.LEFT,
                TitledBorder.TOP));
    }

    /* Prépare le container principal pour y insérer des éléments de formulaires.
     *  Éléments positionnés:
     *           - panelFormulaire: Panneau contenant les zones de textes, boutons radio, etc, inséré dans le panelNorth
     *           - panelNorth: Panneau situé au haut du container principal.
     *                         Les boutons sont placés après le panneau du formulaire.
     * @param titre     Le titre qui s'affiche au haut du cadre du formulaire */
    public void preparerFormulaire(String titre) {
        viderContainer();

        panelFormulaire = new JPanel();
        panelFormulaire.setLayout(new BoxLayout(panelFormulaire, BoxLayout.PAGE_AXIS));
        panelFormulaire.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelNorth = new JPanel();
        panelNorth.setLayout(new BorderLayout());
        panelNorth.add(panelFormulaire, BorderLayout.NORTH);
        panelNorth.setAlignmentX(Component.LEFT_ALIGNMENT);

        titrerPanel(panelNorth, titre);
        container.add(panelNorth, BorderLayout.WEST);
    }

    /* Vide le contenant principal de toutes ses composantes */
    public void viderContainer() {
        container.removeAll();
        container.repaint();
    }

    public void frameConsole() {
        JFrame fenetre = new JFrame();

        // Boutons radio pour le choix de la console
        RadioPanel panel = new RadioPanel(Jeu.Attributs.CONSOLES);
        fenetre.add(panel);
        fenetre.pack();
        fenetre.setVisible(true);

        // TODO: Ajouter bouton, rendu ici???
    }

    public static void main(String[] args) {
        new GUI("Boutique de jeux vid\u00e9o");
    }

    /*****************************************************************************************************
     * Classes personnalisées pour gérer certains éléments graphiques réutilisables.
     *****************************************************************************************************/

    /**
     * Classe pour gérer les TextField ainsi que les labels qui leur sont associés
     * Crée un JPanel contenant un textfield et un label associé.
     */
    private static class CustomTxtField extends JPanel {

        public final Dimension TXT_FIELD_SIZE = new Dimension(700, 20);
        JTextField jTextField;

        /* Constructeur
         * @parm nomLabel    Le texte du label qui s'affichera à côté du JTextField */
        public CustomTxtField(String nomLabel) {

            JPanel panel = new JPanel();
            JLabel jLabel = new JLabel(nomLabel);

            panel.add(jLabel, BorderLayout.LINE_START);
            jTextField = new JTextField();
            jTextField.setColumns(15);
            jTextField.setMaximumSize(TXT_FIELD_SIZE);
            panel.add(jTextField, BorderLayout.LINE_END);

            add(panel);
        }

        /* Retourne le texte saisi dans le textField.
         * @return	La cote sélectionnée pour le jeu à créer */
        public String getText() {
            return jTextField.getText();
        }
    }

    /**
     * Classe pour gérer les boutons radio.
     * Crée un JPanel pour un groupe de boutons radio selon un attribut de la classe Jeu.
     */
    private static class RadioPanel extends JPanel {

        private final ButtonGroup buttonGroup = new ButtonGroup();
        private final JPanel panel;

        /* Retourne la cote associée au bouton sélectionné.
         * @param	Une des valeurs de l'enum des attributs de jeu (consoles ou cotes), lesquels contiennent
         *          à leur tour un enum contenant les différentes consoles ou cotes possibles */
        public RadioPanel(Jeu.Attributs attribut) {

            panel = new JPanel();

            JLabel jLabel = new JLabel(attribut.getAttribut());
            panel.add(jLabel);

            // Ajouter un radio button pour chaque option possible de l'enum correspondant dans la classe Jeu
            for (Enum item : attribut.getValues()) {
                JRadioButton radioButton = new JRadioButton(item.toString());
                System.out.println(item.toString());
                panel.add(radioButton);
            }
            add(panel);
        }

        private void ajouterAuPanneau(JRadioButton radioButton) {
            radioButton.setActionCommand(radioButton.getText());
            panel.add(radioButton);
            buttonGroup.add(radioButton);

            // Sélectionner la cote "E" par défaut
            if (buttonGroup.getSelection() == null) {
                buttonGroup.setSelected(radioButton.getModel(), true);
            }
        }

        /* Retourne la cote associée au bouton sélectionné.
         * @return	La cote sélectionnée pour le jeu à créer */
        public String getChoix() {
            ButtonModel model = buttonGroup.getSelection();
            return model.getActionCommand();
        }
    }

    /**
     * Classe pour gérer les boutons. Crée un JPanel contenant un bouton.
     *
     * @requires Un formulaire déjà créé (des TextFields) pour que le ActionListener puisse aller chercher les données
     * nécessaires pour lancer la méthode associée.
     */
    private class BoutonFlow extends JPanel {

        // Texte à afficher sur les différents boutons
        public static final String BTN_AJOUT_JEU = "Ajouter le jeu";
        public static final String BTN_RECHERCHER = "Rechercher";

        private JButton bouton;

        /* Constructeur
         * @parm nomBouton    Le texte qui s'affichera sur le bouton */
        public BoutonFlow(String nomBouton) {

            setLayout(new FlowLayout());
            JButton bouton = new JButton(nomBouton);
            bouton.addActionListener(new ClickListener());
            add(bouton);
        }

        /* @return	Le texte qui s'affiche sur le bouton */
        public String getText() {
            return bouton.getText();
        }

        public JButton getBouton() {
            return bouton;
        }
    }

    /**
     * Classe pour ajouter et gérer les clics sur les boutons
     *
     * @requires Un formulaire déjà créé pour pouvoir aller chercher les données.
     */
    private class ClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Identifier le bouton qui a déclenché l'action listener
            JButton button = (JButton) e.getSource();

            switch (button.getText()) {
                case BTN_AJOUT_JEU:
                    break;
                case BTN_RECHERCHER:
                    Jeu jeuCherche = baseDeDonnees.getJeu(tfTitre.getText(), tfFabricant.getText());
                    if (jeuCherche != null) {
                        Vector<Vector<String>> jeu = new Vector<>();
                        jeu.add(jeuCherche.vectoriser());
                        afficherResultat(jeu, TITRE_RESULTAT);
                    } else {
                        // TODO: Implémenter dialogBox pour si non trouvé
                    }
                    break;
            }
        }
    }
}
