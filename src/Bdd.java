///////////////////////////////////////////////////////////////////////////////
//
// Fichier de la classe Main:	TP1.java
// Fichier:						Bdd.java
// Session:						Été 2020
//
// Auteur:						Patrick Lainesse
// Matricule:					740302
// Sources:						aucune
//////////////////////////////////////////////////////////////////////////////

import java.util.*;
import java.io.*;

// addJeu plus facile à implémtenter avec un objet bidon!!???
// si on implémente un HashSet, va falloir implémenter hashCode pour chercher???
// pour chercher dans la liste de consoles, préférable de coder qqch qui va pas "jouer" là-dedans, et qui retourne
// true ou false par exemple, pour protéger nos données??? (34 min)
// dans addBdd, mieux de faire l'union de deux Set...????
public class Bdd implements TestInterface {

	//private LinkedHashMap<String, Collection<Jeu>> baseDeDonnees;
	private Map<String, TreeSet<Jeu>> baseDeDonnees;

    public Bdd() {
    	//Collection<Jeu> listeJeux = new TreeSet<Jeu>();
    	baseDeDonnees = new LinkedHashMap<>();
    }


    public void addJeu(Jeu unJeu) {

		String unFabricant = unJeu.getFabricant();

		// vérifier si le fabricant est déjà dans la base de données
		if (baseDeDonnees.containsKey(unFabricant)) {
			// Si le jeu est déjà présent pour ce fabricant, il faut s’assurer d’ajouter toutes les consoles,
			// du jeu passé en paramètre, au jeu déjà dans la liste. Testé avec le fichier jeuxComplement	??? Intro???
			TreeSet<Jeu> ts = baseDeDonnees.get(unFabricant);

			if (ts.contains(unJeu)) {

				Collection<String> consoles = unJeu.getConsoles();

				for (String courant : consoles) {
					System.out.println("addJeu " + unFabricant + ": " + unJeu.getTitre() + " - Console courante: " + courant);    // ??? à enlever

					Objects.requireNonNull(ts.floor(unJeu)).addConsole(courant);    // requireNonNull pour éviter un nullPointerException
				}
			} else {
				ts.add(unJeu);
			}
		} else {
			TreeSet<Jeu> ts = new TreeSet<>();
			ts.add(unJeu);
			baseDeDonnees.put(unJeu.getFabricant(), ts);
		}
    }

	public Jeu getJeu(String titre, String fabricant) {

		TreeSet<Jeu> listeJeux = baseDeDonnees.get(fabricant);
		Jeu resultat = null;

    	if (listeJeux != null) {
			Iterator<Jeu> it = listeJeux.iterator();

			while(resultat == null && it.hasNext()) {
				Jeu courant = it.next();
				if (courant.getTitre().equals(titre)) {
					//System.out.println("Test iterator getJeu" + courant);	?????
					resultat = courant;
				}
			}
		}
		return resultat;
	}

	public void addBdd(String nomFile) {

		FileReader fr = null;
		boolean existeFichier = true;
		boolean finFichier = false;

		try {
			fr = new FileReader(nomFile);

			// déplacer catch pour laisser remplir quand même????
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

	public void loadBdd(String nomFile) {

    	baseDeDonnees = new LinkedHashMap<>();
    	addBdd(nomFile);
	}

	public ArrayList<Jeu> chercheConsole(String console) {

    	ArrayList<Jeu> compatibles = new ArrayList<>();

		//private Map<String, TreeSet<Jeu>> baseDeDonnees;
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

	public Collection<Jeu> getJeuxFabricant(String fabricant) {

		//Collection<Jeu> reponse = baseDeDonnees.get(fabricant);
		return baseDeDonnees.get(fabricant);
	}

	public void saveBdd(String nomFichier) {

    	File fichier = new File(nomFichier);
    	FileWriter fr = null;

    	try {
    		fr = new FileWriter(fichier);
    		fr.write(this.toString());

		} catch (IOException e) {
			e.printStackTrace();
			// ajout message d'erreur???
		} finally {
    		try {
    			fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				// ajout message d'erreur???
			}
		}
	}

	// redéfinition de la méthode toString, utile pour tester et utilisée dans saveBdd
	@Override
	public String toString() {

    	StringBuilder epeler = new StringBuilder();

		Set<String> cles = baseDeDonnees.keySet();
		for (String cle: cles) {
			TreeSet<Jeu> listeFabricant = baseDeDonnees.get(cle);
			Iterator<Jeu> it = listeFabricant.iterator();

			while (it.hasNext()) {
				Jeu jeuCourant = it.next();
				epeler.append(jeuCourant.toString() + "\n");
			}
		}

		return epeler.toString();
	}
}