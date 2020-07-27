# jeux-video-bdd
Programme en Java avec interface graphique Swing pour gérer une base de données de jeux vidéo.

## Installation
Développement seulement pour tester différentes notions de Java, il n'existe pas d'exécutable pour l'installation.
Importer le fichier creer.sql dans une base de données créée localement.
Vérifier le login et mot de passe dans UserData, et le lien vers la base de données dans Requetes.java.
Ouvrir les fichier .java dans un IDE Java et faire un build pour le fichier GUI.java, qui contient la fonction main().
Voir les fichiers .txt pour le format que doivent avoir les fichiers en lecture/écriture.

## Ressources utilisées:
- docs.oracle.com
- https://www.journaldev.com/878/java-write-to-file
- JOptionPane: https://stackoverflow.com/questions/15853112/joptionpane-yes-no-option/15853127
- https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/ActionDemoProject/src/misc/ActionDemo.java

## À propos des différents fichiers

### Jeu.java et FichiersTXT.java
Classe représentant les objets Jeu insérés dans la base de données. Utilise des enum pour stocker des constantes en String associées aux jeux.
Il y a également des méthodes qui ajoutent les données à des vecteurs de String pour faciliter l'affichage dans des JPanel (GUI.java).

### GUI.java
Code de l'interface graphique. Utilise des actions et des enum pour simplifier la lecture du code. Les actions associent des éléments de
la barre de menu à des raccourcis clavier. Une autre section du code rassemble les actions associées aux boutons qui
s'affichent sous les formulaires pour saisir des données ou des critères de recherche. Utilise également plusieurs classes
internes pour simplifier l'utilisation des éléments de formulaires (textfields, radiobuttons, checkboxes, etc.).

### Requetes.java
Classe qui effectue les requêtes à la base de données. Développé localement avec un serveur MariaDB et testé sur le
serveur de l'Université de Montréal. La requête "DELETE FROM jeu" a été préférée à "DROP TABLE jeu" puisque le serveur
de l'Université ne permet pas les DROP TABLE à partir d'applications externes.

### creer.sql
Code SQL pour créer la table jeu, à partir de laquelle ce logiciel effectue des requêtes.

### jeux.txt et jeuxComplements.txt
Fichiers txt contenant des données à lire à l'application, qui les charge ensuite dans la base de données SQL.
