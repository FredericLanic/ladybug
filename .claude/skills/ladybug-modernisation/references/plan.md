# Plan de modernisation de Ladybug

## Vue d'ensemble
| # | Étape | Pourquoi | Patterns ou outils |
|---|---|---|---|
| 0 | Outillage et état de référence | Créer la branche depuis `develop`, voir le code avant d'y toucher, figer le comportement « avant », travailler avec la documentation à jour | Graphify, Context7, checklist et captures |
| 1 | Socle | Faire compiler et démarrer, aligner les versions | – |
| 2 | Lanceur et XML externe | Un démarrage clair, une configuration modifiable sans recompiler | Factory |
| 3 | Tests | Un filet de sécurité avant de refactoriser | JUnit 5 |
| 4 | Découplage du modèle | Un modèle indépendant de Swing, base du réseau | Observer, State, Snapshot |
| 5 | `PlayerInput` | Un contrôleur par personnage, plusieurs fantômes joueurs | Strategy |
| 6 | Fantômes configurables | Un fantôme = assemblage de capacités, perception | Composition, Strategy |
| 7 | Niveaux et rendu du labyrinthe | Niveaux en fichiers, rendu lisible et thématisable | Builder, autotiling, Flyweight |
| 8 | Langage moderne, ménage, documentation | Lisibilité, `ARCHITECTURE.md`, test « Jeff » | records, `switch` moderne |
| 9 | **Réseau** | Un poste Ladybug, un ou plusieurs postes fantômes | Strategy (`NetworkInput`) |
| 10 | Optionnel | `jpackage`, JavaFX ou libGDX | – |

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

### Constats vérifiés
- **Build cassé** : `~/.m2/settings.xml` pointe vers un Nexus `localhost:8081` qui ne tourne pas.
- **Pas de XML externe prioritaire** : un XML posé à côté du jar est ignoré (testé sur `releases/ladybug-0.5.0.jar`), car `JarLauncher` et `ClassPathXmlApplicationContext` ne lisent que l'intérieur du jar.
- **Plantage au démarrage sur Mac** : `UnsatisfiedLinkError` de Jamepad dans `XBoxOneControllerManager.initialize()`, qui fait échouer tout le contexte Spring.
- **Incohérences** :
  - versions : pom `java.version=16`, `MainLadybug` exige Java 19+, bannière en `v0.6.0`, CI en JDK 17 ;
  - dépendances : `spring-boot-starter-web` inutile, dépôt jcenter mort ;
  - aucun test ;
  - `java.util.Observable` est déprécié.
- **Sous-classes de fantômes quasi identiques** :
  - `Clyde`, `Inky` et `Pinky` ne diffèrent que par la vitesse initiale ;
  - `Blinky` ajoute `BlinkySpeedIndex` ;
  - le comportement est un `switch` sur `GhostBehaviour` dans `Ghost.moveByBehaviour()`.
- **Fantôme joueur limité** :
  - le flag « joué par un humain » est stocké dans l'enum `GhostsBodyImages` (`GameModel.java:242-245`), un état global sur une enum d'images (backlog CTEC1) ;
  - `GhostsGroup` envoie une seule `ghostRequest` à tous les fantômes non calculés.
- **Modèle couplé à Swing** : `GameModel` contient un `javax.swing.Timer` et notifie ses observateurs via `Observable`.
- **Mode lampe pensé pour un écran unique** :
  - les règles sont codées en dur dans `CentralView.drawMaze` (3,5 cases) et `drawGhosts` (4 cases) ;
  - la zone éclairée entoure tous les joueurs locaux ;
  - Ladybug est toujours visible.

## Règles de travail
- **Aucune action (branche, build, modification) sans feu vert explicite.** On discute d'abord.
- Une branche dédiée, `modernisation`, créée depuis **`origin/develop`** (et non `master`). **Un commit par sous-étape**, le diff montré avant chaque commit, et la convention de message `[TECH] …`.
- Les constats ci-dessus ont été faits sur `master`. Ils seront revus sur `develop` à l'étape 0.C.5.
- Le jeu reste jouable après chaque étape.
- `~/.m2/settings.xml` n'est pas modifié. Choix à faire : relancer le Nexus, ou utiliser un fichier de réglages temporaire pointant vers Maven Central.

