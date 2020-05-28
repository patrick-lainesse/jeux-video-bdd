import java.io.IOException;
import java.util.*;
/**
	Pour la correction du devoir, nous allons nous batîr
	un programme de test qui va contenir la méthode main
	à exécuter.  L'interface sert à garantir la syntaxe
	commune pour tous les travaux.

 */


public interface TestInterface {

    public void addJeu(Jeu unJeu);

	public Jeu getJeu(String titre, String fabricant);

	public void addBdd(String nomFile) throws IOException;

	public void loadBdd(String nomFile) throws IOException;

	public ArrayList<Jeu> chercheConsole(String console);

	public Collection<Jeu> getJeuxFabricant(String fabricant);

	public void saveBdd(String nomFichier);

}