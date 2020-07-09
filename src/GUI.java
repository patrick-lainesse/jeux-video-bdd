///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						GUI.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						Général: docs.oracle.com
//								JOptionPane: https://stackoverflow.com/questions/15853112/joptionpane-yes-no-option/15853127
//                              Menu et actions: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/ActionDemoProject/src/misc/ActionDemo.java
//////////////////////////////////////////////////////////////////////////////

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.border.TitledBorder;

// TODO: on ne peut pas sélectionner certaines options tant que pas charger une bdd
// TODO: Créer une fonction pour lancer un dialog box avec les infos du jeu lorsqu'on double clique un jeu dans le tableau
@SuppressWarnings("DanglingJavadoc")
public class GUI extends JFrame {

    /**
     * Panneaux principaux où s'affichent les différents éléments graphiques de l'appli.
     * Déclarés ici pour permettre aux différentes méthodes d'interagir avec eux lorsque nécessaire.
     */
    private final JMenuBar menu;
    private final Container container;  // Contenant de la fenêtre principale de l'application
    JPanel formParent;              // Panel qui reçoit le panel du formulaire, permet plus de flexibilité dans le layout
    JPanel panelFormulaire;         // Affiche le formulaire pour saisir les informations d'un jeu
    JScrollPane tableauScrollPane;  // Recçoit le tableau pour afficher les informations sur les jeux
    private Bdd baseDeDonnees;

    /**
     * Éléments de formulaire pouvant se retrouver dans le programme à un moment ou un autre.
     */
    CustomTxtField tfFabricant;
    CustomTxtField tfTitre;
    RadioPanel radioPanelRecherche;

    /**
     * Texte des options des menus
     * TODO: Réécrire en deux enum, un par sous-menu, qui associe texte et action (éventuellement icône, etc.)
     */
    private static final String BDD = "Base de donn\u00e9es";
    private static final String RECHERCHE = "Recherche";

    private static final String CHARGER = "Charger nouvelle base de donn\u00e9es";
    private static final String RAFRAICHIR = "Actualiser l'affichage de la base de donn\u00e9es";
    private static final String AJOUT_FICHIER = "Ajouter fichier de base de donn\u00e9es";
    private static final String RECHERCHE_JEU = "Rechercher un jeu";
    private static final String AJOUT_JEU = "+ Ajouter un nouveau jeu";
    private static final String RECHERCHE_PAR_CONSOLE = "Afficher les jeux par console";
    private static final String RECHERCHE_PAR_COTE = "Afficher les jeux par cote";
    private static final String RECHERCHE_PAR_FABRICANT = "Afficher les jeux par fabricant";
    private static final String ENREGISTRER = "Enregistrer sous...";
    private static final String QUITTER = "Quitter";

    public static final String TITRE_CONTENU_BDD = "Jeux contenus dans la base de donn\u00e9es";
    public static final String TITRE_AJOUT_JEU = "Ajouter un jeu";
    public static final String TITRE_RECH_JEU = "Rechercher un jeu";
    public static final String TITRE_RECH_CONSOLES = "Afficher les jeux pour cette console";
    public static final String TITRE_RECH_COTE = "Afficher les jeux par cote";
    public static final String TITRE_RESULTAT = "R\u00E9sultat de la recherche";

    private static final String ANNULE = "Annul\u00e9 par l'utilisateur.";
    private static final String ATTENTION = "Charger une base de donn\u00E9es \u00E0 partir d'un fichier .txt " +
            "entra\u00EEnera une perte des modifications non enregistr\u00E9es sur la base de donn\u00E9es.";

