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


public class Bdd implements TestInterface {

	//private LinkedHashMap<String, Collection<Jeu>> baseDeDonnees;
	private Map<String, TreeSet<Jeu>> baseDeDonnees;

    public Bdd() {
    	//Collection<Jeu> listeJeux = new TreeSet<Jeu>();
    	baseDeDonnees = new LinkedHashMap<>();
    }


    public void addJeu(Jeu unJeu) {

    	// vérifier si le fabricant est déjà dans la base de données ???
		String unFabriquant = unJeu.getFabricant();

		if (baseDeDonnees.containsKey(unFabriquant)) {
			System.out.println("Le fabriquant est dans la base de données.");
			// Si le jeu est déjà présent pour ce fabricant, il faut s’assurer d’ajouter toutes les consoles, du jeu passé en paramètre, au jeu déjà dans la liste.
			TreeSet<Jeu> ts = baseDeDonnees.get(unFabriquant);
			Collection<String> consoles = unJeu.getConsoles();

			Iterator<String> it = consoles.iterator();

			while(it.hasNext()){
				String courant = it.next();
				System.out.println("addJeu - Console courante: " + courant);	// ??? à enlever
				/// rendu ici????
			}


		} else {
			TreeSet<Jeu> ts = new TreeSet<Jeu>();
			ts.add(unJeu);
			baseDeDonnees.put(unJeu.getFabricant(), ts);
		}
    }

	public Jeu getJeu(String titre, String fabricant) {

		TreeSet<Jeu> listeJeux = baseDeDonnees.get(fabricant);
		Jeu resultat = null;

    	if (listeJeux != null) {
			Iterator<Jeu> it = listeJeux.iterator();
			//boolean trouve = false;

			while(resultat == null && it.hasNext()) {
				Jeu courant = it.next();
				if (courant.getTitre().equals(titre)) {
					//System.out.println("Test iterator getJeu" + courant);	?????
					resultat = courant;
					//trouve = true;
				}
			}
		}
		return resultat;
	}



	public void addBdd(String nomFile){
// � compl�ter
	}

	public void loadBdd(String nomFile){
// � compl�ter
	}

	public ArrayList<Jeu> chercheConsole(String console){
	// � compl�ter et changer l'instruction du return
		return null;
	}

	public Collection<Jeu> getJeuxFabricant(String fabricant){
	// � compl�ter et potentiellement changer l'instruction du return
		return null;
	}

	public void saveBdd(String nomFichier){
	//A compl�ter
	}

}