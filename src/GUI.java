///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	GUI.java
// Fichier:						GUI.java
// Session:						Été 2020
// Description:                 Classe qui gère l'interface graphique du logiciel
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						Général: docs.oracle.com
//								JOptionPane: https://stackoverflow.com/questions/15853112/joptionpane+yes+no+option/15853127
//                              Menu et actions: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/ActionDemoProject/src/misc/ActionDemo.java
//////////////////////////////////////////////////////////////////////////////

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import javax.swing.border.TitledBorder;

public class GUI extends JFrame {

    public static final String NOM_LOGICIEL = "Cataloguideo";
    public static final String VERSION = "1.0.3";

    /* Panneaux principaux où s'affichent les différents éléments graphiques de l'appli.
     * Déclarés ici pour permettre aux différentes méthodes d'interagir avec eux lorsque nécessaire. */
    private final JMenuBar menu;
    private final Container container;  // Contenant de la fenêtre principale de l'application
    JPanel formParent;              // Panel qui reçoit le panel du formulaire, permet plus de flexibilité dans le layout
    JPanel panelFormulaire;         // Regroupe les éléments des formulaires pour saisir les informations d'un jeu
    JScrollPane tableauScrollPane;  // Reçoit le tableau pour afficher les informations sur les jeux
    private boolean baseChargee;    // Pour savoir s'il y a déjà une base chargée dans le logiciel

    // Sous+menus de la barre de menu
    private JMenu menuBaseDeDonnees;
    private JMenu menuRecherche;

    /**
     * Éléments de formulaire pouvant se retrouver dans le programme à un moment ou un autre.
     */
    CustomTxtField tfFabricant;
    CustomTxtField tfTitre;
    RadioPanel radioPanelRecherche;
    CheckBoxPanel checkBoxPanelConsoles;

    public static final String ANNULE = "Annul\u00e9 par l'utilisateur.";
    public static final String ATTENTION = "Charger une base de donn\u00E9es \u00E0 partir d'un fichier .txt " +
            "entra\u00EEnera une perte des modifications non enregistr\u00E9es sur la base de donn\u00E9es.";

