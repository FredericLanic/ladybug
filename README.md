# Ladybug, encore un autre Pacman.
J'ai réalisé le programme "Ladybug" dans une optique de créer une application Java pour ré-apprendre et connaitre mieux Java en m'amusant.

J'ai essayé d'appliquer quelques trucs sympas : le motif d'architecture MVC, le framework Spring, d'utiliser des designs patterns, la hiérarchie des classes, le polymorphisme, les threads....

# Les branches sous GitHub
Dans GitHub, Ladybug possède deux branches : une branche **master** et une branche **develop**

La branche **master** est en théorie la branche qui contient des releases taguées. Je ne suis pas aussi rigoureux que dans un contexte professionnel :).

La branche **develop** est la branche en développement; en bref, c'est celle là qu'il faut scruter.

# Builder et exécuter Ladybug
Le build de ladybug est réalisé par "maven" par la commande "mvn clean install".

En résumé, le build génère un fat jar : un jar qui contient toutes les dépendances décrites dans le fichier "pom.xml"

A ce jour (19 Avril 2021), j'utilise la version **maven 3.6.3** et la version **jdk 14.0.1**.

La commande pour lancer le jeu est :

java -cp "ladybug-0.1.1-SNAPSHOT-jar-with-dependencies.jar;." com.kycox.game.MainLadybug

Pour le jeu en lui même, je vous laisse trouver la gestion des properties et l'application-contexte pour gérer la configuration du jeu et l'affichage des plugins, la couleur de Ladybug.

# Développer Ladybug
Je l'ai développé sous Eclipse version 2020-09. Je ne pense pas que la version soit très importante en fait.

Pour limiter les longueurs de code dues aux Getter et Setter, jai fait le choix d'utiliser Lombok. Je n'ai pas eu de retours super positifs au sujet de lombok dans le cadre professionnel; mais pour ma petite application, ça fait le job et ça ne me pose pas de soucis majeurs, plutôt le contraire; mon code est éclairci.

Installation de "Lombok Eclipse" : voir http://www.jouvinio.net/wiki/index.php/Lombok_Eclipse

J'ai suivi les indications du site et j'ai aussi modifié le "pom.xml" pour que maven puisse buidler de son côté.
N'oublier pas de faire un clean : Menu d'éclipse : Projet -> Clean pour faire un rebuild

A ce jour, vérifier que les classes du package "test.lombox" sont correctement buildées

Ces classes proviennent du site https://fxrobin.developpez.com/tutoriels/java/lombok-retour-experience/
=> on retrouve "François-Xavier Robin" dans les remerciements :)

Voir aussi https://www.baeldung.com/intro-to-project-lombok

# Comprendre Ladybug

Pour rentrer dans le code de Ladybug, je vais vous donner mon point de vue

Le modèle MVC que j'ai essayé d'appliquer est en faire les classes :

1. Le modèle : com.kycox.game.model.GameModel 

  c'est le coeur du jeu, son battement est défini par un timer
  
4. Le contrôleur : java.awt.event.KeyAdapter.KeyGameController 

  c'est les commandes du joueur.
  
4. Les views : 
  * com.kycox.game.sound.GameSounds (le sons du jeu)
  * com.kycox.game.view.GameView (l'affichage du niveau du jeu)
  * com.kycox.game.view.down.PageEndView (l'affichage du statut du jeu)

La classe qui les relie ensemble par le design pattern Observer : com.kycox.game.engine.Engine

> Je trouve que la mise en place du framework Spring ne met pas en avant l'architecture MVC en tant que telle. Elle l'a plutôt diluée. Il y a, me semble-t-il du travail à faire dans ce sens.

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
* faire un installer avec java 16+ pour windows
* faire des tests unitaires avec JUnit

# Bugs détectés
* Lors d'un jeu à 2 joueurs, la présentation ne s'initiale pas correctement lorsque LadyBug ou le Fantôme gagne (je me souviens plus très bien le contexte)