---

## Étape 0 : outillage et état de référence (Graphify, Context7, photo « avant »)
Objectif : avant de toucher au code, se doter de deux outils et figer une référence :
- **Graphify**, une carte du code, refaite après chaque grande étape pour voir l'effet concret des refactorings ;
- **Context7**, la documentation à jour des bibliothèques, pour que les montées de version ne se fassent pas « de mémoire » ;
- **une photo « avant »** du comportement du jeu (build, lancement, checklist, captures d'écran).

**Base de travail : `develop`, pas `master`.** `origin/develop` a 34 commits d'avance sur `master` (Observer remplacé par des événements, SLF4J, internationalisation, rendu des niveaux par Strategy, réorganisation des packages…). Vérifié le 18/11/2026 par HTTPS : les branches GitHub sont identiques aux références locales.

### 0.0 Création de la branche (juste avant l'outillage)
- `git switch -c modernisation origin/develop`. Les fichiers non suivis (`.claude/`, le PDF) passent tels quels sur la branche.
- Premier commit : le skill `ladybug-modernisation` et le PDF du plan, avec la convention de message de Frédéric (`[TECH] …`).
- Ainsi, le `.gitignore` et `.claude/skills/graphify/` de l'étape 0.A seront commités sur `modernisation`, pas sur `master`.
- Pousser se fera plus tard, en HTTPS avec `gh` : SSH est bloqué par le réseau actuel.

### 0.A Graphify : la carte du code
[Graphify](https://github.com/Graphify-Labs/graphify) (open source, Apache-2.0/MIT) transforme le code en **graphe de connaissances** interrogeable depuis Claude Code. Le code Java est analysé **localement** avec tree-sitter : sans IA, et rien ne quitte la machine.

0.1 **Installation** (machine actuelle : Python 3.9, Graphify demande Python 3.11 ou plus) :
- `brew install uv` : gestionnaire Python qui installe sa propre version sans toucher à celle du système ;
- `uv tool install graphifyy` : paquet officiel, avec **deux « y »** (les autres paquets `graphify*` n'ont rien à voir) ;
- `graphify install --project` dans `ladybug/` : crée `.claude/skills/graphify/`, à côté de `ladybug-modernisation` ;
- ajouter `graphify-out/` au `.gitignore`.

0.2 **Premier graphe, état « avant »**, lancé **sur la branche `modernisation`** (c'est-à-dire sur le code de `develop`) : `/graphify . --code-only`. Le mode code seul est 100 % hors ligne. Les fichiers produits dans `graphify-out/` sont :
- `graph.html` : la carte interactive ;
- `GRAPH_REPORT.md` : concepts clés et connexions surprenantes ;
- `graph.json` : le graphe complet.

0.3 **Lecture commentée ensemble** de la structure de `develop` : le package `engine/` (70 classes), le système d'événements qui remplace Observer, le rendu des niveaux par Strategy, les classes les plus connectées, les dépendances inattendues. On garde une copie datée du rapport comme référence.

0.4 **Usage pendant le chantier** :
- avant une étape : `/graphify query`, `/graphify path "A" "B"` et `/graphify explain "X"` pour mesurer l'impact d'un changement ;
- après les étapes 4, 5, 6, 7 et 8 : `/graphify . --update`, puis comparaison avec le graphe précédent.

0.5 Mise à jour du skill `ladybug-modernisation` pour qu'il sache utiliser le graphe.

**Limites connues** :
- le XML Spring (`application-context.xml`) n'est pas analysé : le câblage des fantômes est à documenter à la main dans `ARCHITECTURE.md` ;
- `.properties` et `.odt` ne sont pas pris en charge ;
- les `.md` et `.txt` (README, backlog) passeraient par le modèle de la session : on ne les inclut que plus tard, et sur décision explicite.

**Désinstallation** : `graphify uninstall --purge`, puis `uv tool uninstall graphifyy`.

### 0.B Context7 : la documentation à jour des bibliothèques
Suggéré par Halim. [Context7](https://github.com/upstash/context7) (éditeur Upstash, licence MIT) fournit à Claude la **documentation propre à chaque version** des bibliothèques au moment où il code. Sans lui, Claude travaille de mémoire, qui peut dater. Context7 ne modifie pas le code : il informe.

0.B.1 **Installation** :
- prérequis : Node.js, déjà présent (v22.14.0 via nvm) ;
- commande : `npx ctx7 setup --claude`, en **mode MCP** (serveur distant `https://mcp.context7.com/mcp`) ;
- **Frédéric doit faire lui-même** la connexion OAuth chez Upstash : création d'un compte gratuit et génération d'une clé API, dans le navigateur.

0.B.2 **Outils fournis** : `resolve-library-id` (trouver la bibliothèque) et `query-docs` (interroger sa documentation, en précisant la version).

0.B.3 **Règle d'usage**, ajoutée au skill `ladybug-modernisation` : consulter Context7 avant toute montée de version ou toute utilisation d'une API de bibliothèque, en précisant la version visée (par exemple « Spring Boot 3.5 »).

0.B.4 **Où il sert** :
- étape 1, les vraies questions sur `develop` :
  - Spring Boot 3.0.2 → 3.5.x : notes de migration ;
  - Java **21 ou 25** (les deux sont des LTS) : compatibilité de Spring Boot 3.5 ;
  - **Lombok 1.18.22**, trop ancien pour un JDK récent : quelle version minimale pour 21 et pour 25 ;
  - Jamepad : il n'est chargé que sous Windows sur `develop` (`@Conditional(WindowsHost.class)`) ;
- étape 3 : JUnit 5, AssertJ, Mockito ;
- étape 8 : SLF4J, records, pattern matching ;
- étape 9 : les API réseau.

**Points d'attention** :
- **Confidentialité** : les questions (nom de la bibliothèque et texte de la question) partent chez Upstash. Le code n'est pas censé être envoyé, mais la documentation publique ne précise pas ce qui est conservé, et le serveur n'est pas open source. Ne jamais mettre de code ni d'information personnelle dans les questions.
- **Limites d'usage** : la clé gratuite suffit pour Ladybug ; les quotas exacts ne sont pas publiés.

**Désinstallation** : `npx ctx7 remove`.

### 0.C Photographier l'état « avant »
Le projet n'a aucun test. Il faut donc une référence du **comportement** avant de toucher au code ; le graphe, lui, fixe la **structure**.

0.C.1 **Compiler `develop` tel quel**, avec un JDK explicite (`JAVA_HOME` = Corretto 21). On essaie d'abord le mode hors ligne (`mvn -o`) : le cache `~/.m2` contient déjà Spring Boot 3.0.2 et Jamepad 1.3.2, et le Nexus local est éteint. Il faut noter si ça compile, avec quel JDK, et les erreurs éventuelles (Lombok 1.18.22 notamment).

0.C.2 **Lancer le jeu sur le Mac** et vérifier :
- qu'il démarre sans manette (Jamepad n'est chargé que sous Windows sur `develop`) ;
- qu'il s'affiche correctement (sinon, regarder le correctif `MainFrame` de la branche `macos`).

0.C.3 **Checklist de jeu manuelle**, déroulée par Frédéric et notée dans `doc/checklist-jeu.md` :
- démarrage et écran de présentation ;
- une partie en solo : gommes, méga-gommes, fantômes effrayés, mort, vie gagnée, fruits, téléportation ;
- le mode 2 joueurs (ZQSD) ;
- les touches F1 à F6, Pause, Échap et Entrée (mode debug) ;
- chaque rendu de niveau disponible sur `develop`.

0.C.4 **Captures d'écran datées** de chaque niveau et de chaque rendu, rangées dans `doc/reference-avant/`. Elles serviront de comparaison après les étapes 1, 4 et 7.

0.C.5 **Mettre à jour le plan, le PDF, le skill et le dashboard** avec l'état réel de `develop` (mesures relancées sur la branche, dette technique revue). Le plan actuel avait été écrit à partir de `master`.

## Étape 1 : remettre le socle d'aplomb
1.1 Faire passer le build (`mvn compile`).
1.2 Mettre à jour `pom.xml` :
- Java 21 (LTS) ;
- Spring Boot 3.5.x ;
- Lombok récent ;
- `spring-boot-starter-web` remplacé par `spring-boot-starter` ;
- suppression du dépôt jcenter.

1.3 Rendre la manette optionnelle : `XBoxOneControllerManager` attrape l'échec du chargement natif, écrit un avertissement dans les logs et désactive la manette. Le jeu démarre au clavier.
1.4 CI : JDK 21, actions GitHub en v4.

## Étape 2 : nouveau démarrage (lanceur et XML externe)
2.1 `MainLadybug` devient un petit **lanceur** :
- lecture de la configuration ;
- choix du mode ;
- construction du contexte Spring ;
- affichage de la fenêtre via `SwingUtilities.invokeLater`.

2.2 Version lue depuis le manifest (fin de la chaîne en dur), contrôle de version de Java aligné sur le pom.
2.3 **XML externe prioritaire** :
- si `<dossier du jar>/application-context.xml` existe, il est chargé par `FileSystemXmlApplicationContext` ;
- sinon, celui du classpath est utilisé ;
- l'option `-Dladybug.config=...` permet de forcer un fichier ;
- les logs indiquent quel fichier est utilisé.

2.4 Même mécanisme pour `ladybug.properties` (`@PropertySource` classpath puis `file:`, avec `ignoreResourceNotFound`).
2.5 Modes prévus dans le lanceur dès maintenant : **solo** et **2 joueurs local**. La structure est prête pour accueillir plus tard « héberger » et « rejoindre ».
2.6 README : section « Personnaliser le jeu ».

## Étape 3 : filet de sécurité (tests)
3.1 Ajout de `spring-boot-starter-test` (JUnit 5, AssertJ, Mockito).
3.2 Tests unitaires :
- `Dijkstra` ;
- `SpeedFunction`, `BlinkySpeedIndex` et `GhostSensitiveBehaviour` ;
- `Score` ;
- cohérence des niveaux : dimensions, téléportations symétriques, positions de départ praticables.

3.3 Test de chargement du contexte Spring en mode headless : XML valide et fantômes créés.

## Étape 4 : découpler le modèle de l'affichage
4.1 Remplacer `Observable`/`Observer` par une interface typée `GameModelListener`. Sept classes sont concernées : `GameModel`, `Engine`, `CentralView`, `PageLeftView`, `PageEndView`, `GameSounds`, `XboxOneController`.
4.2 Sortir la boucle de jeu de `GameModel` dans une classe `GameLoop` dédiée. Le modèle ne dépend plus de Swing.
4.3 Introduire un **`GameSnapshot`** (records) produit à chaque tick : positions, statuts, score, gommes, mode lampe. Les vues lisent le snapshot. C'est la base du futur réseau.
4.4 Séparer clairement :
- **l'état de jeu**, décidé par le modèle (mode lampe tiré par niveau, etc.) ;
- **les préférences d'affichage locales** (chapeau, bandeau, logo, son).

## Étape 5 : un contrôleur par personnage (`PlayerInput`)
5.1 Interface `PlayerInput` (« quelle direction veut prendre ce personnage »), avec trois implémentations :
- `KeyboardInput` (flèches ou ZQSD) ;
- `XboxInput` ;
- `AiInput` (les déplacements IA actuels).

5.2 Chaque `Ladybug`/`Ghost` porte son `PlayerInput`. Ce changement :
- **supprime** le flag `computed` stocké sur l'enum `GhostsBodyImages` (CTEC1) ;
- supprime la `ghostRequest` unique de `GhostsGroup`.

5.3 Possibilité d'avoir **plusieurs fantômes joués par des humains** en local (préparation du réseau).
5.4 Un joueur est associé à **un fantôme précis**, pas à un type.

## Étape 6 : fantômes 100 % configurables (assemblage de capacités)
6.1 `GhostMoveStrategy` : `ChaseStrategy` (ex-AGGRESSIVE), `AmbushStrategy` (ex-SMART), `WanderStrategy` (ex-CALM). Le code est repris de `Ghost`.
6.2 `GhostSpeedPolicy` : `StandardSpeed` (modes NORMAL et MINUS) et `RageSpeed` (logique de Blinky).
6.3 **`Perception`**, valable pour les fantômes **et** pour Ladybug :
- `lightRadius` : rayon éclairé ;
- `detectRadius` : ex-`maximumAttackDist`, utilisé à la fois par l'IA et pour la vue du joueur humain ;
- `seesOpponentAlways` : voir l'adversaire même dans le noir ;
- même paramètre pour l'IA et pour l'humain, ce qui garantit l'équité.

6.4 **Notion d'observateur dans la vue** :
- une classe `VisibilityRule` testable répond à « pour cet observateur, qu'est-ce qui est visible ? » ;
- `CentralView` ne contient plus aucune règle en dur ;
- en écran partagé, l'observateur regroupe tous les joueurs locaux (comportement actuel conservé).

6.5 `Ghost` devient une classe concrète. `Blinky`, `Pinky`, `Inky` et `Clyde` sont supprimées et remplacées par des `<bean>` XML. L'ancienne propriété `behaviour="AGGRESSIVE"` reste acceptée comme raccourci.
6.6 La vue gère un nombre quelconque de fantômes et les couleurs inconnues (image par défaut en repli).

## Étape 7 : niveaux en fichiers
7.1 Un fichier texte par niveau dans `resources/levels/`, avec la grille et un en-tête : départ, régénération, méga-gommes, téléportations.
7.2 `LevelLoader`, avec la même priorité dossier du jar > classpath.
7.3 Conversion de `Level1NG` à `Level8NG`, puis suppression de ces classes et des anciennes `Level1`, `Level3` et `Level`.

7.4 **Rendu du labyrinthe** (`view/map/ScreenBlockView`, `level/LevelNG`, `level/CheckScreenBlockBorders`).

Principe : **le style actuel est conservé à l'identique** (contours fins, coins arrondis, pointillés de gommes) ; c'est l'implémentation qui change.
- **Bordures typées** : le masque de bits `int` (`LEFT=1`, `UP=2`…) devient un `EnumSet<Border>`.
- **Autotiling** :
  - les vingt-cinq conditions de `displayBorders()` sont remplacées par un calcul de configuration des voisins et une **table** qui associe chaque configuration aux segments et arcs à dessiner ;
  - chaque cas est testable unitairement.
- **Fusion du calcul des bordures** : `screenBlockValue()` et `CheckScreenBlockBorders` deviennent une seule classe dédiée, par exemple `MazeBorderCalculator`. Une seule source de vérité au lieu de la double liste `screenBlocks`/`viewBlocks`.
- **Image pré-rendue** :
  - les murs sont dessinés une seule fois par niveau dans une `BufferedImage` ;
  - à chaque tick, on ne dessine plus que les gommes, les fruits, les téléporteurs et les personnages ;
  - le mode lampe devient un masque circulaire (`clip`) appliqué à cette image.
- **Thème** : couleurs (murs, gommes, méga-gommes, régénération, téléportation) et épaisseur du trait sont sorties dans un objet `MazeTheme` configurable en XML. C'est la base des skins (CTEC27). Plusieurs styles de labyrinthe deviennent possibles plus tard.
- **Vérification** : capture du rendu avant/après sur chaque niveau. Les images doivent être identiques.

## Étape 8 : modernisation du langage et ménage
8.1 `record`, `switch` avec pattern matching, `var` lorsqu'il clarifie le code.
8.2 Logs : passage de `commons-logging` à SLF4J ; suppression de `log4j.properties` et de `src/main/java/log4j2-test.xml`.
8.3 Ménage :
- `view/tests/*` ;
- `TraceurAdvice` s'il n'est pas utilisé ;
- `images/_inWork` déplacé hors de `src`.

8.4 **Documentation finale** : `ARCHITECTURE.md` complet, `package-info.java`, README technique (« pour les contributeurs »). Relecture du point de vue d'un développeur qui découvre le projet (le test « Jeff »). Le graphe Graphify final, comparé à celui de l'étape 0, illustre le chemin parcouru.

## Étape 9 : mode réseau (plus tard, progressivement)
Architecture : **un hôte qui fait autorité**.
- L'hôte fait tourner le seul `GameModel`.
- Les clients envoient uniquement leurs entrées.
- L'hôte renvoie un `GameSnapshot` à chaque tick.

9.1 Protocole simple : sockets TCP et records sérialisés, sans framework lourd.
9.2 `NetworkInput`, une nouvelle implémentation de `PlayerInput` (étape 5).
9.3 Lanceur : modes **héberger** (`--host`) et **rejoindre** (`--join host:port`). Le client n'assemble que la vue et l'envoi des entrées.
9.4 Chaque client a sa propre vue, avec son personnage comme observateur. L'hôte peut n'envoyer à chaque client que ce que son personnage perçoit (anti-triche).
9.5 Les éléments décidés par l'hôte (mode lampe, tirages aléatoires) sont transmis. Les préférences restent locales.

## Étape 10 : optionnel
- Packaging natif avec `jpackage`.
- Interface JavaFX ou libGDX, facilitée par le découplage de l'étape 4.

---

## Questions ouvertes (à reprendre plus tard, sans blocage)
- **Pouvoirs** : fixes par type de fantôme (XML), ou évolutifs en cours de partie (bonus, niveau) ? *L'architecture permet les deux (capacités = objets remplaçables).*
- **Attribution des fantômes en réseau** : plusieurs joueurs sur le même type, ou un fantôme différent par joueur ? *L'association joueur ↔ fantôme précis permet les deux.*
- **Réseau local seulement, ou Internet aussi** (découverte des parties, NAT, sécurité) ?
- **Classement des options** : lesquelles relèvent de l'état partagé et lesquelles des préférences locales ?
- **Rayons de lumière** : l'écart entre 3,5 et 4 cases dans `CentralView` est-il voulu ?
- **Nexus local** : à relancer, ou build via Maven Central ?
- **Graphify et documents** : inclure plus tard README, backlog et `.txt` dans le graphe (analyse par le modèle), ou rester en mode code seul ?
- **Graphify en mode « toujours actif »** (`graphify claude install`, avec des hooks qui consultent le graphe automatiquement) : à activer ou non ?

## Fichiers critiques
- `pom.xml`, `MainLadybug.java`, `application-context.xml`, `properties/GameProperties.java`
- `controller/xboxone/*`, `controller/key/KeyGameController.java`
- `model/GameModel.java`, `engine/Engine.java`
- `body/ghost/*` (dont `GhostsGroup`), `constant/ghost/image/GhostsBodyImages.java`
- `view/CentralView.java`, `level/*`, `.github/workflows/*.yml`

## Vérification (à chaque étape)
0. Étape 0 :
   - `graphify-out/graph.html` s'ouvre et montre les packages de `com.kycox.game` ; `graphify-out/` est ignoré par git ;
   - Context7 répond à une question test, par exemple « notes de migration Spring Boot 3.0 → 3.5 » ;
   - la branche `modernisation` existe depuis `origin/develop`, avec un premier commit (skill et PDF) ;
   - `doc/checklist-jeu.md` et `doc/reference-avant/` existent, et l'état de compilation de `develop` est noté.
1. `mvn clean verify` : compilation et tests au vert.
2. `java -jar target/ladybug-*.jar` : le jeu démarre (même sans manette) et une partie se joue.
3. Étape 2 : un XML modifié à côté du jar est pris en compte, le log l'indique, et un XML cassé produit un message clair.
4. Étape 5 : deux fantômes joués en local, au clavier et à la manette.
5. Étape 6 : ajouter un 5ᵉ fantôme dans le XML externe, sans recompiler.
6. Étape 6 : en mode lampe, la visibilité respecte la `Perception` de chaque personnage.
7. Étape 7 : un `level9.txt` ajouté à côté du jar est jouable.
8. Étape 9 : deux postes sur le même réseau, l'un Ladybug, l'autre fantôme.
