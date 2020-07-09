///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	TP1.java
// Fichier:						Bdd.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						docs.oracle.com
//								https://www.journaldev.com/878/java-write-to-file
//////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.io.*;

// TODO: vérifier code du prof et améliorer le mien si possible
// TODO: Améliorer les recherches avec toUpper pour ignorer la casse
public class Bdd {

	private Map<String, TreeSet<Jeu>> baseDeDonnees;

    public Bdd() {
    	baseDeDonnees = new LinkedHashMap<>();
    }

    /* Vérifie si le fabricant et/ou le jeu existe déjà dans la base de données.
     * Si le jeu s'y retrouve déjà, ajout des consoles à l'entrée existante
     * Sinon, le jeu est ajouté à la base de données, ainsi que le fabricant si nécessaire
     *
     * @param unJeu		Le jeu à ajouter à la base de données */
    public void addJeu(Jeu unJeu) {

		String unFabricant = unJeu.getFabricant();

		if (baseDeDonnees.containsKey(unFabricant)) {

			TreeSet<Jeu> ts = baseDeDonnees.get(unFabricant);

			if (ts.contains(unJeu)) {
				/* Utilise floor pour retrouver le jeu courant dans le TreeSet contenant les jeux associés à ce fabricant
				 * Utilise requireNonNull pour éviter un nullPointerException */
				Objects.requireNonNull(ts.floor(unJeu)).getConsoles().addAll(unJeu.getConsoles());

			} else {
				ts.add(unJeu);
			}
		} else {
			TreeSet<Jeu> ts = new TreeSet<>();
			ts.add(unJeu);
			baseDeDonnees.put(unFabricant, ts);
		}
    }

    /* Recherche un jeu dans la base de données
     * @param titre			Le nom de ce jeu
     * @param fabricant		Le nom du fabricant pour ce jeu
     * @return		L'objet Jeu correspondant à la recherche, null si non trouvé */
	public Jeu getJeu(String titre, String fabricant) {

		TreeSet<Jeu> listeJeux = baseDeDonnees.get(fabricant);
		Jeu resultat = null;

		/* Si la liste de jeux pour le fabricant passé en paramètre n'est pas nulle, parcourir cette liste
		 * pour voir si ce jeu s'y retrouve */
    	if (listeJeux != null) {
			Iterator<Jeu> it = listeJeux.iterator();

			while(resultat == null && it.hasNext()) {
				Jeu courant = it.next();
				if (courant.getTitre().equals(titre)) {
					resultat = courant;		// Arrêter la recherche dès qu'on obtient un résultat
				}
			}
		}
		return resultat;
	}