    /* Crée la fenêtre pour afficher le programme et initialise le menu principal.
     * Initialisé avec un container principal vide. */
    public GUI(String titre) {

        // Ouvrir le programme pour qu'il occupe les trois quarts de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 3 / 4, screenSize.height * 3 / 4);

        setTitle(titre);
        container = getContentPane();

        // Envoyer le exit code 0 lorsque l'application est fermée par le bouton du coin supérieur droit.
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }
                          }
        );

        // Changer le texte des boutons "Oui" et "Non" des boîtes de dialogue
        UIManager.put("JOptionPane.cancelButtonText", "nope");
        UIManager.put(JOptionPane.TOOL_TIP_TEXT_KEY, "yup");

        // Créer et afficher la barre de menus
        menu = creerMenu();
        setJMenuBar(menu);
        setVisible(true);
    }

    /* Crée la barre de menu. Chaque item du menu est associé à une Action pour clarifier l'organisation du code
     * tout en facilitant l'association des méthodes à des raccourcis clavier (et icones, boutons, etc si nécessaire)
     * @return  Un component JMenuBar entièrement rempli */
    public JMenuBar creerMenu() {

        // Tableaux d'actions, un par menu principal de la barre de menu
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
                new ActionRechParCote(),
                new ActionRechParFabricant()
        };

        JMenuItem menuItem;
        JMenuBar menuBar;
        menuBar = new JMenuBar();

        // Créer les menus principaux
        JMenu menuBaseDeDonnees = new JMenu(BDD);
        JMenu menuRecherche = new JMenu(RECHERCHE);

        // Ajoute les actions (et donc leurs descriptions) à la barre de menu
        for (Action action : actionsBdd) {
            menuItem = new JMenuItem(action);
            menuBaseDeDonnees.add(menuItem);
        }

        for (Action action : actionsRecherche) {
            menuItem = new JMenuItem(action);
            menuRecherche.add(menuItem);
        }

        menuBar.add(menuBaseDeDonnees);
        menuBar.add(menuRecherche);
        return menuBar;
    }

    /**
     * ***************************** ACTIONS DU MENU ***********************************************************
     * Section des actions déclenchées par des sélections du menu ou la combinaison de clés qui leur sont associées.
     */
    /* Ouvre une boîte de dialogue invitant l'utilisateur à sélectionner un fichier txt où se trouve une base de
     * données, puis la fait afficher dans un tableau dans l'écran principal. Affiche un message d'erreur en cas d'échec. */
    public class ActionCharger extends AbstractAction {
        public ActionCharger() {
            super(CHARGER);
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            /* Vérifier qu'il n'y a pas déjà une base de données chargée pour avertir que les données non sauvegardées
                pourraient être perdues */
            int reponse = JOptionPane.YES_OPTION;

            if (baseDeDonnees != null) {
                Object[] options = {"J'ai dit: charger!", "Ah non, alors!"};
                reponse = JOptionPane.showOptionDialog(null,
                        ATTENTION,
                        "Attention",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
            }

            if (reponse == JOptionPane.YES_OPTION) {
                String fichier = choisirFichier();
                if (!fichier.equals(ANNULE)) {
                    baseDeDonnees = new Bdd();
                    try {
                        baseDeDonnees.loadBdd(fichier);
                        afficherBdd();
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Erreur lors de la lecture du fichier.");
                    }
                }
            } else {
                System.out.println(ANNULE);
            }
        }
    }

    /* Permet d'ajouter un nouveau fichier de base de données à celui déjà chargé. */
    public class ActionAjoutFichier extends AbstractAction {
        public ActionAjoutFichier() {
            super(AJOUT_FICHIER);
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, 2));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                String fichierAjout = choisirFichier();
                if (!fichierAjout.equals(ANNULE)) {
                    baseDeDonnees.addBdd(fichierAjout);
                } else System.out.println(ANNULE);
                afficherBdd();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(new JFrame(),
                        "Erreur lors de la lecture du fichier.");
            }
        }
    }

    /* Affiche la base de données et les dernières modifications qui ont pu lui être apportées. */
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

    /* Fait apparaître formulaire pour saisir les données d'un nouveau jeu à ajouter à la base de données.
     * Au clic sur le bouton, le jeu est ajouté à la base de données. */
    public class ActionAjoutJeu extends AbstractAction {
        public ActionAjoutJeu() {
            super(AJOUT_JEU);
            putValue(MNEMONIC_KEY, KeyEvent.VK_PLUS);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 2));
        }

        public void actionPerformed(ActionEvent e) {
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
            panelFormulaire.add(new CheckBoxPanel(Jeu.Attributs.CONSOLES));

            BoutonFlow bouton = new BoutonFlow(new ActionBtnAjoutJeu());
            formParent.add(bouton, BorderLayout.EAST);

            setVisible(true);
            JOptionPane.showMessageDialog(new JFrame(),
                    "Jeu ajout\u00E9 \u00E0 la base de donn\u00E9es.");
        }
    }

    /* Enregistrer la base de données dans un fichier. */
    public class ActionEnregistrer extends AbstractAction {
        public ActionEnregistrer() {
            super(ENREGISTRER);
            putValue(DISPLAYED_MNEMONIC_INDEX_KEY, 12);
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
                try {
                    baseDeDonnees.saveBdd(nomFichier);
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Le jeu a \u00E9t\u00E9 ajout\u00E9 \u00E0 la base de donn\u00E9es.");
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Erreur lors de l'enregistrement du jeu.");
                }
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

    /* Fait apparaître un formulaire pour saisir un fabricant et le titre d'un jeu. Au clic sur le bouton,
     * lance la recherche dans la base de donnée et affiche les informations sur le jeu s'il est trouvé. */
    public class ActionRechJeu extends AbstractAction {
        public ActionRechJeu() {
            super(RECHERCHE_JEU);
            putValue(MNEMONIC_KEY, KeyEvent.VK_J);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, 2));
        }

        public void actionPerformed(ActionEvent e) {

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
    }

    /* Affiche une nouvelle fenêtre avec une série de boutons radio invitant l'utilisateur à sélectionner une console.
     * Affiche ensuite dans un tableau les infos des jeux disponibles sur la console sélectionnée. */
    public class ActionRechParConsole extends AbstractAction {
        public ActionRechParConsole() {
            super(RECHERCHE_PAR_CONSOLE);
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, 2));
        }

        public void actionPerformed(ActionEvent e) {
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
    }

    /* Affiche une nouvelle fenêtre avec une série de boutons radio invitant l'utilisateur à sélectionner une cote.
     * Affiche ensuite dans un tableau les infos des jeux disponibles pour la cote sélectionnée. */
    public class ActionRechParCote extends AbstractAction {
        public ActionRechParCote() {
            super(RECHERCHE_PAR_COTE);
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, 2));
        }

        public void actionPerformed(ActionEvent e) {
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
    }

    /* Affiche un text field pour saisir un fabricant et afficher la liste de jeux
     * publiés par ce fabricant. Aucun raccourcé clavier n'est associé à cette action. */
    public class ActionRechParFabricant extends AbstractAction {
        public ActionRechParFabricant() {
            super(RECHERCHE_PAR_FABRICANT);
        }

        public void actionPerformed(ActionEvent e) {

            // Zone de texte pour saisir le nom du fabricant
            preparerFormulaire(TITRE_RECH_JEU);

            // Ajouter TextField pour saisir les infos du jeu à rechercher
            tfFabricant = new CustomTxtField(Jeu.Attributs.FABRICANT.getAttribut());
            panelFormulaire.add(tfFabricant);

            // Ajouter un bouton pour lancer la recherche
            BoutonFlow bouton = new BoutonFlow(new ActionBtnRechFabricant());
            formParent.add(bouton, BorderLayout.EAST);

            setVisible(true);
        }
    }
    /*****************************************************************************************************
     * Méthodes potentiellement réutilisables dans les actions
     *****************************************************************************************************/
    /* Parcourt la base de données pour créer un vecteur permettant l'appel du constructeur JTable pour afficher en tableau. */
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

    /* Saisit le nom du fichier sélectionné par l'utilisateur et son emplacement.
     * @return	Le path absolu du fichier sélectionné, en String. */
    public String choisirFichier() {
        final JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(menu);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();

            return fichier.getAbsolutePath();
        } else return ANNULE;
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
        final JTextField jTextField;

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
     * Classe pour gérer les checkboxes.
     * Crée un JPanel pour un groupe de checkboxes, permettant la sélection de plusieurs consoles.
     */
    private static class CheckBoxPanel extends JPanel {

        private List<JCheckBox> listeCB = new LinkedList<>();

        /* Constructeur du panel
         * @param	Une des valeurs de l'enum des Attributs de jeu (consoles), lequel contient
         *          à son tour un enum contenant les différentes consoles possibles */
        public CheckBoxPanel(Jeu.Attributs attribut) {

            JPanel panel = new JPanel();
            JLabel jLabel = new JLabel(attribut.getAttribut());
            panel.add(jLabel);

            // Ajouter un checkbox pour chaque option possible de l'enum correspondant dans la classe Jeu
            for (Enum item : attribut.getValues()) {
                JCheckBox checkbox = new JCheckBox(item.toString(),false);
                panel.add(checkbox);
                listeCB.add(checkbox);
            }
            add(panel);
        }

        /* Retourne la cote associée au bouton sélectionné.
         * @return	La cote sélectionnée pour le jeu à créer */
        public ArrayList<String> getChoix() {

            ArrayList<String> choix = null;

            for (JCheckBox checkbox: listeCB) {
                if (checkbox.isSelected()) {
                    choix.add(checkbox.getText());
                }

            }
            return choix;
        }
    }

    /**
     * Classe pour gérer les boutons radio.
     * Crée un JPanel pour un groupe de boutons radio selon un attribut enum de la classe Jeu.
     */
    private static class RadioPanel extends JPanel {

        private final ButtonGroup buttonGroup = new ButtonGroup();
        private final JPanel panel;

        /* Constructeur du panel
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
            radioButton.setActionCommand(radioButton.getText());    // TODO: nécessaire avec actions?
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

    /**
     * ***************************** ACTIONS DES BOUTONS ***********************************************************
     * Action associés aux différents boutons du programme.
     * *************************************************************************************************************
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
                JOptionPane.showMessageDialog(new JFrame(),
                        "Aucun jeu ne correspond \u00E0 ces crit\u00E8res dans la base de donn\u00E9es.");
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
                JOptionPane.showMessageDialog(new JFrame(),
                        "Aucun jeu associé à cette console.");
            }
        }
    }

    public class ActionBtnRechCote extends AbstractAction {
        public ActionBtnRechCote() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            String coteCherchee = radioPanelRecherche.getChoix();
            ArrayList<Jeu> listeJeux = baseDeDonnees.chercheCote(coteCherchee);
            if (listeJeux != null) {
                afficherResultat(Jeu.vectoriserArrayList(listeJeux), TITRE_RESULTAT);
            } else {
                JOptionPane.showMessageDialog(new JFrame(),
                        "Aucun jeu associé à cette cote dans la base de données.");
            }
        }
    }

    public class ActionBtnRechFabricant extends AbstractAction {
        public ActionBtnRechFabricant() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {

            Collection<Jeu> listeJeux = baseDeDonnees.getJeuxFabricant(tfFabricant.getText());
            if (listeJeux != null) {
                afficherResultat(Jeu.vectoriserArrayList(listeJeux), TITRE_RESULTAT);
            } else {
                JOptionPane.showMessageDialog(new JFrame(),
                        "Aucun jeu ne correspond à ce fabricant dans la base de données.");
            }
        }
    }

    /**
     * Classe pour gérer les boutons. Crée un JPanel contenant un bouton.
     *
     * @requires formulaire déjà créé (des TextFields) pour que le ActionListener puisse aller chercher les données
     * nécessaires pour lancer la méthode associée.
     */
    private static class BoutonFlow extends JPanel {

        // Texte à afficher sur les différents boutons
        public static final String BTN_AJOUT_JEU = "Ajouter le jeu";
        public static final String BTN_RECHERCHER = "Rechercher";
        // TODO: Un énum qui associe le texte et l'action du bouton pour éviter d'avoir à la passer en paramètre

        /* Constructeur
         * @parm action    L'action associée au bouton */
        public BoutonFlow(Action action) {

            setLayout(new FlowLayout());
            JButton bouton = new JButton(action);
            add(bouton);
        }
    }
}
