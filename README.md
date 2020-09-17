# Ladybug
Another pacman like

# 31/07/2020
Installation de "Lombok Eclipse" : voir http://www.jouvinio.net/wiki/index.php/Lombok_Eclipse
J'ai suivi les indications du site et j'ai aussi modifié le pom.xml pour que maven puisse buidler de son côté.
N'oublier pas de faire un clean : Menu d'éclipse : Projet -> Clean pour faire un rebuild
A ce jour, vérifier que les classes du package "test.lombox" sont correctement buildées
Ces classes proviennent du site https://fxrobin.developpez.com/tutoriels/java/lombok-retour-experience/
=> on retrouve "François-Xavier Robin" dans les remerciements :)

# Septembre 2020
Ajout du framework Spring vi application-context.xml dans Ladybug

# Idées du jeu à appronfondir
* rajouter un point en fonction du nombre de points mangés : point de téléportation vers un point aléatoire
* rajouter la notion de fruits (le son existe déjà)
* rajouter dans le Jpanel à droite les remerciements (hors jeu)
* rajouter dans le JPanel à gauche une notion de vitesse instantanée des fantômes et de Ladybug
* rajouter dans le JPanel à gauche une notion de comportement des fantômes

# Idées techniques
* modifier le pattern Observer avec java 9+
* faire un installer avec java 14+ pour windows
* faire des tests unitaires avec JUnit

# Bugs détectés
* Lors d'un jeu à 2 joueurs, la présentation ne s'initiale pas correctement lorsque LadyBug ou le Fantôme gagne (je me souviens plus très bien le contexte)
* Lors d'un fin du jeu, le numéro de niveau n'est pas initialisé