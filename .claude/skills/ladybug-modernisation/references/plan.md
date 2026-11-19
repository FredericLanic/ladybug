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
| 7 bis | **Labyrinthe dynamique** (idée de Hammed) | Portes, murs mobiles, niveaux générés ; murs déclenchés par les joueurs avec les pouvoirs (6.7, 9.6) | Observer, Command, Strategy, Builder |
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

## Règles de travail
- **Aucune action (branche, build, modification) sans feu vert explicite.** On discute d'abord.
- **Une branche par étape**, créée depuis `develop` à jour et nommée `etape-N-sujet` (par exemple `etape-1-socle`). On évite `modernisation/…`, que git refuse tant que la branche `modernisation` existe. L'étape 0 a été faite sur `modernisation`, créée depuis **`origin/develop`** (et non `master`). **Un commit par sous-étape**, le diff montré avant chaque commit, et la convention de message `[Tech] …`.
- **Fusion par pull request vers `develop`**, une PR par étape, en *merge commit* (pas de squash, pour garder les commits de sous-étape). Avant la fusion, on passe le robot, la checklist et Graphify, et on résume le résultat dans la description de la PR. Décidé le 18/11/2026.
- Les constats ci-dessus ont été faits sur `master`, puis revus sur `develop` le 18/11/2026 (étape 0.C.5).
- Le jeu reste jouable après chaque étape.
- `~/.m2/settings.xml` n'est pas modifié. Décidé le 18/11/2026 : build via **Maven Central**, avec un réglage propre au projet ; le Nexus local n'est pas relancé.

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
- Branche poussée le 18/11/2026 par SSH (clé `id_ed25519` ajoutée sur GitHub) ; elle suit `origin/modernisation`. Elle sera fusionnée dans `develop` par une PR, la première du chantier.

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

**Règle : toujours lancer Graphify avec `GRAPHIFY_BACKEND=none`** (par exemple `GRAPHIFY_BACKEND=none graphify update .`). Sans cette variable, et faute de clé d'API, Graphify se sert du CLI Claude Code pour nommer les communautés, ce qui envoie des noms de classes à Anthropic (incident du 18/11/2026). Test T3 du 18/11 : sans IA, le graphe du jeu est strictement identique (mêmes nœuds, mêmes liens). Seuls les noms des communautés restent génériques, ou sont tirés de leur classe centrale.

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

> **Ordre de réalisation (décidé le 18/11/2026, complété le 19/11/2026)** : étape 1 → **3.1, 3.2 et 3.4** (tests unitaires, sondes de log) → **3.5.1** (horloge unique en tics, C24) → étape 2 → **3.3** (test du contexte Spring, fait avec 2.7). **Tout cela est fait au 19/11/2026**, ainsi que 3.5.2 (golden master). Reste à décider : 3.5.3 à 3.5.5 avant l'étape 4, ou en parallèle. La numérotation est conservée. Raisons : les tests ont besoin d'un build sain et des versions de test fixées par Spring Boot 3.5 (étape 1) ; les tests unitaires de logique pure protègent ensuite le premier vrai changement de code (étape 2) ; le test du contexte Spring, lui, n'est faisable proprement qu'après l'étape 2, car le `component-scan` de `com.kycox` crée aujourd'hui les 14 vues Swing (`MainFrame`…), ce qui échouerait en mode headless.

