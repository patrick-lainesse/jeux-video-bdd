# jeux-video-bdd
Programme en Java avec interface graphique Swing pour gérer une base de données de jeux vidéo.

## Installation
Développement seulement pour tester différentes notions de Java, il n'existe pas d'exécutable pour l'installation.
Ouvrir les fichier .java dans un IDE Java et faire un build pour le fichier GUI.java. Voir les fichiers .txt pour
le format que doivent avoir les fichiers en lecture/écriture. Éventuellement, la partie .txt sera remplacé par MySQL.

## Ressources utilisées:
- docs.oracle.com
- https://www.journaldev.com/878/java-write-to-file
- JOptionPane: https://stackoverflow.com/questions/15853112/joptionpane-yes-no-option/15853127
- https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/ActionDemoProject/src/misc/ActionDemo.java

## À propos des différents fichiers

### Jeu.java et Bdd.java
Classes pour gérer chaque jeu de la base de données. Développés pour utiliser des Java Collections, seront éventuellement
remplacé par MySQL. Utilise des Enum pour stocker des constantes en String associées aux jeux. Il y a également des
méthodes qui ajoutent les données à des vecteurs de String pour faciliter l'affichage dans des JPanel (GUI.java).

### GUI.java
Code de l'interface graphique. Utilise des actions pour simplifier la lecture du code, qui associent des éléments de
la barre de menu à des raccourcis clavier. Une autre section du code rassemble les actions associées aux boutons qui
s'affichent sous les formulaires pour saisir des données ou des critères de recherche. Utilise également plusieurs classes
internes pour simplifier l'utilisation des éléments de formulaires (textfields, radiobuttons, checkboxes, etc.)