    /* Crée la fenêtre pour afficher le programme et initialise le menu principal.
     * Initialisé avec un container principal vide. */
    public GUI() {

        // Ouvrir le programme pour qu'il occupe les trois quarts de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 7 / 8, screenSize.height * 7 / 8);

        setTitle(NOM_LOGICIEL);
        container = getContentPane();

        // Envoyer le exit code 0 lorsque l'application est fermée par le bouton du coin supérieur droit.
        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  System.exit(0);
                              }
                          }
        );

        // Créer et afficher la barre de menus
        menu = creerMenu();
        setJMenuBar(menu);
        setVisible(true);
        baseChargee = false;
    }

    /**
     * Texte des options des menus.
     */
    public enum ChoixMenu {
        BDD("Base de donn\u00e9es"),
        RECHERCHE("Recherche"),
        CHARGER("Charger nouvelle base de donn\u00e9es"),
        RAFRAICHIR("Actualiser l'affichage de la base de donn\u00e9es"),
        AJOUT_FICHIER("Ajouter fichier de base de donn\u00e9es"),
        RECHERCHE_JEU("Rechercher un jeu"),
        AJOUT_JEU("+ Ajouter un nouveau jeu"),
        RECHERCHE_PAR_CONSOLE("Afficher les jeux par console"),
        RECHERCHE_PAR_COTE("Afficher les jeux par cote"),
        RECHERCHE_PAR_FABRICANT("Afficher les jeux par fabricant"),
        ENREGISTRER("Enregistrer sous..."),
        QUITTER("Quitter");

        private final String choix;

        ChoixMenu(String choix) {
            this.choix = choix;
        }

        @Override
        public String toString() {
            return choix;
        }
    }

    /**
     * Crée la barre de menu. Chaque item du menu est associé à une Action pour clarifier l'organisation du code
     * tout en facilitant l'association des méthodes à des raccourcis clavier (et icones, boutons, etc si nécessaire)
     *
     * @return Un component JMenuBar entièrement rempli
     */
    public JMenuBar creerMenu() {

        // Tableaux d'actions, un par menu principal de la barre de menu
        Action[] actionsBdd = {
                new ActionRafraichir(),
                new ActionAjoutFichier(),
                new ActionAjoutJeu(),
                new ActionEnregistrer()
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
        menuBaseDeDonnees = new JMenu(ChoixMenu.BDD.toString());
        menuRecherche = new JMenu(ChoixMenu.RECHERCHE.toString());

        /* Ajouter ActionCharger séparément pour qu'elle reste active */
        menuBaseDeDonnees.add(new JMenuItem(new ActionCharger()));

        // Ajoute les actions (et donc leurs descriptions) à la barre de menu, puis les rend désactivées
        for (Action action : actionsBdd) {
            menuItem = new JMenuItem(action);
            menuItem.setEnabled(false);
            menuBaseDeDonnees.add(menuItem);
        }

        /* Ajouter ActionQuitter séparément pour qu'elle reste active */
        menuBaseDeDonnees.add(new JMenuItem(new ActionQuitter()));

        for (Action action : actionsRecherche) {
            menuItem = new JMenuItem(action);
            menuItem.setEnabled(false);
            menuRecherche.add(menuItem);
        }

        menuBar.add(menuBaseDeDonnees);
        menuBar.add(menuRecherche);

        // Ajouter l'option À propos à la barre de menu
        menuBar.add(new JMenuItem(new ActionAPropos()));
        return menuBar;
    }

    /**
     * Rend actives toutes les options du menu. Utilisée lorsque une base de donnée est chargée dans le programme.
     */
    public void activerMenu() {

        int nbMenuItems = menuBaseDeDonnees.getItemCount();
        for (int i = 0; i < nbMenuItems; i++) {
            menuBaseDeDonnees.getItem(i).setEnabled(true);
        }

        nbMenuItems = menuRecherche.getItemCount();
        for (int i = 0; i < nbMenuItems; i++) {
            menuRecherche.getItem(i).setEnabled(true);
        }
    }

    /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + Méthodes reliées à l'affichage dans les différents panels
     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * Ajoute un titre et en encadré pour un panel.
     *
     * @param component Le panneau sur lequel on veut apposer un titre
     * @param titre     Le titre à afficher
     */
    public void titrerPanel(JComponent component, String titre) {
        component.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), titre, TitledBorder.LEFT,
                TitledBorder.TOP));
    }

    /**
     * Vide le contenant principal de toutes ses composantes
     */
    public void viderContainer() {
        container.removeAll();
        container.repaint();
    }

    /**
     * Affiche un message d'erreur dans une boîte de dialogue.
     *
     * @param message Le message à afficher.
     */
    public static void messageErreur(String message) {
        JOptionPane.showMessageDialog(new JFrame(),
                message,
                Titres.TITRE_ERREUR.toString(),
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Parcourt la base de données pour créer un vecteur nécessaire à l'appel du constructeur JTable pour afficher en JTable.
     */
    public void afficherBdd() {
        Vector<Vector<String>> lignes = FichiersTXT.vectoriser();
        afficherResultat(lignes, Titres.TITRE_CONTENU_BDD.toString());
    }

    /**
     * Crée un tableau qui prend toute la largeur du container principal pour y afficher
     * le résultat d'une recherche de jeu(x).
     *
     * @param listeJeux Vecteur de la liste des jeux, donc chaque paramètre est dans un vecteur
     * @param titre     Titre à afficher au haut du panel du tableau
     */
    public void afficherResultat(Vector<Vector<String>> listeJeux, String titre) {

        // Crée l'en+tête du tableau
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

    /**
     * Saisit le nom du fichier sélectionné par l'utilisateur et son emplacement.
     *
     * @return Le path absolu du fichier sélectionné, en String.
     */
    public String choisirFichier() {
        final JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(menu);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fichier = fileChooser.getSelectedFile();
            return fichier.getAbsolutePath();
        } else return ANNULE;
    }

    /**
     * Prépare le container principal pour y insérer des éléments de formulaires.
     * Éléments positionnés:
     * + panelFormulaire: Panneau contenant les zones de textes, boutons radio, etc, inséré dans le panelNorth
     * + panelNorth: Panneau situé au haut du container principal.
     * Les boutons sont placés après le panneau du formulaire.
     *
     * @param titre Le titre qui s'affiche au haut du cadre du formulaire
     */
    public void preparerFormulaire(String titre) {
        viderContainer();

        panelFormulaire = new JPanel();
        GridLayout layout = new GridLayout(0, 1, 0, 0);

        panelFormulaire.setLayout(layout);
        panelFormulaire.setAlignmentX(Component.LEFT_ALIGNMENT);

        formParent = new JPanel();
        formParent.setLayout(new BorderLayout());
        formParent.add(panelFormulaire, BorderLayout.NORTH);
        formParent.setAlignmentX(Component.LEFT_ALIGNMENT);

        titrerPanel(formParent, titre);
        container.add(formParent, BorderLayout.WEST);
    }

    /**
     * Texte s'affichant comme titres des panels.
     */
    public enum Titres {
        TITRE_CONTENU_BDD("Jeux contenus dans la base de donn\u00e9es"),
        TITRE_AJOUT_JEU("Ajouter un jeu"),
        TITRE_RECH_JEU("Rechercher un jeu"),
        TITRE_RECH_CONSOLES("Afficher les jeux par console"),
        TITRE_RECH_COTE("Afficher les jeux par cote"),
        TITRE_RESULTAT("R\u00E9sultat de la recherche"),
        TITRE_ERREUR("Erreur");

        private final String titre;

        Titres(String titre) {
            this.titre = titre;
        }

        @Override
        public String toString() {
            return titre;
        }
    }

    /*+++++++++++++++++++++++++++++++ACTIONS DU MENU++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + Actions déclenchées par des sélections du menu ou la combinaison de clés qui leur sont associées.
     ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    /**
     * Ouvre une boîte de dialogue invitant l'utilisateur à sélectionner un fichier txt où se trouve une base de
     * données, puis la fait afficher dans un tableau dans l'écran principal. Affiche un message d'erreur en cas d'échec.
     */
    public class ActionCharger extends AbstractAction {
        public ActionCharger() {
            super(ChoixMenu.CHARGER.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            /* Vérifier qu'il n'y a pas déjà une base de données chargée pour avertir que les données non sauvegardées
                pourraient être perdues */
            int reponse = JOptionPane.YES_OPTION;

            if (baseChargee) {
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
                    try {
                        FichiersTXT.nouvelleBaseTXT(fichier);
                        afficherBdd();
                        activerMenu();
                        baseChargee = true;
                    } catch (Exception exception) {
                        messageErreur("Erreur lors du chargement de la base de donn\u00E9es.");
                        System.out.println("ActionCharger + actionPerformed: " + exception.getMessage());
                    }
                }
            } else {
                System.out.println(ANNULE);
            }
        }
    }

    /**
     * Permet d'ajouter un nouveau fichier txt de base de données.
     */
    public class ActionAjoutFichier extends AbstractAction {
        public ActionAjoutFichier() {
            super(ChoixMenu.AJOUT_FICHIER.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_F);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, 2));
        }

        public void actionPerformed(ActionEvent e) {
            try {
                String fichierAjout = choisirFichier();
                if (!fichierAjout.equals(ANNULE)) {
                    FichiersTXT.ajouterTXT(fichierAjout);
                } else System.out.println(ANNULE);
                afficherBdd();
            } catch (Exception exception) {
                messageErreur("Erreur lors de l'ajout du fichier.");
                System.out.println("ActionAjoutFichier: " + exception.getMessage());
            }
        }
    }

    /**
     * Affiche la base de données et les dernières modifications qui ont pu lui être apportées.
     */
    public class ActionRafraichir extends AbstractAction {
        public ActionRafraichir() {
            super(ChoixMenu.RAFRAICHIR.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, 2));
        }

        public void actionPerformed(ActionEvent e) {
            afficherBdd();
        }
    }

    /**
     * Fait apparaître formulaire pour saisir les données d'un nouveau jeu à ajouter à la base de données.
     * Au clic sur le bouton, le jeu est ajouté à la base de données.
     */
    public class ActionAjoutJeu extends AbstractAction {
        public ActionAjoutJeu() {
            super(ChoixMenu.AJOUT_JEU.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_PLUS);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 2));
        }

        public void actionPerformed(ActionEvent e) {
            preparerFormulaire(Titres.TITRE_AJOUT_JEU.toString());

            // Zones de texte pour saisir le fabricant et le titre du jeu
            tfFabricant = new CustomTxtField(Jeu.Attributs.FABRICANT.getAttribut());
            tfTitre = new CustomTxtField(Jeu.Attributs.TITRE.getAttribut());
            panelFormulaire.add(tfFabricant);
            panelFormulaire.add(tfTitre);

            // Boutons radio pour le choix de la cote
            radioPanelRecherche = new RadioPanel(Jeu.Attributs.COTE);
            panelFormulaire.add(radioPanelRecherche);

            // Panel de checkboxes pour sélectionner des consoles
            checkBoxPanelConsoles = new CheckBoxPanel(Jeu.Attributs.CONSOLES);
            formParent.add(checkBoxPanelConsoles);

            BoutonFlow bouton = new BoutonFlow(new ActionBtnAjoutJeu());
            formParent.add(bouton, BorderLayout.EAST);

            setVisible(true);
        }
    }

    /**
     * Enregistrer la base de données dans un fichier.
     */
    public class ActionEnregistrer extends AbstractAction {
        public ActionEnregistrer() {
            super(ChoixMenu.ENREGISTRER.toString());
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
                    FichiersTXT.enregistrerTXT(nomFichier);
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Base de donn\u00E9es bien enregistr\u00E9e.");
                } catch (Exception exception) {
                    System.out.println("ActionEnregistrer: " + exception.getMessage());
                    messageErreur("Erreur lors de l'enregistrement du fichier.");
                }
            } else System.out.println(ANNULE);
        }
    }

    public static class ActionQuitter extends AbstractAction {
        public ActionQuitter() {
            super(ChoixMenu.QUITTER.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, 2));
        }

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Fait apparaître un formulaire pour saisir un fabricant et le titre d'un jeu. Au clic sur le bouton,
     * lance la recherche dans la base de donnée et affiche les informations sur le jeu s'il est trouvé.
     */
    public class ActionRechJeu extends AbstractAction {
        public ActionRechJeu() {
            super(ChoixMenu.RECHERCHE_JEU.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_J);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J, 2));
        }

        public void actionPerformed(ActionEvent e) {

            preparerFormulaire(Titres.TITRE_RECH_JEU.toString());

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

    /**
     * Affiche une série de boutons radio invitant l'utilisateur à sélectionner une console.
     * Affiche ensuite dans un tableau les infos des jeux disponibles sur la console sélectionnée.
     */
    public class ActionRechParConsole extends AbstractAction {
        public ActionRechParConsole() {
            super(ChoixMenu.RECHERCHE_PAR_CONSOLE.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_M);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, 2));
        }

        public void actionPerformed(ActionEvent e) {

            preparerFormulaire(Titres.TITRE_RECH_CONSOLES.toString());

            // Boutons radio pour le choix de la console
            radioPanelRecherche = new RadioPanel(Jeu.Attributs.CONSOLES);
            BoutonFlow bouton = new BoutonFlow(new ActionBtnRechConsole());

            panelFormulaire.add(radioPanelRecherche);
            panelFormulaire.add(bouton);
            setVisible(true);
        }
    }

    /**
     * Affiche une série de boutons radio invitant l'utilisateur à sélectionner une cote.
     * Affiche ensuite dans un tableau les infos des jeux disponibles pour la cote sélectionnée.
     */
    public class ActionRechParCote extends AbstractAction {
        public ActionRechParCote() {
            super(ChoixMenu.RECHERCHE_PAR_COTE.toString());
            putValue(MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, 2));
        }

        public void actionPerformed(ActionEvent e) {
            preparerFormulaire(Titres.TITRE_RECH_COTE.toString());

            // Boutons radio pour le choix de la console
            radioPanelRecherche = new RadioPanel(Jeu.Attributs.COTE);
            panelFormulaire.add(radioPanelRecherche);

            BoutonFlow bouton = new BoutonFlow(new ActionBtnRechCote());
            panelFormulaire.add(bouton);

            // Ajuster la taille du formulaire pour que le titre s'affiche au complet
            panelFormulaire.setPreferredSize(new Dimension(200, 200));
            setVisible(true);
        }
    }

    /**
     * Affiche un text field pour saisir un fabricant et afficher la liste de jeux
     * publiés par ce fabricant. Aucun raccourcé clavier n'est associé à cette action.
     */
    public class ActionRechParFabricant extends AbstractAction {
        public ActionRechParFabricant() {
            super(ChoixMenu.RECHERCHE_PAR_FABRICANT.toString());
        }

        public void actionPerformed(ActionEvent e) {

            // Zone de texte pour saisir le nom du fabricant
            preparerFormulaire(Titres.TITRE_RECH_JEU.toString());

            // Ajouter TextField pour saisir les infos du jeu à rechercher
            tfFabricant = new CustomTxtField(Jeu.Attributs.FABRICANT.getAttribut());
            panelFormulaire.add(tfFabricant);

            // Ajouter un bouton pour lancer la recherche
            BoutonFlow bouton = new BoutonFlow(new ActionBtnRechFabricant());
            formParent.add(bouton, BorderLayout.EAST);

            setVisible(true);
        }
    }

    /**
     * Affiche un frame avec les informations sur le programme.
     */
    public static class ActionAPropos extends AbstractAction {

        public static final String A_PROPOS = "\u00C0 propos de Cataloguideo";

        public static final String MSG_A_PROPOS =
                "Cataloguideo\n\n" +
                        "Version : " + VERSION + "\n\n" +
                        "Cataloguideo est un logiciel de gestion de catalogue pour une boutique de jeux vid\u00E9o,\n" +
                        "permettant d'obtenir des informations sur les jeux en stock en un temps rapide\n" +
                        "et avec une interface agr\u00E9able \u00E0 l'oeil et \u00E0 l'utilisation." +
                        "\n\nCopyright \u00A9 1996 Patrick Lainesse.\n" +
                        "Matricule: 740302";

        public ActionAPropos() {
            super(A_PROPOS);
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(new JFrame(), MSG_A_PROPOS);
        }
    }

    /* +++++++++++++++++++++++++++++ ACTIONS DES BOUTONS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + Action associés aux différents boutons du programme.
     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    public class ActionBtnAjoutJeu extends AbstractAction {
        public ActionBtnAjoutJeu() {
            super(BoutonFlow.BTN_AJOUT_JEU);
        }

        public void actionPerformed(ActionEvent e) {
            // Vérifier si des consoles ont été sélectionnées, car il est possible d'ajouter un jeu sans y associer de consoles.
            Collection<String> choixConsoles = checkBoxPanelConsoles.getChoix();
            Jeu nouveauJeu;

            if (choixConsoles.isEmpty()) {
                nouveauJeu = new Jeu(tfFabricant.getText(), tfTitre.getText(), radioPanelRecherche.getChoix());
            } else {
                /* Convertir les choix écrits au long en abbréviation pour comparer à la base de données
                   Par exemple, Playstation 4 devient PS4 */
                choixConsoles = Jeu.Consoles.getAbbreviation(choixConsoles);
                nouveauJeu = new Jeu(tfFabricant.getText(), tfTitre.getText(), radioPanelRecherche.getChoix(), choixConsoles);
            }

            Requetes.inserer(nouveauJeu);

            JOptionPane.showMessageDialog(new JFrame(),
                    "Jeu ajout\u00E9 \u00E0 la base de donn\u00E9es.");
            afficherBdd();
        }
    }

    public class ActionBtnRechJeu extends AbstractAction {
        public ActionBtnRechJeu() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            Jeu jeuCherche = null;

            try {
                jeuCherche = Requetes.getJeu(tfTitre.getText(), tfFabricant.getText());
            } catch (SQLException throwables) {
                messageErreur("Probl\u00E8me lors de la recherche dans la base de donn\u00E9es.");
                System.out.println("ActionBtnRechJeu: " + throwables.getMessage());
            }

            if (jeuCherche != null) {
                Vector<Vector<String>> jeu = new Vector<>();
                jeu.add(jeuCherche.vectoriser());
                afficherResultat(jeu, Titres.TITRE_RESULTAT.toString());
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
            LinkedHashSet<Jeu> listeJeux = Requetes.obtenirListe(Jeu.Attributs.CONSOLES, Jeu.Consoles.getAbbreviation(consoleCherchee));
            afficherResultat(Jeu.vectoriserArrayList(listeJeux), Titres.TITRE_RESULTAT.toString());
        }
    }

    public class ActionBtnRechCote extends AbstractAction {
        public ActionBtnRechCote() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            String coteCherchee = radioPanelRecherche.getChoix();
            LinkedHashSet<Jeu> listeJeux = Requetes.obtenirListe(Jeu.Attributs.COTE, coteCherchee);
            afficherResultat(Jeu.vectoriserArrayList(listeJeux), Titres.TITRE_RESULTAT.toString());
        }
    }

    public class ActionBtnRechFabricant extends AbstractAction {
        public ActionBtnRechFabricant() {
            super(BoutonFlow.BTN_RECHERCHER);
        }

        public void actionPerformed(ActionEvent e) {
            Collection<Jeu> listeJeux = Requetes.obtenirListe(Jeu.Attributs.FABRICANT, tfFabricant.getText());
            afficherResultat(Jeu.vectoriserArrayList(listeJeux), Titres.TITRE_RESULTAT.toString());
        }
    }

    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     + Classes personnalisées pour gérer certains éléments des formulaires.
     +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++/
        /**
         * Classe pour gérer les TextField ainsi que les labels qui leur sont associés
         * Crée un JPanel contenant un textfield et un label associé.
         */
    private static class CustomTxtField extends JPanel {

        public final Dimension TXT_FIELD_SIZE = new Dimension(150, 20);
        final JTextField jTextField;

        /**
         * Constructeur
         *
         * @param nomLabel Le texte du label qui s'affichera à côté du JTextField
         */
        public CustomTxtField(String nomLabel) {

            JPanel panel = new JPanel();
            JLabel jLabel = new JLabel(nomLabel);
            jLabel.setForeground(Color.RED);

            panel.add(jLabel, BorderLayout.LINE_START);
            jTextField = new JTextField();
            jTextField.setColumns(15);
            jTextField.setMaximumSize(TXT_FIELD_SIZE);
            panel.add(jTextField, BorderLayout.LINE_END);

            panel.setBorder(BorderFactory.createEtchedBorder());

            add(panel);
        }

        /**
         * Retourne le texte saisi dans le textField.
         *
         * @return La cote sélectionnée pour le jeu à créer
         */
        public String getText() {
            return jTextField.getText();
        }
    }

    /**
     * Classe pour gérer les checkboxes.
     * Crée un JPanel pour un groupe de checkboxes, permettant la sélection de plusieurs consoles.
     */
    private class CheckBoxPanel extends JPanel {

        private final List<JCheckBox> listeCB = new LinkedList<>();

        /**
         * Constructeur du panel
         *
         * @param attribut Une des valeurs de l'enum des Attributs de jeu (consoles), lequel contient
         *                 à son tour un enum contenant les différentes consoles possibles
         */
        public CheckBoxPanel(Jeu.Attributs attribut) {

            /* Créer un panel pour recevoir seulement les checkbox, placées en deux rangées,
             * et un autre pour recevoir le label. */
            JPanel panel = new JPanel();
            JPanel panelCB = new JPanel();

            GridLayout layout = new GridLayout(0, 2);

            panelCB.setLayout(layout);
            JLabel jLabel = new JLabel(attribut.getAttribut());
            jLabel.setForeground(Color.RED);
            panelCB.add(jLabel);

            // Ajouter un checkbox pour chaque option possible de l'enum correspondant dans la classe Jeu
            for (Enum item : attribut.getValues()) {
                JCheckBox checkbox = new JCheckBox(item.toString(), false);
                panelCB.add(checkbox);
                listeCB.add(checkbox);
            }
            panel.add(panelCB);
            panelFormulaire.add(panel);
        }

        /**
         * Parcourt les checkbox du panneau et retourne une liste des textes correspondant aux checkboxes
         *
         * @return La liste de consoles sélectionnées par l'utilisateur
         */
        public Collection<String> getChoix() {

            ArrayList<String> choix = new ArrayList<>();

            for (JCheckBox checkbox : listeCB) {
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

        /* Créer un panel pour recevoir seulement les radio buttons, placées en deux rangées.
         * Le panel du formulaire reçoit le label. */
        private final ButtonGroup buttonGroup = new ButtonGroup();
        private final JPanel panelBoutons;

        /**
         * Constructeur du panel
         *
         * @param attribut Une des valeurs de l'enum des Attributs de jeu (consoles ou cotes), lesquels contiennent
         *                 à leur tour un enum contenant les différentes consoles ou cotes possibles
         */
        public RadioPanel(Jeu.Attributs attribut) {

            panelBoutons = new JPanel();
            JLabel jLabel = new JLabel(attribut.getAttribut());
            jLabel.setForeground(Color.RED);
            panelBoutons.add(jLabel);


            GridLayout layout = new GridLayout(0, 2);
            panelBoutons.setLayout(layout);

            /* Ajouter un radio button pour chaque option possible de l'enum correspondant dans la classe Jeu.
             * Y associer une actionCommand pour pouvoir la récupérer dans la méthode getChoix(). */
            for (Enum item : attribut.getValues()) {
                JRadioButton radioButton = new JRadioButton(item.toString());
                radioButton.setActionCommand(item.toString());
                ajouterAuPanneau(radioButton);
            }
            add(panelBoutons);
        }

        private void ajouterAuPanneau(JRadioButton radioButton) {
            panelBoutons.add(radioButton);
            buttonGroup.add(radioButton);

            // Sélectionner le premier élément par défaut
            if (buttonGroup.getSelection() == null) {
                buttonGroup.setSelected(radioButton.getModel(), true);
            }
        }

        /**
         * Retourne la cote associée au bouton sélectionné.
         *
         * @return La cote sélectionnée pour le jeu à créer
         */
        public String getChoix() {
            ButtonModel model = buttonGroup.getSelection();
            return model.getActionCommand();
        }
    }

    /**
     * Classe pour gérer les boutons. Crée un JPanel contenant un bouton.
     * Nécessite un formulaire déjà créé (des TextFields) pour que le ActionListener puisse aller chercher les données
     * nécessaires pour lancer la méthode associée.
     */
    private static class BoutonFlow extends JPanel {

        // Texte à afficher sur les différents boutons
        public static final String BTN_AJOUT_JEU = "Ajouter le jeu";
        public static final String BTN_RECHERCHER = "Rechercher";

        /**
         * Constructeur
         *
         * @param action L'action associée au bouton
         */
        public BoutonFlow(Action action) {

            setLayout(new FlowLayout());
            JButton bouton = new JButton(action);
            add(bouton);
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}
