## Contexte
Ladybug est un projet personnel (≈140 classes, 9 100 lignes) écrit pour se perfectionner en Java (MVC, Spring, patterns), à l'époque d'OXiane BZH.

Objectifs, dans l'ordre :
1. **Factoriser, améliorer, moderniser** le code.
2. **Rendre le jeu plus générique**, en conservant et renforçant la configuration XML (ajouter, retirer ou régler des fantômes sans recompiler).
3. **Ensuite seulement**, mettre en place progressivement un **mode réseau** : un poste joue Ladybug, un ou plusieurs autres postes jouent des fantômes.

Le réseau est une étape à part, en fin de parcours. Les étapes précédentes sont toutefois conçues pour le préparer, afin de ne pas refactoriser deux fois.

### Fil rouge : rendre le code accessible et compréhensible
Un développeur Java expérimenté (Jeff) n'a pas osé se plonger dans le programme, faute de temps. Le but est qu'un développeur extérieur puisse comprendre et contribuer rapidement, et que **Frédéric comprenne chaque pattern introduit**.

- **Pédagogie à chaque étape** :
  - chaque refactoring nomme le design pattern utilisé ;
  - il explique pourquoi ce pattern a été choisi ;
  - il montre un « avant / après ».
- **Classes courtes, une seule responsabilité, méthodes au nom explicite.** Les grosses méthodes à conditions imbriquées sont découpées.
- **Javadoc** sur les interfaces et les points d'extension, ainsi qu'un `package-info.java` par package (rôle du package).
- **Cohérence de nommage (décidé)** : le **code en anglais** (classes, méthodes, variables), les **commentaires et la Javadoc en français**. La règle s'applique progressivement, dans les fichiers touchés à chaque étape.
- **Document `ARCHITECTURE.md`** : vue d'ensemble, diagramme des couches, cycle d'un tick, liste des patterns utilisés et emplacement de chacun dans le code, guide « ajouter un fantôme / un niveau / un thème ».

- **Guide des patterns (`doc/patterns/`)** : une fiche par design pattern, rédigée au moment où il est introduit dans le code et lisible à **trois niveaux** :
  1. **Junior, l'idée en une image** : une analogie du quotidien en 2 ou 3 phrases, sans jargon (ex. Strategy : « une manette qu'on branche sur n'importe quel personnage »).
  2. **Confirmé, le pattern dans Ladybug** :
     - le problème concret dans notre code ;
     - le code avant / après ;
     - un schéma simple (Mermaid) ;
     - où le trouver dans le code (liens `fichier:ligne`).
  3. **Senior / vétéran, le recul** :
     - les compromis ;
     - quand **ne pas** l'utiliser ;
     - les alternatives (ex. lambda plutôt que classe, `sealed`/records en Java moderne) ;
     - les pièges classiques ;
     - le lien avec les principes SOLID.

  Chaque fiche se termine par un **petit exercice** facultatif, par exemple « ajoutez une stratégie `PatrolStrategy` ». Un `doc/patterns/README.md` sert de sommaire, avec un parcours de lecture conseillé. La Javadoc des classes concernées renvoie vers la fiche correspondante.

| Pattern | Où dans le plan |
|---|---|
| Observer (listener typé) | Étape 4 : `GameModelListener` |
| Strategy | Étapes 5 et 6 : `PlayerInput`, `GhostMoveStrategy`, `GhostSpeedPolicy` ; existant dans `model/strategy` |
| State | Étape 4 : statuts de jeu et de fantôme (amélioration de l'existant) |
| Composition plutôt qu'héritage | Étape 6 : fantôme = assemblage de capacités |
| Snapshot / objet valeur immuable (records) | Étape 4 : `GameSnapshot` |
| Factory / Builder | Étapes 2 et 7 : lanceur, `LevelLoader` |
| Table de correspondance (autotiling) | Étape 7 : rendu du labyrinthe |
| Template / Flyweight (image pré-rendue) | Étape 7 : rendu du labyrinthe |
| Command (changement de mur daté) | Étape 7 bis : `MazeChange` |

### Constats vérifiés (sur `develop`, revus le 18/11/2026 à l'étape 0.C.5)
- **Build cassé en l'état** :
  - `~/.m2/settings.xml` pointe vers un Nexus `localhost:8081` qui ne tourne pas. Contournement de l'étape 0 : un fichier de réglages temporaire vers Maven Central ;
  - **Lombok 1.18.22 ne compile pas en JDK 21** (`NoSuchFieldError JCImport.qualid`). Avec 1.18.36, mis temporairement, la compilation passe.
- **Pas de XML externe prioritaire** : `Application` charge `application-context.xml` avec `ClassPathXmlApplicationContext`, donc uniquement depuis l'intérieur du jar. Testé sur `releases/ladybug-0.5.0.jar` ; le code de chargement de `develop` est le même.
- **Plus de plantage au démarrage sur Mac** : sur `develop`, Jamepad n'est chargé que sous Windows (`@Conditional(WindowsHost.class)` sur `XboxRequest`). Le jeu démarre en JDK 21 sur le Mac, sans manette. (Sur `master`, `UnsatisfiedLinkError` dans `XBoxOneControllerManager.initialize()`.)
- **Incohérences** :
  - versions : pom `java.version=19`, bannière « v0.6.0-SNAPSHOT for Java 19+ », CI (`maven-release.yml`) en **JDK 16** ;
  - dépendances : `spring-boot-starter-web` déjà retiré, mais le dépôt jcenter (mort) est toujours dans le pom ;
  - aucun test ;
  - `java.util.Observable` a disparu : il est remplacé par des événements dont la source est `GameModel` (0 classe concernée).
- **Sous-classes de fantômes quasi identiques** :
  - `Clyde`, `Inky` et `Pinky` (39 lignes chacune) ne diffèrent que par la vitesse initiale (`getRealIndexSpeed` ou `getRealIndexSpeedMinus`) ;
  - `Blinky` ajoute `BlinkySpeedIndex` ;
  - le comportement est un `switch` sur `behaviour` dans `Ghost.moveByBehaviour()`.
- **Fantôme joueur limité** :
  - le flag « joué par un humain » est stocké dans l'enum `GhostsBodyImages` (`Ghost.isComputed()` lit `color.isComputed()`, tirage dans `GameModel.java:242-246`), un état global sur une enum d'images (backlog CTEC1) ;
  - `GhostsGroup.move` envoie une seule `ghostRequest` à tous les fantômes non calculés.
- **Modèle couplé à Swing** : `GameModel` importe `javax.swing.*` et contient un `javax.swing.Timer` (`programTimer`).
- **Mode lampe pensé pour un écran unique** :
  - les règles sont codées en dur dans `CentralView.drawMaze` (3,5 cases) et `drawGhosts` (4 cases) ;
  - la zone éclairée entoure tous les joueurs locaux ;
  - Ladybug est toujours visible.