## Étape 1 : remettre le socle d'aplomb
1.1 Faire passer le build (`mvn compile`) **via Maven Central**, avec un réglage propre au projet, sans toucher à `~/.m2/settings.xml` (décidé le 18/11/2026 ; le Nexus local n'est pas relancé).
1.2 Mettre à jour `pom.xml` :
- Java 21 (LTS), décidé le 18/11/2026 (Java 25 éventuellement à l'étape 8) ;
- Spring Boot 3.5.x ;
- Lombok récent ;
- `spring-boot-starter-web` remplacé par `spring-boot-starter` ;
- suppression du dépôt jcenter.

1.3 Rendre la manette optionnelle : `XBoxOneControllerManager` attrape l'échec du chargement natif, écrit un avertissement dans les logs et désactive la manette. Le jeu démarre au clavier.
1.4 CI : JDK 21, actions GitHub en v4, `::set-output` remplacé par `$GITHUB_OUTPUT`. **Nouvelle CI `build.yml`** (décidée le 18/11/2026) : `mvn verify` à chaque PR et à chaque push sur `develop`.

## Étape 2 : nouveau démarrage (lanceur et XML externe)
> **Fait le 19/11/2026** (branche `etape-2-lanceur`), **avec Spring Boot** à la demande de Frédéric (« JE VEUX QUE CE SOIT SPRINGBOOT ») :
> - 2.1 `SpringApplicationBuilder` + `headless(false)` (Spring Boot passe sinon la JVM en mode sans écran, d'où l'échec de l'ancien essai commenté) + `@ImportResource` du XML ; `MainLadybug` est un `ApplicationRunner` qui ouvre la fenêtre par `invokeLater` ; ancien format de log conservé ;
> - 2.2 version lue dans le manifest (bannière Spring Boot et log) ; le contrôle « Java 19+ » était du code mort (une JVM plus ancienne refuse de charger les classes compilées pour Java 21) : retiré. **D03 corrigée** ;
> - 2.3 et 2.4 fichiers externes selon la convention Spring Boot (**C29** : dossier courant et `config/`, et non « à côté du jar ») : `application-context.xml` (`--ladybug.config=file:…` pour en imposer un) et `ladybug.properties` (`spring.config.import`) ; `StartupReport` écrit les fichiers utilisés ; XML invalide → message clair et arrêt (**C27**) ;
> - 2.5 `LaunchMode.LOCAL` seulement ; le choix 1 ou 2 joueurs reste dans le jeu (**C28**) ;
> - 2.7 et 3.3 profil « gui » par la méta-annotation `@GuiComponent` (21 classes d'interface) ; `SpringContextTest` et `ExternalSpringXmlTest` chargent le vrai contexte sans écran ;
> - 2.8 écran de la souris, place réellement disponible (**D18 corrigée**) ; à la demande de Frédéric : bloc centré, zone du bas collée au labyrinthe, entre 2 et 3 cases de haut, dessin de Ladybug sur toute sa bande, plafonné à une case ;
> - 2.6 README « Personnaliser le jeu », fiche `doc/patterns/configuration-externalisee.md`.
> 128 tests ; robot de fin d'étape dans `doc/reference-apres/etape-2/`.

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
2.7 Une fois le lanceur en place : test 3.3 (chargement du contexte Spring en mode headless), avec un cas pour le XML externe prioritaire.
2.8 **Écran du jeu selon la souris** (demandé par Frédéric le 18/11/2026) :
- la fenêtre s'ouvre sur l'écran où se trouve la souris au lancement (`MouseInfo.getPointerInfo().getDevice()`) ;
- les dimensions viennent de la place réellement disponible sur cet écran (bornes de l'écran moins barre de menus et Dock, via `Toolkit.getScreenInsets`), et non plus de `Toolkit.getScreenSize()`, qui donne l'écran principal barre de menus comprise ;
- corrige au passage le bas du labyrinthe coupé sur Mac : 118 px réservés en haut et en bas, il reste 687 px pour un labyrinthe de 720.
- la hauteur de la zone du haut est divisée par deux (`topDimension` = `getBorderHeight() / 2`), comme l'a demandé Frédéric. Ce correctif, d'abord proposé en commit séparé à l'étape 1, est reporté ici (choix de Frédéric, 18/11/2026).

## Étape 3 : filet de sécurité (tests)
*3.1, 3.2 et 3.4 se font **juste après l'étape 1** (faits le 19/11/2026), puis 3.5.1, avant l'étape 2 ; 3.3 se fait **après l'étape 2** (voir 2.7).*

3.1 Ajout de `spring-boot-starter-test` (JUnit 5, AssertJ, Mockito).
3.2 Tests unitaires :
- `Dijkstra` ;
- `SpeedFunction`, `BlinkySpeedIndex` et `GhostSensitiveBehaviour` ;
- `Score` ;
- comportement commun des fantômes (décidé le 19/11/2026 : il n'existe pas de « case visée » propre à chaque fantôme, tous poursuivent Ladybug par `Dijkstra`) : chasse, fuite quand il a peur, retour au point de régénération puis `AUTOGENERATED` ;
- cohérence des niveaux : dimensions, téléportations symétriques, positions de départ praticables.

3.4 **Sondes de log** (décidé le 18/11/2026, C22), faites avec 3.1 et 3.2 : un message `INFO` pour chaque événement de jeu que la checklist vérifie à la main et que les logs ne montrent pas encore : gomme mangée (avec le score), fantôme mangé et retour au point de régénération, fruit apparu et mangé, téléportation, Ladybug mangée par le fantôme joueur. Le robot et Claude vérifient alors ces points en lisant les logs. Restent à l'œil humain, en 5 minutes par étape : le son et l'affichage (animations, clignotement, labyrinthe entier). Le comportement des fantômes (checklist 3.1 à 3.3) se vérifie par des tests unitaires de leurs stratégies, ajoutés en 3.2.

3.5 **Tests autonomes** (décidé le 19/11/2026, C23 ; source : doc « Ladybug — Stratégie de tests autonomes », https://claude.ai/artifact/4mXUqpsGiwLczZzegYUyUP, écrit par Frédéric depuis son téléphone). Objectif : chaque PR prouve, sans passage humain, que Ladybug se comporte comme avant ; le passage humain se réduit au son et au plaisir de jeu. Ce chantier **s'ajoute** à 3.1, 3.2 et 3.4 : les tests unitaires deviennent sa couche 5, les sondes de log préfigurent son journal d'événements.

Cahier des charges : règles, affichage, son, plantage ou blocage ; référence = comportement actuel figé (golden master), bugs compris, puis corrections une par une ; le code du jeu peut changer pour devenir testable, sans changer de comportement ; exécution en local et en CI à chaque PR, bloquante ; modes solo, 2 joueurs, touches spéciales, manette ; macOS, Windows, Linux ; en cas d'échec, Claude analyse et propose, Frédéric valide ; objectif : score de mutation PIT ≥ 60 %. Le bot de test vit dans `src/test/`, jamais dans le jar ; le jeu n'a ni mode ni option « bot ».

Cinq couches, toutes sans fenêtre et lancées par `mvn verify`, sur une horloge en tics et une graine aléatoire fixées par le test :
1. golden master : une partie rejouée (graine + touches) donne le même journal d'événements et les mêmes images clés (dessinées hors écran, comparées avec tolérance) ;
2. scénarios pas à pas : fantôme mangé, fruit, téléportation, fantôme humain qui mange Ladybug, fin de super-gomme ;
3. bot joueur qui vise (gomme la plus proche, fuite, chasse après méga-gomme), avec des règles vérifiées en continu (score jamais en baisse, aucun mur traversé, état qui évolue) ;
4. son : faux lecteur qui enregistre les demandes, durées injectées ;
5. tests unitaires, ajoutés au fil des corrections.

Sous-étapes :
- 3.5.1 **Rendre le jeu testable**, **avant l'étape 2** (C24). **Fait le 19/11/2026** (branche `etape-3-horloge`, 5 commits) :
    - 3.5.1a `GameRandom` : une seule source de hasard, graine dans les logs, `-Dladybug.seed=…` pour rejouer ;
    - 3.5.1b `TickScheduler` (attentes du modèle en battements, sur le fil Swing : **D22 corrigée**) et `BeatPacer` (boucle à pas fixe, 25 battements/s réels ; sans lui, la boucle battait à ~43 ms et la musique du début de niveau était jouée deux fois) ;
    - 3.5.1c `SuperPowerTimer` en battements (375, dont 125 de clignotement), sondes « flashing » et « over » ;
    - 3.5.1d messages affichés en battements, `IncrementScoreTimer` supprimé : le modèle n'utilise plus aucun `java.util.Timer` ;
    - 3.5.1e `GameModel.beat()` public, `-Dladybug.loop.autostart=false`, fiche `doc/patterns/game-loop.md`, robot : 174 statuts identiques à la référence.
    - Choix faits (C25 et 19/11) : minuteries en battements, pas d'horloge pilotable ; le décompte continue pendant la pause, comme avant ; durées des sons et correction de la musique redemandée à chaque battement reportées en 3.5.3 ; minuteries de l'affichage (`SimpleTimer`, `WaitAndDoActionAfterTimer` de `CentralView`, `TimerView`) laissées à l'étape 4.
    - Critère « une partie à graine fixe rejouée deux fois à l'identique » **reporté à 3.5.2** : il faut l'enregistreur de parties, et un contexte Spring sans fenêtres (étape 2).
- 3.5.2 Golden master solo puis 2 joueurs. **Fait le 19/11/2026** (branche `etape-3-golden-master`, 5 commits) : `GameDriver` (jeu entier sans écran, battement par battement ; sauvegarde réglable `ladybug.save.dir`), `GameJournal` (événements + état chaque seconde, C31), `DeterminismTest` (même graine et mêmes touches → même partie, vérifié par sabotage), `GoldenMaster` et trois références validées par Frédéric (C32) : `solo-seed42`, `two-players-seed42`, `bot-seed42` ; `AimingBot` + `GameRules` (règles vérifiées à chaque battement), partie du bot jusqu'au **niveau 2** (C26). Fiche `doc/patterns/golden-master.md`. Images clés reportées (C30). Dette trouvée : D23 (actions des fantômes créées seulement au premier battement de présentation).
- 3.5.3 Touches spéciales (F1 à F7, pause, Échap, Entrée), manette Xbox derrière une interface (corrige aussi D19, double initialisation de Jamepad), faux lecteur de sons, durées des sons derrière une interface ; le son du début de niveau ne doit plus être redemandé à chaque battement de `LEVEL_STARTING` (risque résiduel de musique rejouée, vu en 3.5.1b). Fin : chaque touche et chaque son a au moins un scénario.
- 3.5.4 CI sur trois systèmes (matrice Ubuntu, Windows, macOS dans `build.yml`). Fin : vert, ou écarts expliqués (pause sur Mac, marquée « bug connu »).
- 3.5.5 Confiance mesurée : PIT dans Maven, tests ajoutés là où des mutants survivent. Fin : ≥ 60 %, seuil inscrit dans la CI.

Risques : le passage aux tics change le rythme ressenti (conversion exacte, robot et partie de Frédéric avant fusion) ; images différentes selon le système (rendu hors écran, tolérance, sinon une référence par système) ; golden master trop strict (journal d'événements métier plutôt que pixels, mise à jour de référence proposée par Claude et validée par Frédéric).

À trancher plus tard : place de 3.5.2 à 3.5.5 par rapport à l'étape 2 (seule 3.5.1 est fixée avant).

3.3 Test de chargement du contexte Spring en mode headless : XML valide et fantômes créés. **Après l'étape 2**, qui sépare la construction du contexte de l'affichage de la fenêtre.

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
6.7 **Pouvoir « agir sur les murs »** (variante C du labyrinthe dynamique, réalisée après l'étape 7 bis) : une capacité configurable en XML, par exemple « ce fantôme peut ouvrir un mur », « Ladybug peut poser un mur ». Elle passe par le socle de l'étape 7 bis (`MazeChange`, `ReachabilityChecker`). C'est une réponse possible à la question des pouvoirs.

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

## Étape 7 bis : labyrinthe dynamique (idée de Hammed, 18/11/2026)
Idée proposée par Hammed après une démo : le labyrinthe change pendant la partie. Frédéric retient **les quatre variantes** : A. portes, B. murs mobiles, C. murs déclenchés par les joueurs, D. labyrinthe généré.

**Constats dans le code (`develop`)** :
- les fantômes s'adapteraient d'eux-mêmes : `Dijkstra.getShorterWay` reconstruit son graphe depuis `ScreenData` à chaque appel ;
- l'affichage est figé : les bordures sont calculées une seule fois par niveau (`checkViewBlockBorder`), avec la double liste `dataScreenBlocks` / `viewScreenBlocks`. L'étape 7 (`MazeBorderCalculator`, source de vérité unique, image pré-rendue) est donc un **prérequis**.

**7 bis.0 Socle commun** (construit une seule fois) :
- `MutableMaze` : seul point d'entrée pour changer un mur (`setWall(case, bool)`) ;
- `MazeChangeEvent` (**Observer**) : prévient la vue (redessiner seulement la zone touchée de l'image pré-rendue), les fantômes et, plus tard, le réseau ;
- `MazeChange` (**Command**) : un changement décrit comme un objet daté, par exemple « fermer (12,5) à t+30 s » ;
- `ReachabilityChecker` : refuse tout changement qui rendrait une gomme inaccessible (parcours en largeur, testé unitairement) ;
- `OccupancyRule` (**Strategy**) : que faire si un mur se ferme sur un personnage. Par défaut, attendre que la case soit libre (à confirmer).

Les variantes ne diffèrent que par **qui décide** du changement, sur le même principe que `PlayerInput` (étape 5).

7 bis.1 **A. Portes** : décrites dans le fichier du niveau (`door: 12,5 period=20s`). Premier palier : il installe et montre le socle.
7 bis.2 **B. Murs mobiles** : une `MazeMutationStrategy` (planifiée ou aléatoire), réglable en XML.
7 bis.3 **D. Labyrinthe généré** : un `MazeGenerator` (**Builder**) produit un niveau, validé par le même `ReachabilityChecker` et chargé comme les fichiers de l'étape 7.
**C. Murs déclenchés par les joueurs** : traitée en 6.7 (pouvoir configurable) puis en 9.6 (réseau), une fois les capacités et les entrées par personnage en place.

**Vérification** : une porte s'ouvre et se ferme, les fantômes changent de chemin, aucune gomme ne devient inaccessible ; capture avant/après.

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
9.6 **Labyrinthe dynamique en réseau** : l'hôte seul décide des changements de murs (étape 7 bis), qui voyagent dans le `GameSnapshot`. Un client qui utilise le pouvoir « agir sur les murs » (6.7) envoie une demande, et l'hôte la valide.

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
- **Graphify et documents** : inclure plus tard README, backlog et `.txt` dans le graphe (analyse par le modèle), ou rester en mode code seul ?
- **Q11, labyrinthe dynamique** : prévenir le joueur avant un changement (clignotement du mur) ? Labyrinthe généré symétrique comme les niveaux actuels, ou libre ? Règle quand un mur se ferme sur un personnage (attendre, pousser) ?
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
1. `mvn clean verify` : compilation et tests au vert (des tests existent à partir de 3.1 et 3.2, réalisés juste après l'étape 1).
2. `java -jar target/ladybug-*.jar` : le jeu démarre (même sans manette) et une partie se joue.
3. Étape 2 : un XML modifié à côté du jar est pris en compte, le log l'indique, et un XML cassé produit un message clair. Avec deux écrans, le jeu s'ouvre sur celui de la souris, et le labyrinthe est entier.
4. Étape 5 : deux fantômes joués en local, au clavier et à la manette.
5. Étape 6 : ajouter un 5ᵉ fantôme dans le XML externe, sans recompiler.
6. Étape 6 : en mode lampe, la visibilité respecte la `Perception` de chaque personnage.
7. Étape 7 : un `level9.txt` ajouté à côté du jar est jouable.
8. Étape 9 : deux postes sur le même réseau, l'un Ladybug, l'autre fantôme.