	/* Lit une liste de jeux contenue dans un fichier .txt et les ajoute à la base de données
	 * ou ajoute les consoles associées à ce jeu s'il s'y trouve déjà.
	 * Utilise un BufferedReader pour améliorer la performance à la lecture.
	 *
	 * @param nomFile		Le nom du fichier .txt à lire */
	public void addBdd(String nomFile) {

		FileReader fr = null;
		boolean existeFichier = true;
		boolean finFichier = false;

		try {
			fr = new FileReader(nomFile);
		} catch (java.io.FileNotFoundException e) {
			System.out.println("Probleme d'ouverture du fichier " + nomFile);
			existeFichier = false;
		}

		if (existeFichier) {
			BufferedReader entree = new BufferedReader(fr);

			while (!finFichier) {
				String ligne = null;		// null si fin de fichier
				try {
					ligne = entree.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (ligne != null) {

					/*Format d'une entrée:	split(;)
					 						[0]	[1]		[2]		[3] -> consoles
					 						EA;NHL 2020;E;PS4,WIIU,XONE,PC */
					String[] parametres = ligne.split(";");
					String[] consoles = parametres[3].split(",");

					Jeu nouveau = new Jeu(parametres[0], parametres[1], parametres[2]);

					for (String console : consoles) {
						nouveau.addConsole(console);
					}
					this.addJeu(nouveau);

				} else finFichier = true;
			}
			try {
				entree.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Vérifie si le nom de fichier passé en paramètre est valide, et si oui, vide la base de données puis fait appel
	 * à addBdd() pour lire un nouveau fichier. */
	public void loadBdd(String nomFile) {

		boolean existeFichier = true;

		try {
			new FileReader(nomFile);
		} catch (java.io.FileNotFoundException e) {
			System.out.println("Probleme d'ouverture du fichier " + nomFile);
			existeFichier = false;
		}

		if (existeFichier) {
			baseDeDonnees = new LinkedHashMap<>();
			addBdd(nomFile);
		}
	}

	/* Parcourt la base de données et dresse une liste de jeux compatibles avec une certaine console
	 *
	 * @param console		La console pour laquelle on cherche à dresser une liste
	 * @return				ArrayList de jeux compatibles avec la console en paramètre, vide si elle n'est pas dans la base de données. */
	public ArrayList<Jeu> chercheConsole(String console) {

    	ArrayList<Jeu> compatibles = new ArrayList<>();

		Set<String> cles = baseDeDonnees.keySet();
    	for (String cle: cles) {
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);

			for (Jeu jeuCourant : listeFabricant) {
				if (jeuCourant.getConsoles().contains(console)) {
					compatibles.add(jeuCourant);
				}
			}
		}

		return compatibles;
	}

	/* Retourne la liste de jeux associés à un fabricant.
	 *
	 * @param fabricant		Fabricant pour lequel on veut imprimer la liste
	 * @return		Collection contenant la liste des jeux associés à ce fabricant, null si ne s'y trouve pas */
	public Collection<Jeu> getJeuxFabricant(String fabricant) { return baseDeDonnees.get(fabricant); }

	/* Écrit les informations de la base de données dans un fichier .txt sous le format:
	 * FOCUS;Vampyr;PG;PS4,XONE,PC,MAC,SWITCH
	 * @param nomFichier		Le nom du fichier .txt à créer */
	public void saveBdd(String nomFichier) {

    	File fichier = new File(nomFichier);
    	FileWriter fr = null;

    	try {
    		fr = new FileWriter(fichier);
    		fr.write(this.toString());

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erreur lors de l'écriture du fichier.");
		} finally {
    		try {
				assert fr != null;
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Erreur lors de la tentative du fermeture du fichier après l'écriture.");
			}
		}
	}

	/* Affiche à l'écran toutes les informations relatives aux jeux ayant une cote passée en paramètre
	 * ##### Modifiée pour le TP2, afin de faire afficher le résultat dans l'interface graphique #####
	 *
	 * @param cote			La cote pour laquelle on cherche à afficher une liste */
	public ArrayList<Jeu> chercheCote(String cote) {

		ArrayList<Jeu> liste = new ArrayList<>();
		Set<String> cles = baseDeDonnees.keySet();
		for (String cle: cles) {
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);

			for (Jeu jeuCourant : listeFabricant) {
				if (jeuCourant.getCote().equals(cote)) {
					liste.add(jeuCourant);
				}
			}
		}
		return liste;
	}

	/* Redéfinition de la méthode toString pour écire les informations de la base de données sous le format:
	 * FOCUS;Vampyr;PG;PS4,XONE,PC,MAC,SWITCH
	 * Utilise un StringBuilder pour améliorer la performance à l'écriture */
	@Override
	public String toString() {

    	StringBuilder epeler = new StringBuilder();

		Set<String> cles = baseDeDonnees.keySet();
		for (String cle: cles) {
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);

			for (Jeu jeuCourant : listeFabricant) {
				epeler.append(jeuCourant.toString()).append("\n");
			}
		}
		return epeler.toString();
	}


	/*****************************************************************************************************
	 * Fonction utiles pour transposer à l'interface graphique (GUI)
	 *****************************************************************************************************/

	/* Parcourt la base de données et ajoute un vecteur de String pour chacun des jeux, dans le but
	 * de l'afficher dans un JTable.
	 *
	 * @return	Un vecteur de jeux, dont chacun de ses attributs sont regroupés dans un vecteur de String */
	public Vector<Vector<String>> vectoriser() {

		Vector<Vector<String>> vecteurJeu = new Vector<>();

		Set<String> cles = baseDeDonnees.keySet();
		for (String cle: cles) {
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);

			for (Jeu jeuCourant : listeFabricant) {
				vecteurJeu.add(jeuCourant.vectoriser());
			}
		}

		return vecteurJeu;
	}
}