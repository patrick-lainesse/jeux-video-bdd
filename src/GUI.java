///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						GUI.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								Utilisation des titre des border: https://www.codota.com/code/java/methods/javax.swing.border.TitledBorder/getTitle
//////////////////////////////////////////////////////////////////////////////

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.border.TitledBorder;

// TODO: Créer une fonction pour lancer un dialog box avec les infos du jeu lorsqu'on double clique un jeu dans le tableau
public class GUI extends JFrame {

    /**
     * Panneaux principaux où s'affichent les différents éléments graphiques de l'appli.
     * Déclarés ici pour permettre aux différentes méthodes d'interagir avec eux lorsque nécessaire.
     */
    private final JMenuBar menu;
    private final Container container;

    // TODO: Réflichir à si c'est plus pertinent que ce soit global ou non.
    JPanel formParent;              // Panel qui reçoit le panel du formulaire, permet plus de flexibilité dans le layout
    JPanel panelFormulaire;         // Affiche un formulaire pour saisir les informations 'un jeu
    JScrollPane tableauScrollPane;  // Affiche un tableau d'informations sur les jeux
    private Bdd baseDeDonnees;

    /**
     * Éléments de formulaires pouvant se retrouver dans le programme à un moment ou un autre.
     */

    CustomTxtField tfFabricant;
    CustomTxtField tfTitre;
    RadioPanel radioPanelRecherche;      // TODO: expliquer qu'il sera utilisé seulement pour les recherches
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
    private static final String RECHERCHE_PAR_CONSOLE = "Afficher les jeux par console";
    private static final String RECHERCHE_PAR_COTE = "Afficher les jeux par cote";
    private static final String ENREGISTRER = "Enregistrer sous...";
    private static final String QUITTER = "Quitter";

    public static final String TITRE_CONTENU_BDD = "Jeux contenus dans la base de donn\u00e9es";
    public static final String TITRE_AJOUT_JEU = "Ajouter un jeu";
    public static final String TITRE_RECH_JEU = "Rechercher un jeu";
    public static final String TITRE_RECH_CONSOLES = "Afficher les jeux pour cette console";
    public static final String TITRE_RECH_COTE = "Afficher les jeux par cote";  // TODO: Duplicata RECHERCHE
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

        menu = creerMenu();
        setJMenuBar(menu);

