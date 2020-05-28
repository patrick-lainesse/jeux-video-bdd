import java.util.*;

public class TP1 {

	// Pour faire afficher un jeu.  À modifier pour le tp2
	public static void afficherJeu(TestInterface b, String fab, String titre ){
		Jeu aAfficher = b.getJeu(titre, fab);

		if( aAfficher != null)
			System.out.println(aAfficher);
		else
			System.out.println(titre + " n'est pas dans la banque de données");
	}




    public static void main(String[] args) {

    	TestInterface laBase = new Bdd();
    	Jeu unJeu;

    	unJeu = new Jeu("EA", "The Sims 5", "M");
    	laBase.addJeu(unJeu);

    	unJeu.addConsole("PS4");
    	unJeu.addConsole("XONE");

    	System.out.println("Les infos sur les Sims 5 : ");
		//System.out.println(laBase.getJeu("The Sims 5", "EA"));
    	afficherJeu(laBase, "EA", "The Sims 5");

    	/*laBase.loadBdd("jeux.txt");

    	System.out.println("\n\nAprès le load, les infos sur les Sims 5 : ");
    	afficherJeu(laBase, "EA", "The Sims 5");

    	System.out.println("Les infos sur NHL 20 : ");
    	afficherJeu(laBase, "EA", "NHL 2020");

    	System.out.println("Les infos sur Vampyr : ");
    	afficherJeu(laBase, "FOCUS", "Vampyr");


		laBase.addBdd("jeuxComplement.txt");

		System.out.println("\n\nAprès le addBdd, les infos sur les Sims 5 : ");
    	afficherJeu(laBase, "EA", "The Sims 5");

    	System.out.println("Les infos sur NHL 20 : ");
    	afficherJeu(laBase, "EA", "NHL 2020");

    	System.out.println("Les infos sur Vampyr : ");
    	afficherJeu(laBase, "FOCUS", "Vampyr");

		System.out.println("\n\nLes jeux disponibles sur la SWITCH sont :");
		List<Jeu> lstSwitch = laBase.chercheConsole("SWITCH");

		for(Jeu j : lstSwitch)
			System.out.println(j);

		Collection<Jeu> colFab = laBase.getJeuxFabricant("UBISOFT");
		System.out.println("\n\nLes jeux de UBISOFT");
		if(colFab==null)
			System.out.println("Aucun jeu par UBISFOT");
		else{
			for(Jeu j : colFab)
			System.out.println(j);
		}

		laBase.saveBdd("sauvegarde.txt");*/
    }
}
