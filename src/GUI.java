import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class GUI extends JFrame implements ActionListener {

    /**
     * Panneaux principaux où s'affichent les différents éléments graphiques de l'appli.
     * Déclarés ici pour permettre aux différentes méthodes d'interagir avec eux lorsque nécessaire.
     */
    private final JMenuBar menu;
    private final Container container;

    // TODO: ne pas faire new ici?
    JPanel panelNorth = new JPanel();   // TODO: Réflichir à si c'est plus pertinent que ce soit global ou non
    JPanel panelFormulaire = new JPanel();              // Affiche un formulaire pour saisir les informations 'un jeu
    JScrollPane tableauScrollPane = new JScrollPane();  // Affiche un tableau d'informations sur les jeux
    private Bdd baseDeDonnees;

    /**
     * Texte des options des menus
     * TODO: Enum à la place? + séparer menus des items des menus
     */
    private static final String BDD = "Base de donn\u00e9es";
    private static final String RECHERCHE = "Recherche";

    private static final String CHARGER = "Charger nouvelle base de donn\u00e9es";
    private static final String RAFRAICHIR = "Actualiser l'affichage de la base de donn\u00e9es";
    private static final String AJOUT_FICHIER = "Ajouter fichier de base de donn\u00e9es";
    private static final String RECHERCHE_JEU = "Rechercher un jeu";
    private static final String AJOUT_JEU = "+ Ajouter un nouveau jeu";

    public static final String BTN_AJOUT_JEU = "Ajouter le jeu";

    /**
     * Messages pouvant s'afficher dans le programme
     */
    private static final String ANNULE = "Annul\u00e9 par l'utilisateur.";
    // TODO: Boîte de dialogue pour avertir que possible de perdre des données non sauvegardées

    // TODO: En-tête
    public GUI(String titre) {

        // Ouvrir le programme pour qu'il occupe les trois quarts de l'écran
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 3 / 4, screenSize.height * 3 / 4);


        setTitle(titre);
        container = getContentPane();
        container.setLayout(new BorderLayout());    // TODO: C'est pas par défaut?

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
                if (fichier != ANNULE) {
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
                if (fichierAjout != ANNULE) {
                    baseDeDonnees.addBdd(fichierAjout);
                } else System.out.println(ANNULE);
                afficherBdd();
                break;
            case RECHERCHE_JEU:
                rechercheJeu();
                break;
            case AJOUT_JEU:
                afficherForm();
                break;
            case BTN_AJOUT_JEU:
                break;
        }
    }

    /* Crée un tableau qui prend toute la largeur du container principal pour y afficher
     * la liste de jeux de la base de données et leurs attributs. */
    public void afficherBdd() {

        viderContainer();

        // TODO: Utiliser un enum dans la classe jeu
        Vector<String> nomColonnes = new Vector<>();
        nomColonnes.add("Titre");
        nomColonnes.add("Fabricant");
        nomColonnes.add("Cote");
        nomColonnes.add("Consoles");

        Vector<Vector<String>> lignes = baseDeDonnees.vectoriser();

        JTable table = new JTable(lignes, nomColonnes);
        tableauScrollPane = new JScrollPane(table);

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

    // TODO
    public void rechercheJeu() {

        //public Jeu getJeu(String titre, String fabricant) {
        JTextField textField = new JTextField(20);
    }

    /* Crée un formulaire pour saisir les données d'un nouveau jeu à ajouter à la base de données.
     * Le formulaire est placé dans un panel, lui-même est placé dans un autre panel pour bien le placer
     * au coin supérieur gauche du container principal. */
    public void afficherForm() {

        viderContainer();
        panelFormulaire = new JPanel();      // TODO: voir ce qui est le plus pertinent entre global ou non. Ici, c'est un sous-panel déjà...

        // TODO: Utiliser un enum dans la classe jeu
        String[] attributsJeu = {"Fabricant", "Titre", "Cote", "Consoles"};
        panelNorth = new JPanel();
        panelNorth.setLayout(new BorderLayout());

        panelFormulaire.setLayout(new BoxLayout(panelFormulaire, BoxLayout.PAGE_AXIS));

        // TextField pour saisir le fabricant et le titre du jeu
        CustomTxtField tfFabricant = new CustomTxtField("Fabricant");
        CustomTxtField tfTitre = new CustomTxtField("Titre");
        panelFormulaire.add(tfFabricant);
        panelFormulaire.add(tfTitre);

        // Boutons radio pour le choix de la cote
        CotesPanel cotesPanel = new CotesPanel();
        panelFormulaire.add(cotesPanel);

        // TODO: txtField et bouton pour ajouter des consoles?
        // Ou afficher msg pour ne pas oublier d'ajouter les consoles
        //panelFormulaire.add(new CustomTxtField("Console"));


        //panelFormulaire.setBorder(BorderFactory.createEmptyBorder(35, 15, 5, 5));
        panelFormulaire.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelNorth.add(panelFormulaire, BorderLayout.NORTH);

        JPanel flowPanel = new JPanel(new FlowLayout());
        JButton boutonCreer = new JButton(BTN_AJOUT_JEU);
        boutonCreer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //public Jeu(String fabricant, String titre, String cote)
                Jeu nouveauJeu = new Jeu(tfFabricant.getText(), tfTitre.getText(), cotesPanel.getChoix());
                baseDeDonnees.addJeu(nouveauJeu);
            }
        });
        flowPanel.add(boutonCreer);

        panelNorth.add(flowPanel, BorderLayout.EAST);
        panelNorth.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(panelNorth, BorderLayout.WEST);

        setVisible(true);
    }

    /* Vide le contenant principal de toutes ses composantes */
    public void viderContainer() {

        container.removeAll();
        container.validate();   // TODO: vérifier ce que ça signifie
        container.repaint();
    }

    public static void main(String[] args) {
        new GUI("Boutique de jeux vid\u00e9o");
    }
}

/*****************************************************************************************************
 * Classes pour créer certains éléments graphiques réutilisables
 *****************************************************************************************************/

/**
 * Crée un JPanel contenant un textfield et un label associé.
 */
class CustomTxtField extends JPanel {

    public static final Dimension TXT_FIELD_SIZE = new Dimension(700, 20);
    JTextField jTextField;

    /* Constructeur
     *
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
 * Crée un JPanel pour le groupe de boutons radio afin de sélectionner la cote du jeu lors de sa création.
 */
class CotesPanel extends JPanel {

    // TODO: enum dans la classe Jeu pour stocker les cotes possibles
    private ButtonGroup buttonGroup = new ButtonGroup();
    String[] cotes = {"E", "PG", "T", "M"};

    public CotesPanel() {

        JPanel panel = new JPanel();
        JLabel jLabel = new JLabel("Cote");

        panel.add(jLabel);

        for (String cote : cotes) {
            JRadioButton radioButton = new JRadioButton(cote);
            radioButton.setActionCommand(radioButton.getText());
            panel.add(radioButton);
            buttonGroup.add(radioButton);

            // Sélectionner la cote "E" par défaut
            if (buttonGroup.getSelection() == null) {
                buttonGroup.setSelected(radioButton.getModel(), true);
            }
        }
        add(panel);
    }

    /* Retourne la cote associée au bouton sélectionné.
     * @return	La cote sélectionnée pour le jeu à créer */
    public String getChoix() {
        ButtonModel model = buttonGroup.getSelection();
        return model.getActionCommand();
    }
}
