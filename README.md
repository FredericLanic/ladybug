

# Ladybug, "encore" un autre Pacman.
J'ai réalisé le programme "Ladybug" dans une optique de créer une application Java pour ré-apprendre et connaitre mieux Java en m'amusant.

J'ai essayé d'appliquer quelques trucs sympas : le motif d'architecture MVC, le framework Spring, d'utiliser des designs patterns, la hiérarchie des classes, le polymorphisme, les threads....

# Les branches sous GitHub
Dans GitHub, Ladybug possède deux branches : une branche **master** et une branche **develop**

La branche **master** est en théorie la branche qui contient des releases taguées. Je ne suis pas aussi rigoureux que dans un contexte professionnel :).

La branche **develop** est la branche en développement; en bref, c'est celle là qu'il faut scruter.

# Builder et exécuter Ladybug
Le build de ladybug est réalisé par "maven" par la commande "mvn clean install".

En résumé, le build génère un fat jar : un jar qui contient toutes les dépendances décrites dans le fichier "pom.xml"

A ce jour (19 Avril 2021), j'utilise la version **maven 3.6.3** et la version **jdk 16**.

La commande pour lancer le jeu est :

java -cp "ladybug-0.1.1-SNAPSHOT-jar-with-dependencies.jar;." com.kycox.game.MainLadybug

Pour le jeu en lui même, je vous laisse trouver la gestion des properties et l'application-contexte pour gérer la configuration du jeu et l'affichage des plugins, la couleur de Ladybug.

# Développer Ladybug
Je l'ai développé sous Eclipse version 2020-09. Je ne pense pas que la version soit très importante en fait.

Pour limiter les longueurs de code dues aux Getter et Setter, j'ai fait le choix d'utiliser Lombok. Je n'ai pas eu de retours super positifs au sujet de lombok dans le cadre professionnel; mais pour ma petite application, ça fait le job et ça ne me pose pas de soucis majeurs, plutôt le contraire; mon code est éclairci.

Installation de "Lombok Eclipse" : voir http://www.jouvinio.net/wiki/index.php/Lombok_Eclipse

J'ai suivi les indications du site et j'ai aussi modifié le "pom.xml" pour que maven puisse buidler de son côté.
N'oublier pas de faire un clean : Menu d'éclipse : Projet -> Clean pour faire un rebuild

A ce jour, vérifier que les classes du package "test.lombox" sont correctement buildées.

Ces classes proviennent du site https://fxrobin.developpez.com/tutoriels/java/lombok-retour-experience/
=> on retrouve "François-Xavier Robin" dans les remerciements :)

Voir aussi https://www.baeldung.com/intro-to-project-lombok

# Quels mots architecture de Ladybug

Pour rentrer dans le code de Ladybug, je vais vous donner mon point de vue

Le modèle MVC que j'ai essayé d'appliquer est en fait les classes :

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

# C'est quoi le but du jeu ?

Vous être Ladybug, une cousine bien lointaine de Pacman, et vous n'avez qu'un seul but dans la vie : manger des gommes. Vous avez faim de gommes. Pour cela, vous avez mis votre OX-Tshirt bleu préféré, qui vous donne la force et l'envie d'avancer dans les niveaux.


Evidemment, votre vie serait bien trop simple, et peut être ennuyante, si vous étiez seule dans votre monde à déguster paisiblement les gommes : des fantômes rodent sur votre parcours, et malheureusement, leur contact peut être mortel. 

![Introduction](/readme/introduction.png)

Heureusement, si un fantôme assez futé vous touche, vous avez la chance et la capacité de vous regénérer au point de départ du niveau. Dès le lancement de votre partie, le destin vous donne la possibilité de commettre 3 erreurs. A fur et à mesure de votre expérience et de votre appétit, vous allez pouvoir augmenter le nombre d'erreurs possibles : votre XP augmente, votre nombre de vos vies aussi.


## Les fantômes
Les fantômes sont rancuniers; ils se souviennent de leurs trop nombreuses expériences avec Pacman, et ils veulent se venger. Leur unique but et d'empêcher sa cousine lointaine, Ladybug, de manger les gommes.

La nature étant imparfaite, les fantôme ont plus ou moins un tempérament intelligent, agressif voire négligeant, avec aussi des facultés de déplacement plus ou moins rapides et progressives à chaque niveau.

| Hey ! Photo  | Description |
| ------------- | ------------- |
| ![I'm Blinky](/readme/ghosts/blinky.png) | Blinky, le fantôme rouge est celui que vous devriez vous méfier le plus. Plus votre niveau est élevé, plus il est agressif et vous pourchasse. De plus, plus vous mangez de gommes, plus il s'énerve et il se déplace de plus en plus vite.   |
| ![I'm Inky](/readme/ghosts/inky.png) | Inky, le fantôme bleu, lui est bien différent. Le destin lui a apporté une comportement négligeant; il mène son parcours comme bon lui semble, sans prendre en compte le monde extérieur. Tout en étant tranquille, son chemin peut être surprenant.  |
| ![I'm Pinky](/readme/ghosts/pinky.png) | Pinky, avec sa tenue rose, est du genre futée. Plus votre niveau est important, plus elle aura tendance à aller devant vous, et peut être vous prendre en tenaille avec Blinky.  |
| ![I'm Clyde](/readme/ghosts/clyde.png) | Clyde, le fantôme orange, est plutôt du genre pépère, comme Inky. Peut être un jour il se réveillera.... |

## Allez, on joue !
Lorsqu'un niveau débute, vous, Ladybug êtes assez loin de fantômes. Ils débutent tous regroupés sur un point précis : leur point de re-génération [image du point de re-génération]. Vous pouvez alors commencer tranquillement à vous régaler des gommes qui sont tout autour de vous.


Mais attention, les fantômes arrivent vite !!


Pour vous défendre, vous avez une arme forte : la méga-gomme [mettre l'image de la méga-gomme]. Il y en a quelques-unes par niveau. Les fantômes en ont une peur maladive. Dès que vous l'avalez, les fantômes deviennent ultra stressés, tellement stressés qu'il en change de couleur [mettre un fantôme gris], qu'ils essaient de s'éloigner de vous, tout en se déplaçant moins rapidement. Puis, petit à petit, il reprennent confiance, leurs états "intial" et "stressé" alternent et c'est pour ça qu'ils se mettent à clignoter. Leur vitesse revient petit à petit comme avant.


Quand les fantômes sont dans un état de stress, vous avez le pouvoir de manger leur enveloppe corporelle, même s'ils clignotent. Ils deviennent alors totalement inoffensif et se dirige instinctivement vers leur point de re-génération [Mettre l'image de point de regénération] dans le but de récupérer leur état et leur instinct initiaux envers vous.



# Idées du jeu à appronfondir
* rajouter dans le Jpanel à droite les remerciements (hors jeu)
* rajouter dans le JPanel à gauche une notion de vitesse instantanée des fantômes et de Ladybug
* rajouter dans le JPanel à gauche une notion de comportement des fantômes

# Idées techniques
* modifier le pattern Observer avec java 9+ (peut être via des listeners)
* faire un installer avec java 16+ pour windows (en cours par Jim)
* faire des tests unitaires avec JUnit

# Bugs détectés
* J'ai défini dans le fichier backlog.txt les différentes améliotions techniques et fonctionnelles du projet. Ce fichier me sert de base pour gérer les évolitions (c'est mon Jira à moi ;))