        setVisible(true);
    }

    // TODO: on ne peut pas sélectionner certaines options tant que pas charger une bdd
    /* TODO: Ancien en-tête
     * Redirige vers les fonctions pertinentes selon la sélection qui est faite dans le menu principal.
     * @param   L'élément qui a été sélectionné. */
    public class ActionCharger extends AbstractAction {
        public ActionCharger() {
            super(CHARGER);
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            String fichier = choisirFichier();
            if (!fichier.equals(ANNULE)) {
                baseDeDonnees = new Bdd();
                baseDeDonnees.loadBdd(fichier);
                afficherBdd();
            } else System.out.println(ANNULE);
        }
    }

    public class ActionRafraichir extends AbstractAction {
        public ActionRafraichir() {
            super(RAFRAICHIR);
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, 2));
        }

        public void actionPerformed(ActionEvent e) {
            afficherBdd();
        }
    }

    public class ActionAjoutFichier extends AbstractAction {
        public ActionAjoutFichier() {
            super(AJOUT_FICHIER);
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, 2));
        }

        public void actionPerformed(ActionEvent e) {
            String fichierAjout = choisirFichier();
            if (!fichierAjout.equals(ANNULE)) {
                baseDeDonnees.addBdd(fichierAjout);
            } else System.out.println(ANNULE);
            afficherBdd();
        }
    }

    public class ActionAjoutJeu extends AbstractAction {
        public ActionAjoutJeu() {
            super(AJOUT_JEU);
            putValue(MNEMONIC_KEY, KeyEvent.VK_PLUS);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 2));
        }

        public void actionPerformed(ActionEvent e) {
            ajouterJeu();
        }
    }

    public class ActionEnregistrer extends AbstractAction {
        public ActionEnregistrer() {
            super(ENREGISTRER);
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 2));
        }

        public void actionPerformed(ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            int selectionne = fileChooser.showSaveDialog(menu);
            String nomFichier;

            if (selectionne == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                nomFichier = fichier.getAbsolutePath();
            } else nomFichier = ANNULE;

            if (!nomFichier.equals(ANNULE)) {
                baseDeDonnees.saveBdd(nomFichier);
                // TODO: Afficher DialoBox pour dire que ça s'est fait
            } else System.out.println(ANNULE);
        }
    }

    public static class ActionQuitter extends AbstractAction {
        public ActionQuitter() {
            super(QUITTER);
            putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, 2));
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public class ActionRechJeu extends AbstractAction {
        public ActionRechJeu() {
            super(RECHERCHE_JEU);
            putValue(MNEMONIC_KEY, KeyEvent.VK_J);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, 2));
        }

        public void actionPerformed(ActionEvent e) {
            formRechercheJeu();
        }
    }

    public class ActionRechParConsole extends AbstractAction {
        public ActionRechParConsole() {
            super(RECHERCHE_PAR_CONSOLE);
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, 2));
        }

        public void actionPerformed(ActionEvent e) {
            frameConsole();
        }
    }

    // TODO: Problème que reste affiché si trouve pas de jeu (avant d'ajouter nouvelle bdd par exemple)
    public class ActionRechParCote extends AbstractAction {
        public ActionRechParCote() {
            super(RECHERCHE_PAR_COTE);
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, 2));
        }

        public void actionPerformed(ActionEvent e) {
            frameCote();
        }
    }

    // TODO: En-tête
    public JMenuBar creerMenu() {

        Action[] actionsBdd = {
                new ActionCharger(),
                new ActionRafraichir(),
                new ActionAjoutFichier(),
                new ActionAjoutJeu(),
                new ActionEnregistrer(),
                new ActionQuitter()
        };

        Action[] actionsRecherche = {
                new ActionRechJeu(),
                new ActionRechParConsole(),
                new ActionRechParCote()
        };

        JMenuItem menuItem = null;
        JMenuBar menuBar;
        menuBar = new JMenuBar();

        // Créer les menus principaux
        JMenu menuBaseDeDonnees = new JMenu(BDD);
        JMenu menuRecherche = new JMenu(RECHERCHE);

        for (int i = 0; i < actionsBdd.length; i++) {
            menuItem = new JMenuItem(actionsBdd[i]);
            menuBaseDeDonnees.add(menuItem);
        }

        for (int i = 0; i < actionsRecherche.length; i++) {
            menuItem = new JMenuItem(actionsRecherche[i]);
            menuRecherche.add(menuItem);
        }

        menuBar.add(menuBaseDeDonnees);
        menuBar.add(menuRecherche);

        return menuBar;
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
        BoutonFlow bouton = new BoutonFlow(new ActionBtnRechJeu());
        formParent.add(bouton, BorderLayout.EAST);

        setVisible(true);
    }

    /* Fait apparaître formulaire pour saisir les données d'un nouveau jeu à ajouter à la base de données.
     * Au clic sur le bouton, le jeu est ajouté à la base de données. */
    // TODO: Afficher message comme quoi ça a fonctionné et afficher le contenu de la bdd à nouveau?
    public void ajouterJeu() {

        preparerFormulaire(TITRE_AJOUT_JEU);

        // Zones de texte pour saisir le fabricant et le titre du jeu
        tfFabricant = new CustomTxtField(Jeu.Attributs.FABRICANT.getAttribut());
        tfTitre = new CustomTxtField(Jeu.Attributs.TITRE.getAttribut());
        panelFormulaire.add(tfFabricant);
        panelFormulaire.add(tfTitre);

        // Boutons radio pour le choix de la cote
        radioPanelRecherche = new RadioPanel(Jeu.Attributs.COTE);
        panelFormulaire.add(radioPanelRecherche);

        // TODO: Liste des consoles en checkbox

        BoutonFlow bouton = new BoutonFlow(new ActionBtnAjoutJeu());
        formParent.add(bouton, BorderLayout.EAST);

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

        formParent = new JPanel();
        formParent.setLayout(new BorderLayout());
        formParent.add(panelFormulaire, BorderLayout.NORTH);
        formParent.setAlignmentX(Component.LEFT_ALIGNMENT);

        titrerPanel(formParent, titre);
        container.add(formParent, BorderLayout.WEST);
    }

    /* Vide le contenant principal de toutes ses composantes */
    public void viderContainer() {
        container.removeAll();
        // TODO:
        //  container.add(Box.createHorizontalStrut(100));
        container.repaint();
    }

    public void frameConsole() {
        JFrame fenetre = new JFrame();

        // Boutons radio pour le choix de la console
        radioPanelRecherche = new RadioPanel(Jeu.Attributs.CONSOLES);
        titrerPanel(radioPanelRecherche, TITRE_RECH_CONSOLES);
        fenetre.add(radioPanelRecherche, BorderLayout.CENTER);

        BoutonFlow bouton = new BoutonFlow(new ActionBtnRechConsole());
        fenetre.add(bouton, BorderLayout.SOUTH);

        fenetre.pack();
        fenetre.setVisible(true);
    }

    public void frameCote() {
        JFrame fenetre = new JFrame();

        // Boutons radio pour le choix de la console
        radioPanelRecherche = new RadioPanel(Jeu.Attributs.COTE);
        titrerPanel(radioPanelRecherche, TITRE_RECH_COTE);
        fenetre.add(radioPanelRecherche, BorderLayout.CENTER);

        BoutonFlow bouton = new BoutonFlow(new ActionBtnRechCote());
        fenetre.add(bouton, BorderLayout.SOUTH);
        fenetre.pack();
        fenetre.setVisible(true);
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
     * Crée un JPanel pour un groupe de boutons radio selon un attribut enum de la classe Jeu.
     */
    private static class RadioPanel extends JPanel {

        private final ButtonGroup buttonGroup = new ButtonGroup();
        private final JPanel panel;

        /* Retourne la cote associée au bouton sélectionné.
         * @param	Une des valeurs de l'enum des Attributs de jeu (consoles ou cotes), lesquels contiennent
         *          à leur tour un enum contenant les différentes consoles ou cotes possibles */
        public RadioPanel(Jeu.Attributs attribut) {

            panel = new JPanel();
            JLabel jLabel = new JLabel(attribut.getAttribut());
            panel.add(jLabel);

            // Ajouter un radio button pour chaque option possible de l'enum correspondant dans la classe Jeu
            for (Enum item : attribut.getValues()) {
                JRadioButton radioButton = new JRadioButton(item.toString());
                ajouterAuPanneau(radioButton);
            }
            add(panel);
        }

        private void ajouterAuPanneau(JRadioButton radioButton) {
            radioButton.setActionCommand(radioButton.getText());
            panel.add(radioButton);
            buttonGroup.add(radioButton);

            // Sélectionner le premier élément par défaut
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

    /**TODO: Réécrire
     * Classe pour ajouter et gérer les clics sur les boutons
     *
     * @requires Un formulaire déjà créé pour pouvoir aller chercher les données.
     */
    public class ActionBtnAjoutJeu extends AbstractAction {
        public ActionBtnAjoutJeu() {
            super(BoutonFlow.BTN_AJOUT_JEU);
        }

        public void actionPerformed(ActionEvent e) {
            Jeu nouveauJeu = new Jeu(tfFabricant.getText(), tfTitre.getText(), radioPanelRecherche.getChoix());
            baseDeDonnees.addJeu(nouveauJeu);
            afficherBdd();
        }
    }

    public class ActionBtnRechJeu extends AbstractAction {
        public ActionBtnRechJeu() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            Jeu jeuCherche = baseDeDonnees.getJeu(tfTitre.getText(), tfFabricant.getText());
            if (jeuCherche != null) {
                Vector<Vector<String>> jeu = new Vector<>();
                jeu.add(jeuCherche.vectoriser());
                afficherResultat(jeu, TITRE_RESULTAT);
            } else {
                // TODO: Implémenter dialogBox pour si non trouvé
            }
        }
    }

    public class ActionBtnRechConsole extends AbstractAction {
        public ActionBtnRechConsole() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            String consoleCherchee = radioPanelRecherche.getChoix();
            ArrayList<Jeu> listeJeux = baseDeDonnees.chercheConsole(Jeu.Consoles.getAbbreviation(consoleCherchee));

            if (listeJeux != null) {
                afficherResultat(Jeu.vectoriserArrayList(listeJeux), TITRE_RESULTAT);
            } else {
                // TODO: Implémenter dialogBox pour si non trouvé
            }
        }
    }

    public class ActionBtnRechCote extends AbstractAction {
        public ActionBtnRechCote() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            String coteCherchee = radioPanelRecherche.getChoix();
            ArrayList<Jeu> liste = baseDeDonnees.chercheCote(coteCherchee);
            afficherResultat(Jeu.vectoriserArrayList(liste), TITRE_RESULTAT);
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
        // TODO: Un énum qui associe le texte et l'action du bouton?
        // TODO: cette classe semble inutile avec les actions...

        private JButton bouton;

        /* Constructeur
         * @parm action    L'action associée au bouton */
        public BoutonFlow(Action action) {

            setLayout(new FlowLayout());
            JButton bouton = new JButton(action);
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
}
