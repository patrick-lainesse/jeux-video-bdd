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

// si on implémente un HashSet, va falloir implémenter hashCode pour chercher???
// pour chercher dans la liste de consoles, préférable de coder qqch qui va pas "jouer" là-dedans, et qui retourne
// true ou false par exemple, pour protéger nos données??? (34 min)
// dans addBdd, mieux de faire l'union de deux Set...????
public class Bdd implements TestInterface {

	//private LinkedHashMap<String, Collection<Jeu>> baseDeDonnees;	???
	private Map<String, TreeSet<Jeu>> baseDeDonnees;

    public Bdd() {
    	//Collection<Jeu> listeJeux = new TreeSet<Jeu>();
		// vérifier ce qui se passe quand on ajoute un bdd sans TreeSet associé et qu'on tente d'y avoir accès???
    	baseDeDonnees = new LinkedHashMap<>();
    }


    /* Vérifie si un jeu existe déjà dans la base de données.
     * Si le jeu s'y retrouve déjà, ajout des consoles à l'entrée existante
     * Sinon, le jeu est ajouté à la base de données, ainsi que le fabricant si nécessaire
     * @param unJeu		Le jeu à ajouter à la base de données */
    public void addJeu(Jeu unJeu) {

		String unFabricant = unJeu.getFabricant();

		/* vérifie si le fabricant est déjà dans la base de données,
		 * sinon, crée une nouvelle entrée pour ce fabricant dans la base de données */
		if (baseDeDonnees.containsKey(unFabricant)) {

			TreeSet<Jeu> ts = baseDeDonnees.get(unFabricant);

			/* vérifie si le jeu est présent dans la liste des jeux de ce fabricant, et si oui,
			 * y ajoute la liste de consoles du jeu passé en paramètres à addJeu().
			 * Sinon, ajoute simplement le jeu passé en paramètre */
			if (ts.contains(unJeu)) {

				Collection<String> consoles = unJeu.getConsoles();

				// plus simple de faire un add all au jeu en question JavaDocs, plus vérifier si conserve ordre de saisie???
				for (String courant : consoles) {
					/* Utilise floor pour retrouver le jeu courant dans le TreeSet contenant les jeux associés à ce fabricant
					 * Utilise requireNonNull pour éviter un nullPointerException */
					Objects.requireNonNull(ts.floor(unJeu)).addConsole(courant);
				}
			} else {
				ts.add(unJeu);
			}
		} else {
			TreeSet<Jeu> ts = new TreeSet<>();
			ts.add(unJeu);
			baseDeDonnees.put(unFabricant, ts);
			//baseDeDonnees.put(unJeu.getFabricant(), ts);	????
		}
    }

    /* Recherche un jeu dans la base de données
     * @param titre			Le nom de ce jeu
     * @param fabricant		Le nom du fabricant pour ce jeu
     * @return		L'objet Jeu correspondant à la recherche, null si non trouvé */
	public Jeu getJeu(String titre, String fabricant) {

		// binarySearch????
		TreeSet<Jeu> listeJeux = baseDeDonnees.get(fabricant);
		Jeu resultat = null;

		/* Si la liste de jeux pour le fabricant passé en paramètre n'est pas nulle, parcourir cette liste
		 * pour voir si ce jeu s'y retrouve */
    	if (listeJeux != null) {
			Iterator<Jeu> it = listeJeux.iterator();

			// arrêter la recherche dès qu'on obtient un résultat
			while(resultat == null && it.hasNext()) {
				Jeu courant = it.next();
				if (courant.getTitre().equals(titre)) {
					resultat = courant;
				}
			}
		}
		return resultat;
	}

	/* Lit une liste de jeux contenue dans un fichier .txt et les ajoute à la base de données
	 * ou ajoute les consoles associées à ce jeu s'il s'y trouve déjà.
	 * @param nomFile		Le nom du fichier .txt à lire */
	// tester avec nom de fichier dans un path???
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

	/* Vide la base de données puis fait appel à addBdd pour lire un nouveau fichier. */
	// Attention: efface si mal formé nom de fichier????
	public void loadBdd(String nomFile) {

    	baseDeDonnees = new LinkedHashMap<>();
    	addBdd(nomFile);
	}

	/* Parcourt la base de données et dresse une liste de jeux compatibles avec une certaine console
	 * @param console		La console pour laquelle on cherche à dresser une liste
	 * @return		ArrayList de jeux compatibles avec la console voulue, vide si la console n'est pas dans la base de données. */
	public ArrayList<Jeu> chercheConsole(String console) {

    	ArrayList<Jeu> compatibles = new ArrayList<>();

		//private Map<String, TreeSet<Jeu>> baseDeDonnees;	???
		Set<String> cles = baseDeDonnees.keySet();
    	for (String cle: cles) {
			//System.out.println(cle);		???
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);
			Iterator<Jeu> it = listeFabricant.iterator();

			while (it.hasNext()) {
				Jeu jeuCourant = it.next();
				if (jeuCourant.getConsoles().contains(console)) {
					compatibles.add(jeuCourant);
				}
			}
		}

		// essayer vide et trier??? imprimer différemment???
		return compatibles;
	}

	/* Retourne la liste de jeux associés à un fabricant.
	 * @param fabricant		Fabricant pour lequel on veut imprimer la liste
	 * @return		Collection contenant la liste des jeux associés à ce fabricant, null si ne s'y trouve pas */
	// tester null???
	public Collection<Jeu> getJeuxFabricant(String fabricant) {

		//Collection<Jeu> reponse = baseDeDonnees.get(fabricant);	???
		return baseDeDonnees.get(fabricant);
	}

	/* Écrit les informations de la base de données dans un fichier .txt sous le format:
	 * FOCUS;Vampyr;PG;PS4,XONE,PC,MAC,SWITCH
	 * @param nomFichier		Le nom du fichier .txt à créer */
	// tester avec un nom de fichier qui fonctionne pas et gérer???
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
    			fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Erreur lors de la tentative du fermeture du fichier après l'écriture.");
			}
		}
	}
	// chercheCote: affiche à l'écran toutes les informations relatives aux jeux ayant une cote passée en paramètre.???

	/* Redéfinition de la méthode toString pour écir les informations de la base de données sous le format:
	 * FOCUS;Vampyr;PG;PS4,XONE,PC,MAC,SWITCH
	 * Utilise un StringBuilder pour améliorer la performance à l'écriture */
	@Override
	public String toString() {

    	StringBuilder epeler = new StringBuilder();

		Set<String> cles = baseDeDonnees.keySet();
		for (String cle: cles) {
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);
			Iterator<Jeu> it = listeFabricant.iterator();

			while (it.hasNext()) {
				Jeu jeuCourant = it.next();
				epeler.append(jeuCourant.toString()).append("\n");
			}
		}
		return epeler.toString();
	}
}