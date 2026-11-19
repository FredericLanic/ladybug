# Journal des échanges : modernisation de Ladybug

Le fil chronologique des discussions entre Frédéric et Claude, avec le raisonnement derrière chaque décision. Le plan (`plan.md`) dit **quoi** faire ; ce journal dit **pourquoi** et **comment on en est arrivé là**.

Ajoutez une entrée à chaque nouvelle discussion importante. Le skill le demande dans sa section « Mettre ce skill à jour ».

---

## 2026-11-17 / 18 : session de cadrage

### Découverte du projet
- Claude a parcouru le projet : ≈140 classes, 9 100 lignes, `com.kycox.game`. C'est un MVC avec un `GameModel` observable, un pattern State dans `model/strategy`, des interfaces de contrat par couche, Spring configuré en XML et un `Engine` qui branche les observateurs.
- Le README décrit le jeu : Ladybug, cousine de Pacman, avec le T-shirt OX bleu ; quatre fantômes à la personnalité distincte ; les touches F1 à F6 ; un mode 2 joueurs avec ZQSD.

### Pourquoi ce projet existe
- Frédéric a été embauché par Alex chez OXiane BZH. Il a écrit Ladybug **pour se perfectionner en Java**.
- Il juge son niveau « moyen, à l'image de Ladybug ». Claude a noté des points forts : State, contrats, Dijkstra, injection par constructeur.
- Jeff, un vétéran Java, n'a pas osé se plonger dans le code parce que ça lui aurait pris trop de temps. Objectif : un code accessible et des patterns que Frédéric comprend.

### La configuration XML
- Claude avait d'abord proposé de passer le XML Spring en `@Configuration` Java. **Frédéric a refusé** : le XML permet d'ajouter, retirer ou régler des fantômes sans recompiler. Claude a retiré sa proposition.
- Frédéric pensait qu'un XML posé à côté du jar était prioritaire. **Claude l'a testé** : un `application-context.xml` volontairement cassé, posé à côté de `releases/ladybug-0.5.0.jar`, a été ignoré. La cause : `JarLauncher` + `ClassPathXmlApplicationContext` ne voient que l'intérieur du jar. Le correctif prévu : `FileSystemXmlApplicationContext` si le fichier externe existe (étape 2.3).
- Le même test a révélé un plantage de Jamepad (`UnsatisfiedLinkError`) qui fait tomber tout le contexte Spring sur Mac. Correctif prévu : rendre la manette optionnelle (étape 1.3).

### Méthode de travail
- Après validation du plan, Claude a voulu créer une branche et lancer un build. **Frédéric a dit « non, je veux échanger avant tout »**. Règle retenue : aucune action sans feu vert explicite.
- Le build échoue parce que `~/.m2/settings.xml` pointe vers un Nexus `localhost:8081` éteint. Choix encore ouvert : relancer le Nexus ou passer par Maven Central. On ne touche pas au `settings.xml`.

### Objectifs exprimés par Frédéric
1. Factoriser, améliorer, moderniser (« plus générique, plus à la mode »).
2. Revoir le démarrage (`main`).
3. Plus tard, et progressivement, un **mode réseau** : un poste joue Ladybug, un ou plusieurs autres jouent des fantômes. Frédéric a demandé que le réseau soit une **étape à part, après** la factorisation.

### Mode lampe et visibilité
- Frédéric a signalé que l'affichage en cercle autour d'un joueur existe déjà : c'est le mode lampe (F6, ou tirage aléatoire par niveau dans `LitLampMode`).
- Analyse de Claude : dans `CentralView`, la lumière entoure tous les joueurs locaux (3,5 cases pour le labyrinthe, 4 pour les fantômes), et Ladybug est toujours visible. En réseau, chaque poste doit éclairer autour de **son** joueur : il faut une notion d'observateur.
- Claude a demandé si Ladybug toujours visible était volontaire. Réponse de Frédéric : **« tout dépend du type de fantôme et de ses pouvoirs »**. D'où la `Perception` par personnage (étape 6.3). `maximumAttackDist` est déjà une forme de perception côté IA, et le même paramètre servira à l'humain.
- Deux questions (pouvoirs fixes ou évolutifs, attribution des fantômes en réseau) ont été **reportées à plus tard** à la demande de Frédéric. L'architecture garde les deux options possibles.

### Le style d'affichage du labyrinthe
- Frédéric a demandé si Claude avait vu le style. Analyse : le niveau ne contient que des `B` et des `N` ; les murs sont déduits des voisins (masque de bits dans `LevelNG`, puis `CheckScreenBlockBorders`), et `ScreenBlockView` les dessine avec environ 25 conditions. Le rendu est réussi (contours bleus fins, coins arrondis) mais l'implémentation est fragile.
- Décision : **garder le style à l'identique** et refaire l'implémentation (autotiling par table, image pré-rendue, `MazeTheme`). C'est l'étape 7.4.

### Accessibilité et pédagogie
- Frédéric veut des classes propres, de belles méthodes et des design patterns **qu'il comprenne**.
- Langue : **code en anglais, commentaires en français** (validé).
- Chaque pattern aura une fiche à trois niveaux (junior, confirmé, vétéran) plus un exercice. Format validé sur l'exemple Strategy (voir `pattern-sheet.md`).
- Frédéric donnera son ressenti au fur et à mesure quand il ne comprend pas.

### Documents produits
- Plan : `~/.claude/plans/linked-cooking-valley.md`, copié dans `references/plan.md`.
- PDF : `doc/plan-modernisation-ladybug.pdf`, dont la source est `references/plan-pdf.html`, rendue avec Chrome headless.
- Ce skill, rangé dans le projet à la demande de Frédéric (`.claude/skills/ladybug-modernisation/`). Rien n'est commité.
- Frédéric voulait garder toute la conversation. Claude a expliqué ce qui persiste d'une session à l'autre (mémoire, plan, skill, reprise de conversation) et ce qui ne persiste pas.

### Graphify
- Frédéric a demandé d'« ajouter ce skill » au dépôt public Graphify-Labs/graphify. Claude a déconseillé : le dépôt ne lui appartient pas, le skill n'y servirait à rien, et il contient des informations personnelles. Frédéric voulait en fait **utiliser Graphify sur Ladybug**.
- Graphify construit un graphe du code. Java est analysé localement, sans IA. Il faut Python 3.11 ou plus (la machine a 3.9), donc un passage par `uv`. Le XML Spring n'est pas analysé.
- Décision : étape 0, outillage. On commence en mode `--code-only`. Rien n'est installé pour l'instant.

### Context7
- Halim a parlé à Frédéric d'un outil pour gérer les mises à jour Java, appelé « contextset ». Claude n'a rien trouvé sous ce nom et a proposé Context7 ou OpenRewrite. **Frédéric a confirmé : Context7.**
- Context7 (Upstash, MIT) fournit la documentation à jour, version par version, des bibliothèques. Il s'installe avec `npx ctx7 setup --claude`, en mode MCP. Frédéric fera lui-même la connexion OAuth. Les questions partent chez Upstash : pas de code ni de données personnelles dans les questions.
- Décision : étape 0.B. Règle dans le skill : consulter Context7 avant toute montée de version ou nouvelle API. Rien n'est installé pour l'instant.
- OpenRewrite (migration automatique par « recettes ») reste une idée possible pour la partie mécanique de l'étape 1, mais n'a pas été retenu à ce stade.
- Frédéric a demandé : « retiens bien tout ce qu'on se dit ». C'est l'origine de ce journal.

### Dashboard
- Halim a montré à Frédéric un dashboard tenu par Claude. Frédéric en voulait un pour Ladybug, puis un autre plus tard pour son travail. Ce sera un sujet à part : il faudra d'abord vérifier ce qui est confidentiel et ce que l'employeur autorise.
- Sections choisies : avancement, santé du code, dette et décisions, outillage et journal. **Frédéric a choisi d'afficher tout le contexte** (Jeff, Halim, OXiane, son niveau Java).
- Le dashboard a été créé : https://claude.ai/artifact/SpChAsmu1H63WxzCEYsp3d. C'est une page privée qui ne lit pas le disque en direct. Claude y reporte les mesures de `scripts/metrics.py` et les statuts.
- Mesures au 18/11/2026 :
  - 138 classes et 9 152 lignes ;
  - 0 test ;
  - 4 sous-classes de fantômes ;
  - 6 classes qui utilisent Observable/Observer ;
  - Java 16 et Spring Boot 3.0.2 dans le pom ;
  - build cassé.

### Sauvegarde dans Documents/Claude
- Frédéric veut que tout soit sauvegardé dans `~/Documents/Claude`. Claude a créé `~/Documents/Claude/ladybug/` avec plan, PDF, skill, mémoire, données et page du dashboard, et conversations (brutes et lisibles), plus un README d'index.
- Ce sont des **copies** : les originaux restent là où Claude les lit. Le script `scripts/sauvegarde.sh` refait la copie, et la règle « sauvegarder à la fin de chaque session » est ajoutée au skill et à la mémoire.

### Branche de travail : découverte de `develop`
- Frédéric a rappelé qu'il fallait travailler sur une branche dédiée. Ses choix : nom `modernisation` ; premier commit avec le skill et le PDF ; « analyser d'abord » les branches existantes avant de choisir la base.
- Analyse en lecture seule. `git fetch` a échoué (SSH vers GitHub inaccessible) : les branches distantes datent du dernier fetch local.
- **Constat majeur : toute l'analyse avait été faite sur `master`, alors que `origin/develop` est bien plus avancé.** Il a 34 commits d'avance (288 fichiers modifiés) et intègre `levelStyle` et `issue-11`. Il contient déjà :
  - Java 19, sans `spring-boot-starter-web` ;
  - **Observer supprimé**, remplacé par des événements dont la source est `GameModel` (c'était l'étape 4.1 du plan) ;
  - SLF4J (étape 8.2) et l'internationalisation ;
  - « un start un peu plus propre » et un `Application.java` (lien avec l'étape 2) ;
  - un **rendu des niveaux par images et par design pattern Strategy**, avec plusieurs rendus (lien avec l'étape 7.4) ;
  - une grosse refacto et des déplacements de packages (`engine/` regroupe 70 classes) ;
  - « plateforme indépendante » (issue #11).
- Ce qui reste comme sur `master` : les sous-classes Blinky, Clyde, Inky et Pinky, le XML Spring, Jamepad, et toujours 0 test.
- `master` n'a en propre que 11 commits de CI et de release (0.7.0 du 12/03/2024).
- `macos` (2023, MainFrame et Mac) et `SCS` (2022, essai de Strategy) ne sont pas dans `develop`. `postTrainingStringCore` est déjà incluse.
- Vérification ensuite par HTTPS (`git ls-remote`) : les branches GitHub sont **identiques** aux références locales (develop `f456c22`, master `7673806`). L'analyse est donc à jour.
- Problème SSH : les clés existent déjà (`id_ed25519`, `id_rsa`, **sans passphrase**). C'est le **réseau** qui bloque SSH : le port 22 tombe en timeout, et SSH via `ssh.github.com:443` est coupé (connection reset). HTTPS passe. Pistes proposées : HTTPS avec token pour pousser sur ce réseau, SSH tel quel sur un autre réseau, et ajouter une passphrase aux clés. Rien n'a été modifié.
- Recommandation de Claude : partir de `develop` et **refaire l'état des lieux** (plan, mesures, dashboard) sur cette base avant de commencer. En attente de la décision de Frédéric.

### Avant l'étape 1, et ajustement de l'étape 0
- Frédéric a demandé ce qu'il fallait prévoir avant de lancer l'étape 1. Vérifications faites sur `develop` :
  - le pom est en `0.6.0-SNAPSHOT`, Java 19, Spring Boot 3.0.2, Lombok 1.18.22 (trop ancien pour un JDK récent), avec jcenter encore présent ;
  - la CI se limite à `maven-release.yml` ;
  - Jamepad n'est chargé que sous Windows (`@Conditional(WindowsHost.class)`).
- Sur la machine : Corretto 21 (trois versions) et OpenJDK 25, qui est le JDK par défaut. Le cache `~/.m2` contient Spring Boot 3.0.2 et Jamepad 1.3.2, ce qui devrait permettre de compiler `develop` hors ligne.
- Points à régler avant l'étape 1 : base `develop`, Java 21 ou 25, Nexus ou Maven Central, photo « avant », définition de « terminé », stratégie de fusion, branche `macos`.
- Frédéric a validé **les quatre ajustements de l'étape 0, reportés partout** (plan, PDF de 10 pages, skill, dashboard) :
  1. graphe Graphify lancé sur la branche `modernisation` (code de `develop`) ;
  2. questions Context7 revues (Spring Boot 3.0.2 → 3.5, Java 21 ou 25, Lombok 1.18.22, Jamepad) ;
  3. nouvelle sous-étape **0.C, photographier l'état « avant »** (compilation hors ligne avec JDK 21, lancement sur Mac, `doc/checklist-jeu.md`, captures dans `doc/reference-avant/`, puis mise à jour de tous les documents pour `develop`) ;
  4. **0.0, création de la branche depuis `origin/develop` juste avant l'outillage**.
- Restent en attente : Java 21 ou 25, Nexus ou Central, l'ordre outillage / étape 1 (le plan prévoit l'étape 0 d'abord), la stratégie de fusion et la façon de pousser. Rien n'a encore été exécuté dans git.

### Étape 0 lancée (18/11/2026, feu vert : « vas-y, lance l'étape 0 »)
- **0.0** :
  - branche `modernisation` créée depuis `origin/develop` (commit `f456c22`) ;
  - le suivi automatique de `origin/develop` a été **retiré** (`--unset-upstream`), sinon un `git push` serait parti sur `develop` ;
  - la convention de message la plus récente est `[Tech] …` ;
  - le skill et le PDF sont indexés pour le premier commit, **qui n'est pas encore fait** (diff à montrer).
- **0.A Graphify** :
  - `uv` 0.13.9 installé par Homebrew, `graphifyy` 0.9.112 installé par uv ;
  - `graphify install --project` a créé le skill **et activé tout seul le mode « toujours actif »** (`CLAUDE.md`, `.claude/CLAUDE.md`, des hooks dans `.claude/settings.json`). Comme la question Q07 n'est pas tranchée, ces trois fichiers ont été **mis de côté** (copie dans `~/Documents/Claude/ladybug/graphify-toujours-actif/`) et seul le skill est gardé ;
  - erreur de Claude, corrigée aussitôt : le `.gitignore` n'avait pas de retour à la ligne final, et la ligne ajoutée a donné `bingraphify-out/`. Le fichier a été réécrit (`target`, `bin`, `graphify-out/`) ;
  - le graphe porte sur 154 fichiers de code : 1 429 nœuds, 3 596 liens, 75 communautés, aucun cycle d'import. Les nœuds centraux sont `ScreenData` (89), `ScreenBlock` (60), `GameModel` (52), `CurrentProgramStatus` (52) et `Ghost` (50). Rapport copié dans `doc/reference-avant/graphify-report-2026-11-18.md` ;
  - ⚠️ sans clé d'API, Graphify a utilisé **le CLI Claude Code** de la machine pour nommer les communautés. Ont été envoyés des noms de classes et de constantes, pas le code source, et uniquement chez Anthropic. Ce n'était pas le « 100 % hors ligne » annoncé : les prochains lancements se feront avec `GRAPHIFY_BACKEND=none`.
- **0.C.1, compilation de `develop`** (Maven hors ligne, JDK Corretto 21.0.8) :
  - le cache Maven est refusé par défaut, parce que les artefacts viennent d'autres dépôts que le Nexus éteint. Contournement : `-Daether.enhancedLocalRepository.trackingFilename=__ignore__` ;
  - **avec Lombok 1.18.22, `develop` ne compile pas en JDK 21** (`NoSuchFieldError JCImport.qualid`), ce qui confirme le blocage prévu ;
  - avec Lombok 1.18.36 (présent dans le cache) mis **temporairement** dans le pom puis restauré, la compilation passe ;
  - en revanche, l'empaquetage échoue faute de plugins dans le cache (surefire, plexus-archiver, dependency-analyzer). Le jeu n'a donc pas encore pu être lancé.
- Bloquants en attente de Frédéric :
  1. accès réseau Maven (Central via HTTPS, ou Nexus) pour finir le build ;
  2. connexion OAuth Context7 (0.B) ;
  3. validation des diffs avant commit.
- Point d'entrée sur `develop` : `Application.main` charge `application-context.xml`, puis `MainLadybug.launchTheGame()`.
- Frédéric a répondu « ok » aux trois points :
  - **commits faits** : `3ab1f8c [Tech] Ajout du skill de modernisation et du plan` et `8df14e1 [Tech] Outillage : Graphify` ;
  - **build via Maven Central** avec un fichier de réglages temporaire (dans le scratchpad, sans miroir). `~/.m2/settings.xml` n'a pas été touché. Avec Lombok 1.18.36 mis temporairement puis pom restauré : `target/ladybug-0.6.0-SNAPSHOT.jar` (22 Mo) ;
  - **le jeu démarre sur le Mac en JDK 21**, sans erreur et sans plantage lié à la manette. La bannière affiche encore « for Java 19+ ».
- `doc/checklist-jeu.md` a été créée (6 sections, avec une colonne « avant » et une colonne « après étape 1 »). Elle attend que Frédéric joue et fasse les captures. Context7 attend toujours sa connexion OAuth.

### Divers
- Le message « Update installed · Restart to update » vient de l'application Claude : une mise à jour à appliquer en redémarrant, sans effet sur le projet.
- Le message « API Error: Connection lost mid-response » était une coupure réseau : la réponse en cours a été tronquée et le travail a repris ensuite.

### 18/11/2026 (après-midi) : partie de Frédéric et idée de Hammed
- Frédéric a joué une partie (logs) : fin du niveau 1 puis passage au niveau 2 (2.9 ✅), Échap → N reprend, Échap → O arrête, Échap en présentation ferme le jeu (5.7 ✅). Aucune erreur.
- Script `doc/reference-avant/robot/LadybugRobot.java` écrit et compilé, **pas encore lancé** (attend les autorisations macOS). En lisant `KeyboardController` : F7 (camouflage) absente de la checklist ; la touche Pause n'existe pas sur un clavier Mac ; le rendu tiré au hasard n'est pas loggé.
- **Idée de Hammed : un labyrinthe dynamique.** Discussion ouverte (question Q11), rien de décidé.
  - Constat favorable : `Dijkstra.getShorterWay` reconstruit son graphe depuis `ScreenData` à chaque appel, donc les fantômes s'adapteraient d'eux-mêmes à un mur qui bouge.
  - Constat défavorable aujourd'hui : les bordures sont calculées une seule fois par niveau (`checkViewBlockBorder`), avec la double liste `dataScreenBlocks`/`viewScreenBlocks`. C'est exactement ce que l'étape 7 (`MazeBorderCalculator`, une seule source de vérité, image pré-rendue) doit assainir.
  - Proposition de Claude : en faire une étape 7 bis, après l'étape 7, avec un premier palier simple (portes qui s'ouvrent et se ferment) avant des murs mobiles ou des niveaux générés.
- Réponse de Frédéric à « quelle variante ? » : **les 4** (portes, murs mobiles, murs déclenchés par les joueurs, labyrinthe généré). Claude propose un socle commun puis un ordre par paliers (A → B → D, et C relié aux pouvoirs des fantômes et au réseau). En attente de validation avant de toucher au plan.
- Frédéric valide l'organisation proposée (« oui, mets à jour le plan partout ») :
  - **plan** : nouvelle **étape 7 bis** (socle commun 7 bis.0, puis 7 bis.1 portes, 7 bis.2 murs mobiles, 7 bis.3 labyrinthe généré), **6.7** pouvoir « agir sur les murs », **9.6** labyrinthe dynamique en réseau, pattern **Command** ajouté au tableau, question **Q11** ;
  - **skill**, **PDF** (12 pages), **dashboard** (étape `s07b` avec num 7.5, décision C15, question Q11, journal j16 et j17) et **sauvegarde** mis à jour.
- Frédéric demande que **toutes les idées et tout le plan** soient lisibles sur la page web du dashboard. Ajouts :
  - section **« Le plan en détail »** : le plan complet, une section dépliable par titre « ## » (18 sections), avec « Tout déplier / Tout replier ». Données dans `data/ladybug/plan`, produites par le nouveau script `scripts/plan_dashboard.py`. Le rendu Markdown de la page est construit en DOM (pas d'`innerHTML`) ;
  - section **« Idées »** : 10 idées (Hammed, Halim, Frédéric, Claude) avec statut et place dans le plan (`data/ladybug/ideas`). Le compte apparaît dans la phrase d'en-tête ;
  - l'étape 7 bis s'affiche enfin « 7 bis » (champ `label`) au lieu de 7,5 ;
  - procédure de mise à jour du skill complétée (idées et plan à resynchroniser à chaque changement).
- **0.B Context7 installé** à la demande de Frédéric (« execute npx ctx7 setup --claude ») :
  - la commande est interactive ; Claude l'a relancée en non interactif avec les choix du plan : `npx ctx7 setup --claude --mcp --oauth --project -y` ;
  - **portée projet** (et non globale), pour ne pas l'activer dans les projets professionnels de Frédéric ;
  - fichiers créés : `.mcp.json` (serveur `https://mcp.context7.com/mcp/oauth`, sans clé ni secret), `.claude/rules/context7.md` (règle « consulter Context7 pour toute question de bibliothèque ») et `.claude/skills/context7-mcp/SKILL.md` ;
  - reste à faire par Frédéric : redémarrer Claude Code, `/mcp` → context7 → connexion OAuth chez Upstash dans le navigateur. Puis test T4 (notes de migration Spring Boot 3.0 → 3.5).

### 18/11/2026 (soir) : test T3, Graphify sans IA
- Référence copiée dans `~/Documents/Claude/ladybug/graphify-out-2026-11-18/`.
- Relance avec `GRAPHIFY_BACKEND=none graphify update .`, en retirant `~/.local/bin` du `PATH`, ce qui masque le CLI `claude` : aucun appel possible à une IA. Message de Graphify : « no LLM needed ». Graphify a aussi fait sa propre sauvegarde dans `graphify-out/2026-11-18/`.
- Résultat : 1 483 nœuds et 3 744 liens, contre 1 429 et 3 596 dans la référence. **Aucun nœud ni lien supprimé, et les 3 596 liens entre nœuds communs sont strictement identiques.** Les 54 nœuds ajoutés viennent tous de fichiers nés après la référence : `LadybugRobot.java` (33, plus 15 classes Java qu'il importe), `plan_dashboard.py` (4) et `.mcp.json` (2). Le code du jeu donne donc exactement le même graphe sans IA. **T3 ✅**
- Communautés : 85 au lieu de 75, à cause des nœuds ajoutés. Graphify en a renommé 21 d'après leur classe centrale, sans IA.
- Piste : exclure `doc/reference-avant/` et `.claude/` du graphe, pour qu'il ne décrive que le jeu.
- Décision de Frédéric (« 1 ») : la règle `GRAPHIFY_BACKEND=none` devient permanente. Elle est ajoutée au plan (0.4), au PDF et au skill. L'exclusion de `doc/reference-avant/` et `.claude/` du graphe reste en suspens.

### 18/11/2026 (soir) : tests T11, T13 et T7
- **T11 ✅** : 2.9 et 5.7 reportés « OK » dans `doc/checklist-jeu.md`, d'après la partie de Frédéric.
- **T13 ✅** : `metrics.py` relancé sur la branche (149 classes, 9 495 lignes, 0 test, 4 sous-classes de fantômes, 0 Observable, Java 19, Spring Boot 3.0.2). Les « constats vérifiés » du plan ont été revus sur `develop` :
  - **ce qui a changé** : plus de plantage Jamepad sur Mac, `spring-boot-starter-web` retiré, Observable remplacé par des événements, pom en Java 19 ;
  - **nouveau** : Lombok 1.18.22 bloque la compilation en JDK 21, et la CI est en **JDK 16** (et non 17 comme sur `master`) ;
  - **toujours vrai** : Nexus éteint, jcenter, aucun test, sous-classes de fantômes, flag humain dans `GhostsBodyImages` (désormais `GameModel.java:242-246`), `javax.swing.Timer` dans `GameModel`, mode lampe codé en dur, XML chargé depuis le classpath.
  - Plan et PDF mis à jour.
- **T7 ✅** : rebuild avec `clean package` (Central, Lombok 1.18.36 temporaire, pom restauré). Les 463 fichiers du nouveau jar sont identiques, au CRC près, à ceux du jar du 18/11 à 14 h 16. Ce jar-là contenait en plus 5 restes d'une ancienne compilation (`log4j.properties`, `images/ghosts/color/blue.png`, les dossiers `model/` et `contract/`), parce qu'il avait été construit sans `clean`. Leçon : **toujours `clean`**. La commande est corrigée dans la checklist.
- Dashboard : packages remesurés sur develop (6 packages de premier niveau au lieu des 20 de master), indicateurs de santé et descriptions mis à jour. Dette revue : **D02 résolue** ; D01 (Lombok), D03, D04, D05, D07, D08, D12, D13, D14 et D15 reformulées selon l'état de `develop`.

### 18/11/2026 (soir) : test T4, Context7
- Frédéric s'est connecté avec `/mcp` (« Signed in and connected to context7 »).
- `resolve-library-id` « Spring Boot » : retenu `/spring-projects/spring-boot`, qui propose des versions précises (v3.5.16, v3.5.9, v4.0.3…). Il existe aussi `/websites/spring_io_spring-boot` (versions 3.4, 3.5 et 4.0).
- `query-docs` sur `/spring-projects/spring-boot/v3.5.16` : vraie documentation, sources citées. **T4 ✅**
  - Spring Boot 3.5 demande **au moins Java 17 et fonctionne jusqu'à Java 25** : Java 21 et Java 25 sont donc tous les deux possibles pour l'étape 1 (question encore ouverte) ;
  - pour la migration, il faut ajouter temporairement `spring-boot-properties-migrator` (scope runtime), qui signale les propriétés renommées ou supprimées, puis le retirer.
- Limite : le détail des changements cassants version par version est dans les « release notes » du wiki GitHub, et pas dans cette documentation. À consulter version par version à l'étape 1.

### 18/11/2026 (soir) : premier essai du robot (T9), échoué
- Lancé sur demande de Frédéric (« fait T9 pour moi »), avant qu'il ait donné les autorisations macOS.
- Résultat : le jeu démarre, mais les captures sont grises et unies (pas d'Enregistrement de l'écran), la fenêtre n'est pas localisée, et les touches n'arrivent pas : le jeu reste en `PROGRAM_PRESENTATION` et le robot s'arrête au bout de 20 s, comme prévu.
- Vérification : `System Events` → `UI elements enabled` = **false**, donc Accessibilité non accordée à Terminal.app (l'app qui héberge Claude Code).
- Le dossier de sortie, inutilisable, a été supprimé. Le robot est à relancer une fois les autorisations données.

### 18/11/2026 (soir) : robot relancé (T9), résultat partiel ; pause, Frédéric rentre chez lui
- Autorisations accordées à Terminal.app (Accessibilité `true`, captures avec du vrai contenu).
- Le robot a fait 35 captures et son `rapport.md` dans `doc/reference-avant/2026-11-18/` :
  - démarrage, F1, choix 1 ou 2 joueurs ✅, déplacements ✅, F2 à F7 et Entrée (de 0,7 à 3,2 % des pixels changent) ;
  - **Pause** : pas concluant (0,90 % de différence entre deux captures en pause, alors qu'on attendait environ 0). Il n'y a pas de touche Pause sur un clavier Mac, à revoir ;
  - Échap → N : retour en partie ✅ ;
  - rendus : 7 parties sur 10, puis arrêt sur un problème de timing (`ASK_FORCE_END_GAME` atteint, puis retour à `IN_GAME` 0,24 s après : un Échap ou un O a sans doute été reçu en double).
- Rendus vus sur les captures : Lignes bleues (partie 5), Lignes rouges et blocs colorés (parties 2 et 4), tuiles « pierre » orange (parties 1, 6 et 7). Il reste à savoir si ces tuiles sont le rendu 492 ou le 584 (vignettes à comparer dans le scratchpad), et le 4e rendu n'a pas été vu.
- Les parties 1, 3 et 7 s'affichent en mode lampe (seule la zone autour de Ladybug est visible), probablement parce que F6 était resté actif. À vérifier.
- **À reprendre** : identifier 492 ou 584 ; rendre plus robuste la séquence Échap → O du robot et la touche Pause ; relancer la section 6 ; remplir la checklist (sections 1, 5 et 6) ; mettre à jour le dashboard (journal, outillage « reference ») ; T12.

### 18/11/2026 (soir, reprise après « continue ») : T9 et T12 terminés
- Tuiles orange = rendu **584** ; tuiles grises au dessus rouge = rendu **492**.
- Corrections du robot :
  - `stopCurrentGame` et `checkEscape` relèvent la position dans les statuts **avant** d'envoyer la touche (nouvelle méthode `waitForStatusSince`), pour ne plus rater un statut qui change très vite ;
  - la fin de partie est réessayée jusqu'à 3 fois si le jeu ne demande pas confirmation ;
  - un avertissement est ajouté sur la touche Pause, absente des claviers Mac ;
  - 12 parties au lieu de 10 pour les rendus.
- Second passage complet, sans erreur : 45 captures, `rapport.md` et `ladybug.log` dans `doc/reference-avant/2026-11-18/` (environ 3 Mo). Le dossier du premier passage partiel a été remplacé.
- **Les 4 rendus sont vus** : 492 (8 parties sur 12), lignes rouges (2, plus la partie solo), lignes (2), 584 (partie à 2 joueurs).
- Vérifié sur les captures : F1, F2, F3, F4 (cache-œil), F5 (bandana), F6, F7 (camouflage), Entrée (cercles de debug), déplacements, 2 joueurs (le fantôme suit ZQSD), sortie par Échap (code 0).
- **Constats** :
  - **Pause KO sur Mac** : `VK_PAUSE` n'arrive pas au jeu, les fantômes bougent pendant la « pause », et il n'existe pas d'autre touche ;
  - **mode lampe tiré au hasard** (`LitLampMode`), avec une probabilité qui augmente avec le niveau ;
  - **aide F1 incomplète** (ni F6, ni F7, ni Pause, ni Entrée) ;
  - le retour inexpliqué en `IN_GAME` du premier passage n'est pas reproduit.
- Checklist remplie : sections 1, 2.1, 4.1, 5 (avec une nouvelle ligne 5.9 pour F7) et 6, plus les remarques.
- **T9 ✅ et T12 ✅** (rapport Graphify, captures et rapport du robot). Dette D16 (pause sur Mac) et D17 (aide incomplète) ajoutées au dashboard, étape à confirmer avec Frédéric.
- Restent pour l'étape 0 : T10 (partie de Frédéric : sections 2.2 à 2.10, 3 et 4.2), puis T14, T15 et T16.

### 18/11/2026 (soir, dans le train) : partie T10 et /doctor
- Partie de Frédéric (18 h 52 – 18 h 57, logs dans le scratchpad) : niveaux 1 et 2 terminés, arrêt au niveau 3 avec Échap → O, 4 méga-gommes et « super power », 4 « New life for Ladybug » (vie gagnée ou renaissance : à préciser), aucune erreur. Il attend encore le retour de Frédéric pour remplir 2.2 à 2.10, la section 3 et 4.2.
- `/doctor` : installation native saine, version 2.1.327 à jour (mises à jour automatiques en arrière-plan désactivées), aucun hook, contexte léger. Validé par Frédéric :
  - `.claude/rules/context7.md` **supprimé** : il doublonnait les instructions du serveur Context7 (environ 440 tokens par session, est.). Copie gardée dans `~/Documents/Claude/ladybug/doctor/` ;
  - `"permissions": {"defaultMode": "auto"}` ajouté dans `~/.claude/settings.json` (s'applique à tous les projets). Copie de l'ancien fichier gardée dans le même dossier.
- Conseils de `/usage` : faire `/clear` entre deux sujets (par exemple avant l'étape 1), car le journal du skill garde le contexte.
- Retour de Frédéric sur T10 : gommes, fantôme mangé, vie gagnée, fruits et téléporteurs OK (2.2, 2.4, 2.6, 2.7, 2.8). 2.3 OK d'après les logs. Le comportement des fantômes est difficile à voir : astuce Entrée (panneau debug à gauche) ajoutée en section 3. Restent 2.5, 2.10, 3.1 à 3.3 et 4.2.
- Deux autres parties (19 h 11 – 19 h 12 et 19 h 14 – 19 h 16) : 4 morts puis « Ladybug lost the game », méga-gomme, Échap → O. Les 4 « New life » de la première partie étaient bien des vies gagnées (aucune mort ce jour-là). Frédéric : « tout est ok » pour 2.5, 2.10, 3.1 à 3.3 et 4.2.
- **T10 ✅**. La checklist « avant » est complète : tout est OK, sauf 5.6 (Pause), KO sur Mac.
- Restent T14 (cohérence des documents), T15 (revue du diff et commit) et T16 (étape 0 « Terminée »).

### 18/11/2026 (soir) : étape 0 terminée (T15, T16)
- Choix de Frédéric : commiter les captures, `.idea/` dans le `.gitignore`, 3 commits.
- Commits : `feb4a99 [Tech] Outillage : Context7`, `1ac73d4 [Tech] Photo « avant » : checklist, robot et captures`, `bdd0842 [Tech] Plan et skill mis à jour pour develop`. Rien n'est poussé (SSH bloqué sur le réseau habituel ; à faire en HTTPS avec `gh` ou depuis un autre réseau).
- Dashboard : étape 0 « Terminée », entrée j23.
- **Prochaine étape : 1, le socle.** Questions à trancher avant : Java 21 ou 25 (Spring Boot 3.5 accepte les deux), Nexus ou Maven Central, stratégie de fusion vers `develop`, façon de pousser.
- **Branche poussée sur GitHub** (18/11, dans le train) : aucune des deux clés SSH n'était enregistrée sur le compte GitHub. Frédéric a ajouté `id_ed25519` (SHA256:XLVzbPb2…) dans les réglages GitHub, puis `git push -u origin modernisation` : la branche suit maintenant `origin/modernisation`. `develop` et `master` n'ont pas été touchées.
- GitHub (Dependabot) signale 83 vulnérabilités sur la branche par défaut (9 critiques, 38 élevées, 26 moyennes, 10 faibles) : à examiner à l'étape 1 (montée de Spring Boot et des dépendances).

### 18/11/2026 (soir) : une branche par étape
- Frédéric demande s'il vaut mieux continuer sur `modernisation` ou créer une branche par étape. Claude recommande une branche par étape (diff lisible par pattern, retour arrière facile, point stable pour le robot et Graphify).
- **Décision (C16)** :
  - une branche `etape-N-sujet` par étape, créée depuis `develop` à jour (pas `modernisation/…`, que git refuse tant que la branche `modernisation` existe) ;
  - une PR par étape vers `develop`, fusionnée en merge commit, sans squash ;
  - robot, checklist et Graphify avant chaque fusion, résumés dans la PR ;
  - la branche `modernisation` fait la première PR.
- C17 : on pousse en SSH (clé enregistrée sur GitHub). Q10 est tranchée ; Q09 se réduit à « develop → master ».
- Mis à jour : règles de travail du plan, PDF (12 pages), skill (principe 2, section « Base de travail », note réseau), dashboard (p03, C16, C17, Q09, j25).
- Les PR demandent `gh` (`brew install gh` puis `gh auth login`), ou se créent sur le site de GitHub.
- Commit `d49ca5e [Tech] Clôture de l'étape 0 : une branche par étape` poussé sur `origin/modernisation`. Prochaine action : PR `modernisation` → `develop` (site de GitHub, ou `gh` une fois installé).
- `gh` 2.105.0 installé par Homebrew, connecté par Frédéric (compte FredericLanic, protocole SSH).
- **PR #14 créée** : `modernisation` → `develop`, « [Tech] Étape 0 : outillage et état de référence », 6 commits, https://github.com/FredericLanic/ladybug/pull/14. À fusionner par Frédéric en merge commit.
- **PR #14 fusionnée** à la demande de Frédéric (« La PR me convient, fusionne-la ») par `gh pr merge 14 --merge` : merge commit `69b2710` sur `develop`. La branche `develop` locale est créée et suit `origin/develop`. L'étape 1 partira de là (`etape-1-socle`).
- Choix de Frédéric : la ligne de journal en attente partira avec le premier commit de l'étape 1. La branche `modernisation` (entièrement fusionnée) est **supprimée** en local et sur GitHub ; son contenu reste dans `develop` (merge `69b2710`).

### 18/11/2026 (soir) : tests unitaires d'abord, ou l'étape prévue ?
- Question de Frédéric. Première réponse de Claude : faire l'étape 1, puis l'étape 3, puis l'étape 2. Frédéric : « tu trouves mieux ? ». En vérifiant, Claude affine sa réponse : `application-context.xml` fait un `component-scan` sur `com.kycox`, qui crée 14 vues Swing, donc le test 3.3 du contexte en mode headless échouerait avant l'étape 2.
- **Décision (C18)** : ordre de réalisation 1 → 3.1 et 3.2 (tests unitaires de logique pure) → 2 → 3.3 (test du contexte Spring, nouvelle sous-étape 2.7). La numérotation est conservée.
- Mis à jour : plan (encadré « Ordre de réalisation », 2.7, notes en 3 et 3.3, vérification n° 1), PDF (12 pages), skill (« Ordre des étapes »), dashboard (p04, p06, p07, p18, s02, s03, C18, j28).

### 18/11/2026 (soir, à la maison) : lancement de l'étape 1
- Choix de Frédéric : **Java 21** (C19), **Maven Central** avec un réglage propre au projet (C20).
- Constats pour l'étape 1 :
  - Spring Boot 3.5.16 et Lombok 1.18.48 sont les dernières versions sur Maven Central ;
  - Jamepad (`com.github.WilliamAHartman`) n'est pas sur Central : il faut garder jitpack ;
  - jcenter est déclaré avec l'identifiant `central`, ce qui masque le vrai Central ;
  - le `maven-compiler-plugin` force `source/target 17`, contre `java.version` 19 ;
  - la CI `maven-release.yml` (déclenchée à la main) utilise JDK 16, les actions v2 et `::set-output`.
- Plan de l'étape 1 validé, **avec une nouvelle CI `build.yml`** (`mvn verify` sur les PR et sur `develop`).
- Branche `etape-1-socle` créée depuis `develop` (`69b2710`).
- Commits de l'étape 1 :
  - `245993d` : plan ;
  - `4d047c3`, 1.1 : `.mvn/maven.config` et `.mvn/settings.xml` (Maven Central sans miroir), jcenter retiré ;
  - `14bada8`, 1.2 : Spring Boot 3.5.16, Java 21, Lombok géré par Spring Boot (1.18.46), `source/target 17` retiré ; le migrateur de propriétés n'a rien signalé ;
  - `8454093`, 1.4 : nouvelle CI `build.yml`, et `maven-release.yml` passé en JDK 21, checkout v7, setup-java v6, `$GITHUB_OUTPUT`. Context7 a donné des versions d'actions plus récentes que le « v4 » du plan. Vérifié : le `-s` passé en ligne de commande l'emporte sur `.mvn/maven.config`.
- `mvn clean verify` passe en JDK 21 **sans aucune bidouille**.
- **Robot après l'étape 1** (`doc/reference-apres/etape-1/`, 45 captures) :
  - séquence de statuts **strictement identique** à la référence, 0 erreur ;
  - touches F1 à F7, Entrée, 2 joueurs, Échap et sortie OK ; la pause toujours KO sur Mac ;
  - 3 rendus sur 4 tirés (Lignes non sorti, simple hasard).
  - Le format des logs passe de « - » à « -- » (Spring Boot 3.5) : sans effet sur le robot.
- Checklist : colonne « Après étape 1 » remplie pour ce que couvre le robot. Défaut corrigé au passage : les lignes 2.2 à 2.10, 3.x et 4.2 avaient perdu leur cellule « Après étape 1 » lors du remplissage du 18/11.
- IntelliJ : seule la version Community (IDEA CE 2025.2) était installée. À la demande de Frédéric, **IntelliJ IDEA Ultimate 2026.3** a été installé par Homebrew (`brew install --cask intellij-idea`), avec le plugin **Claude Code [Beta]** (`com.anthropic.code.plugin`, ajouté par `idea installPlugins`). Ladybug est ouvert dans l'Ultimate. Restent à faire par Frédéric : activer la licence avec son compte JetBrains, régler la touche Échap du terminal, et mettre le SDK du projet sur Corretto 21.
- Retour à **IntelliJ Community** (pas d'activation de licence Ultimate) : plugin Claude Code [Beta] installé dans IDEA CE 2025.2 (`idea installPlugins`), Ladybug rouvert dans la Community. L'Ultimate reste installé, à fermer par Frédéric ; désinstallation possible avec `brew uninstall --cask intellij-idea`.
- **1.3, manette optionnelle** (choix (a) de Frédéric, plutôt que de considérer 1.3 couvert par `@Conditional(WindowsHost.class)`) : `XBoxOneControllerManager.initialize()` attrape `LinkageError | RuntimeException`, écrit un avertissement et passe `available` à faux ; `XboxOneController.onApplicationEvent` ne fait rien si la manette est indisponible.
  - Test sur Mac en simulant Windows (`-Dos.name="Windows 11"`, Jamepad tente alors de charger `jamepad.dll`) : **avant**, `UnsatisfiedLinkError` puis « cancelling refresh attempt », le contexte Spring tombe ; **après**, deux avertissements « Manette Xbox désactivée » et le jeu atteint `PROGRAM_PRESENTATION`.
  - Deux avertissements, car `XboxOneController` hérite de `XBoxOneControllerManager` : Spring crée deux beans et initialise Jamepad deux fois. Défaut préexistant, à traiter plus tard (composition plutôt qu'héritage).
- **Graphify après l'étape 1** (`GRAPHIFY_BACKEND=none graphify update .`) : 1 483 → 1 509 nœuds, 3 744 → 3 767 liens. Les ajouts sont la CI `build.yml` et des méthodes du robot ; les autres écarts viennent de la ré-extraction des fichiers Xbox modifiés (identifiants renommés), sans changement de structure.

### 18/11/2026 (nuit) : écran du jeu et labyrinthe coupé
- Partie manuelle lancée pour la checklist, fermée par Frédéric dès l'écran de présentation (code 0, aucune erreur) : remarque de Frédéric, le jeu s'ouvre toujours sur l'écran du Mac (écran principal), pas sur son HP 27w.
- Cause : `MainFrame` ouvre une fenêtre maximisée sans bordure sur l'écran principal, et `Screen` prend `Toolkit.getScreenSize()` (écran principal, barre de menus comprise).
- Constat de Claude sur `01-presentation.png` : **le bas du labyrinthe est coupé** sur Mac. 956 px d'écran, 923 px de fenêtre, 118 px réservés en haut et en bas, il reste 687 px pour 720. La ligne 1.1 de la checklist (« pas de zone coupée ») était donc OK à tort, dès la référence « avant ».
- Demande de Frédéric : diviser par deux la hauteur de la zone du haut. Claude propose de le faire dans un commit à part sur `etape-1-socle` (`topDimension` = `getBorderHeight() / 2`) : **en attente du feu vert**.
- **Décision (C21)** : nouveau point **2.8** du plan, « écran du jeu selon la souris » : la fenêtre s'ouvre sur l'écran de la souris, avec les dimensions de la place réellement disponible (`getScreenInsets`). Plan, copie `~/.claude/plans`, PDF et dashboard mis à jour.

### 18/11/2026 (nuit) : partie « après étape 1 » et sondes de log
- Jeu relancé à la demande de Frédéric (jar à jour, 23 h 32 – 23 h 36, code 0, aucune erreur ni aucun avertissement). Logs : 4 méga-gommes, 7 morts, 4 vies gagnées, niveau 1 → niveau 2, « Ladybug lost the game ». Séquence de statuts identique à la référence.
- Question de Frédéric : « si tu rajoutes des logs, je n'aurai plus à faire les tests ? ». Réponse de Claude : en partie. Les logs prouvent les événements (gomme, fantôme mangé, fruit, téléportation, fantôme joueur qui mange Ladybug), pas le son ni l'affichage (le labyrinthe coupé, passé « OK » en 1.1, l'a montré). Le comportement des fantômes se vérifie mieux par des tests unitaires de leurs stratégies. Autre limite : le robot joue sans viser, donc certains événements n'arrivent pas.
- **Décision (C22)** : les sondes de log se font **à l'étape 3**, avec 3.1 et 3.2 (nouveau point **3.4**), plus des tests des stratégies des fantômes en 3.2. Ensuite, le passage humain par étape se réduit à environ 5 minutes (son et affichage).
- Frédéric : « tout est ok » pour 2.2 à 2.10, 3.1 à 3.3 et 4.2. **La checklist « Après étape 1 » est complète** : tout est OK, sauf 5.6 (pause), KO sur Mac comme avant.
- Choix de Frédéric : le correctif du labyrinthe coupé (`topDimension` divisé par deux) est **reporté au point 2.8**, avec l'écran choisi selon la souris (plan et PDF mis à jour).
- Feu vert de Frédéric (« vas-y, commite les deux ») : commit 1.3 (manette optionnelle), puis commit de la photo « après étape 1 » (checklist, captures, plan, PDF, journal).
- Clôture de l'étape 1 (feu vert « vas-y, enchaîne les trois ») : dashboard mis à jour (s01 « Terminée », santé et packages au commit `8d9803f`, D01 et D04 résolus, C22, j32, plan p06 et p07). Deux dettes ajoutées : **D18** (labyrinthe coupé, étape 2 / 2.8) et **D19** (double initialisation de Jamepad par héritage, étape 8).
- Reste de l'étape 1 repéré : `MainLadybug` affiche encore « for Java 19+ » et teste `>= 19`. D03 reste ouvert et passe à l'étape 2 (point 2.2 : contrôle de version aligné sur le pom). Code non modifié.
- Cette entrée partira avec le premier commit de l'étape suivante (même règle qu'à la fin de l'étape 0).
- **PR #15 créée puis fusionnée** (https://github.com/FredericLanic/ladybug/pull/15). Consigne de Frédéric : « si la CI passe, fusionne la PR ». Premier passage de la CI `build.yml` au vert (28 s). Fusion par `gh pr merge 15 --merge` : merge commit `552cbb4` sur `develop`. La branche `develop` locale est à jour. **L'étape 1 est terminée.**
- Prochaine étape, selon l'ordre C18 : **3.1 et 3.2** (tests unitaires), avec 3.4 (sondes de log), sur une branche `etape-3-tests` à créer depuis `develop`.
- Branche `etape-1-socle` (entièrement fusionnée) **supprimée** en local et sur GitHub, à la demande de Frédéric. Fin de session : « on s'arrête là ce soir ». Reprise prévue avec le plan détaillé de 3.1, 3.2 et 3.4.

### 19/11/2026 (nuit) : IntelliJ IDEA Ultimate activé
- Frédéric a trouvé une licence **IntelliJ IDEA Ultimate** : abonnement Toolbox attribué à « OXIANE / Fréderic Lanic », valable jusqu'au 31/10/2027 (fallback perpétuel : 2026.2). Activée dans l'Ultimate 2026.3 déjà installé (compte JetBrains, *Manage Subscriptions → Activate*). Le numéro de licence n'est volontairement pas noté ici.
- Intérêt pour Ladybug : le support Spring de l'Ultimate comprend `application-context.xml` (navigation XML → classes, beans mal câblés signalés), utile aux étapes 2 et 6.
- Conseils de Claude, à faire par Frédéric : accepter la mise à jour du JDK « corretto-21 (3) » vers 21.0.13.1 et celle des 8 plugins ; SDK du projet et niveau de langage sur 21 (*Project Structure*) ; touche Échap du terminal pour Claude Code. Doublons « corretto-21 » à nettoyer plus tard dans *SDKs*. Désinstaller IDEA CE : à décider plus tard.

### 19/11/2026 (nuit, suite) : analyse détaillée de 3.1, 3.2 et 3.4 (lecture seule)
- Plan détaillé proposé à Frédéric : branche `etape-3-tests`, 4 commits (3.1 outillage, 3.2a calculs purs, 3.2b labyrinthe et fantômes, 3.4 sondes de log). Trois questions en attente : remplacer le test de « case visée », INFO ou DEBUG pour la gomme, feu vert. Frédéric : « continue ton analyse, je vais me coucher ». Aucune modification de code, aucune branche créée.
- **Constat 1 : pas de « case visée » par fantôme.** Blinky, Pinky, Inky et Clyde poursuivent tous Ladybug elle-même par `Dijkstra` (`Ghost.moveTo`) et ne diffèrent que par la vitesse. Proposition : tester le comportement commun (chasse, fuite quand il a peur, retour au point de régénération puis `AUTOGENERATED`).
- **Constat 2 : bug probable, points des fantômes comptés deux fois.** `Score.setScore` et `Score.setMessages` font tous les deux `addScore(nbrEatenGhosts * SCORE_EATEN_GHOST)`. Un fantôme mangé rapporte donc 30 points alors que le message affiche 15. Le bug vient du commit `5150b97` (« grosse refacto du jeu »). Un test de `Score` le mettra en évidence ; la correction est à décider avec Frédéric (règle : on ne corrige pas en étape 3 sans en parler).
- **Constat 3 : `Dijkstra`.** La condition marquée « je pense qu'il y a une erreur » est en réalité correcte (relaxation en profondeur : le résultat est juste, mais le calcul est coûteux). Vraies limites : NPE dans `UnitDijkstra.getShorterWay` si la cible est inaccessible ; les téléporteurs sont ignorés ; récursion profonde. Les tests documenteront ces points.
- **Constat 4 : code mort.** `tools/CheckLevelMap` n'est utilisé nulle part : c'est un doublon de `engine/screendata/utils/CheckScreenBlockBorders`. À supprimer à l'étape 8 (dette à ajouter).
- **Constat 5 : ordre des niveaux.** Les 8 `SecondGenerationLevelN` sont des `@Component` sans `@Order` : l'ordre de la liste injectée dans `ManageLevel` dépend du scan du classpath. Ça fonctionne, mais c'est fragile (étape 7). Les `FirstGenerationLevel` sont désactivés (`// @Component`).
- **Constat 6 : parenthèses dans `setSoundRequests`.** Dans `addTeleport`, `isInGame()` ne s'applique qu'aux fantômes et pas à Ladybug. Sans gravité.
- **Faisabilité des tests** : `new ScreenData(new ManageLevel(List.of(new SecondGenerationLevel1())))` suffit (ni Spring ni Swing). `Ladybug` est `final`, mais Mockito 5 (fourni par Spring Boot 3.5) sait simuler les classes finales. Sur Java 21, Mockito s'attache lui-même en tant qu'agent : simple avertissement, à régler via `argLine` dans surefire si on veut un log propre. Context7 confirme le contenu du starter (JUnit 5, AssertJ, Mockito, Hamcrest, Awaitility).
- **Emplacement des sondes 3.4** : `GameModelGameIsPlaying.programBeat()` enchaîne déjà tous les événements. Proposition : une méthode `logGameEvents()` appelée juste avant `setSoundRequests()` (le score est alors à jour), plus un log dans `addNewFruit()` et dans `caseOfGhostEatLadybug()` (en précisant si le fantôme est joué par un humain). Méga-gomme et vie gagnée ont déjà leur log.

### 19/11/2026 : réponses de Frédéric et commit 3.1
- Réponses : « oui pour 1, INFO, test puis on corrige, vas-y ». Donc : (1) le test de « case visée » est remplacé par le test du comportement commun des fantômes ; (2) la gomme mangée est loguée en `INFO` ; (3) points des fantômes comptés deux fois : d'abord un test qui montre le bug, puis la correction dans l'étape 3 ; (4) feu vert pour la branche et 3.1.
- Branche `etape-3-tests` créée depuis `develop` à jour (`552cbb4`).
- 3.1 : `spring-boot-starter-test` (scope `test`) ; Mockito chargé comme agent Java dans surefire (configuration recommandée par la documentation Mockito, vérifiée dans Context7), plus `-Xshare:off` pour faire taire l'avertissement « Sharing is only supported… » de la JVM. Vérifié avec un test temporaire (mock de `Ladybug`, classe `final`) : OK, sans avertissement. Test temporaire retiré ; `mvn clean verify` passe.
- 3.2a (feu vert « oui, vas-y pour 3.2a ») : tests de `SpeedFunction` (10), `BlinkySpeedIndex` (7), `GhostSensitiveBehaviour` (3, statistiques sur 20 000 tirages) et `Score` (7). **Bug confirmé** : `ScoreTest.eatenGhost` attend 15 et obtient 30. Le test est `@Disabled` avec la raison, pour garder un build vert ; il sera réactivé par le commit de correction. Comportement documenté sans être corrigé : au niveau 30, `SpeedFunction` renvoie l'index 7, hors de `VALID_SPEEDS` (0 à 6) ; `Body.getSpeed()` le ramène à 6. `BlinkySpeedIndex` donne une valeur négative entre 0 et 80 %, ramenée à 0 par le cast en `int`.
- Première fiche pédagogique : `doc/patterns/test-unitaire.md` (Arrange/Act/Assert, mocks, tests paramétrés, aléa) et sommaire `doc/patterns/README.md`.
- Correction (feu vert « oui, vas-y pour la correction puis 3.2b ») : ligne en double supprimée dans `Score.setMessages` ; `ScoreTest.eatenGhost` réactivé et vert. En jeu, un fantôme mangé rapporte désormais 15 points (au lieu de 30), comme le message affiché.
- 3.2b : 55 nouveaux tests, 82 en tout, tous verts. `LevelConsistencyTest` (40 : 5 contrôles × 8 niveaux : dimensions, départs praticables, fantômes capables de rejoindre Ladybug, toutes les gommes atteignables, téléporteurs symétriques et au bord). `DijkstraTest` (11) : comparé, sur les 8 niveaux, à un parcours en largeur indépendant (`LevelFixtures.distancesFrom`), soit plus de 1 100 trajets ; **Dijkstra est juste**. Deux limites documentées par des tests : les téléporteurs sont ignorés, et une cible inaccessible provoque une `NullPointerException`. `GhostMovementTest` (4) : en chasse, chaque pas rapproche de Ladybug ; le fantôme rejoint une Ladybug immobile ; effrayé, il fuit ; mangé, il rejoint le point de régénération puis passe en `AUTOGENERATED`.
- Données réelles des niveaux : 15 × 15 cases, de 132 à 157 gommes, téléporteurs dans les niveaux 1 à 4. Test de mutation fait (direction de poursuite inversée dans `Ghost.moveTo`) : 2 des 4 tests des fantômes échouent, puis le code a été restauré.
- Piège repéré pour plus tard : `Body.position` vaut par défaut la constante partagée `GameMainConstants.POINT_ZERO`, et les déplacements la modifient sur place (`translate`). Un corps déplacé avant d'avoir reçu sa position modifierait cette constante pour tout le jeu (dette à ajouter, étape 4 ou 8).

### 19/11/2026 : sondes de log (3.4), robot et doc « Stratégie de tests autonomes »
- Reprise sur « continue » : le code de 3.4 (`logGameEvents()` dans `GameModelGameIsPlaying`, plus des logs dans `addNewFruit()` et `caseOfGhostEatLadybug()`) était écrit et non commité. `mvn clean verify` vert (82 tests).
- Robot relancé à la demande de Frédéric (`doc/reference-apres/etape-3/`, 47 captures, code 0, aucune erreur ni avertissement). Sondes vues : 21 « eats a point » (case et score justes) et 2 « Ladybug is eaten by … (computer) ». Non déclenchées, car le robot ne vise pas : fruit, téléportation, fantôme mangé ou régénéré.
- Séquence de statuts identique à la référence, sauf `GAME_START` et `LEVEL_START` inversés (positions 70-71), à la même milliseconde, sur deux fils : `AWT-EventQueue` (touche du robot) et `Timer-82` (minuteur `java.util.Timer`). Concurrence préexistante, sans lien avec les sondes : **D22** (étape 4, probablement résolue dès 3.5.1).
- Frédéric a écrit depuis son téléphone, sur claude.ai, le doc **« Ladybug — Stratégie de tests autonomes »** (https://claude.ai/artifact/4mXUqpsGiwLczZzegYUyUP) : cahier des charges, constat du code, cinq couches de tests (golden master, scénarios pas à pas, bot joueur, son, unitaires), cinq étapes (jeu testable, golden master, touches/manette/son, CI trois systèmes, PIT ≥ 60 %), risques et trois points ouverts. Un commentaire du Claude mobile demandait : minuteries en tics ou horloge pilotable ?
- Réponses de Frédéric (« 1 non, le reste oui ») :
  - pas de partie à la main pour les 4 sondes non vues ; 3.4 commité tel quel, avec le rapport du robot (`f14d73c`) ;
  - **C23** : le chantier **s'ajoute** à 3.1, 3.2 et 3.4, comme nouveau point **3.5** ;
  - **C24** : **3.5.1 (horloge unique) passe avant l'étape 2** ; l'ordre devient 1 → 3.1/3.2/3.4 → 3.5.1 → 2 → 3.3. La place de 3.5.2 à 3.5.5 reste à fixer ;
  - **C25** : les minuteries **comptent en tics** (durée ÷ `PACE`), pas d'horloge pilotable. Réponse postée dans le fil du doc ;
  - **C26** : une partie de référence atteint le niveau 2, une fois que le bot vise ;
  - D22 ajoutée ; plan, PDF et dashboard mis à jour.
- Dettes notées le 19/11 (nuit) et enfin numérotées : **D20** `tools/CheckLevelMap`, code mort qui double `CheckScreenBlockBorders` (étape 8) ; **D21** `Body.position` partage par défaut la constante `POINT_ZERO`, modifiée sur place par `translate` (étape 4).
- Le plan 3.2 est corrigé : « case visée par fantôme » remplacé par le comportement commun (décision du 19/11).
- Partie à la main de Frédéric (10 h 29 – 10 h 30, code 0, aucune erreur) : **toutes les sondes vues en vrai**. 85 gommes, 4 fantômes mangés puis 4 régénérés en (7,2), 3 téléportations (Ladybug et Clyde), fruit 1 apparu puis mangé (score 181), Ladybug mangée par Clyde (computer). Reste non vu : Ladybug mangée par un fantôme humain (partie en solo). Deux fantômes mangés au même battement affichent le même score (celui de fin de battement) : comportement accepté.
- Feu vert de Frédéric : plan, journal, skill et PDF commités tout de suite, sans attendre 3.5.1.

### 19/11/2026 : dashboards locaux
- Demande de Frédéric : un dashboard local pour Ladybug, puis un par sujet professionnel, avec une page d'accueil où chaque projet a sa carte (image ou résumé). Les sujets pro restent **entièrement en local**, et la mémoire de chaque projet doit être **étanche** vis-à-vis des autres. « Je ne veux rien perdre des actions que j'ai réalisées. »
- Réponse de Claude sur l'étanchéité : Claude Code range déjà mémoire et conversations par dossier de travail ; règle : un dossier par projet, Claude lancé depuis ce dossier. À éviter pour les données pro : `~/.claude/CLAUDE.md`, `~/.claude/skills`, `~/.claude/plans` (partagés). Les conversations passent toujours par Anthropic : à vérifier avec la politique de Clever Age.
- Pas de serveur : pages HTML statiques, hors ligne, données et image intégrées, ouvertes d'un double-clic.
- Réalisé (feu vert « ok vas-y, capture en partie ») :
  - skill utilisateur générique **`dashboard-local`** (`~/.claude/skills/dashboard-local/` : `scripts/generer.py`, `references/format.md`), sans aucune donnée de projet. Le générateur n'écrit que les `index.html` et la carte, ne supprime rien ;
  - dashboard local Ladybug : `~/Documents/Claude/ladybug/dashboard/index.html` (projet.json, carte.png = capture en partie de l'étape 3) ;
  - page d'accueil `~/Documents/Claude/accueil/index.html`, cartes dans `accueil/cartes/` (titre, résumé, image, lien ; résumé calculé : étape en cours, dettes, décisions, dernière entrée) ;
  - `sauvegarde.sh` accepte un dossier d'export, le fusionne sans suppression, puis régénère dashboard, carte et accueil ;
  - j34 complété dans le dashboard en ligne avec la partie à la main.
- Piège noté : l'export `read_db` avec `out_dir` refuse d'écraser des fichiers déjà présents ; l'ancien export a été conservé dans `dashboard/donnees-avant-2026-11-19`.

### 19/11/2026 : PR #16, premier lot de l'étape 3
- Recommandation de Claude, acceptée (« oui ») : PR de 3.1 à 3.4 tout de suite, 3.5.1 sur une branche à part (premier vrai changement du cœur du jeu), même consigne qu'à l'étape 1 (« si la CI passe, fusionne »).
- Graphify (`GRAPHIFY_BACKEND=none graphify update .`) : 1 509 → 1 597 nœuds, 3 767 → 4 069 liens ; 88 nœuds ajoutés (tests, `LevelFixtures`, 2 pour les sondes), aucun supprimé. Sauvegarde du graphe précédent : `graphify-out/2026-11-19/`.
- PR #16 (https://github.com/FredericLanic/ladybug/pull/16) : CI verte (82 tests, 0 échec), fusionnée en merge commit `f4d8ebd` sur `develop`. Branche `etape-3-tests` conservée (suppression à demander à Frédéric).
- Suite : branche `etape-3-horloge` depuis `develop`, analyse en lecture seule des minuteries pour 3.5.1.

### 19/11/2026 : analyse de 3.5.1 (lecture seule)
- Branche `etape-3-tests` supprimée (local et GitHub), branche `etape-3-horloge` créée depuis `develop` (`f4d8ebd`), feu vert « oui pour les deux ».
- Minuteries du **modèle** (`java.util.Timer`, hors EDT) : `WaitAndDoActionAfterTimer` dans 4 états (`ProgramStarting` 2 500 ms, `GameIsStarting` 2 500 ms, `LevelIsStarting` = jingle, `LevelIsEnded` = intermède, `GameIsEnding` 1 500 ms) qui rappellent `CurrentProgramStatus.doActionAfterTimer(code)` ; `SuperPowerTimer` (bean, 15 s = 10 s de peur + 5 s de clignotement, deux `Timer` chaînés) ; `IncrementScoreTimer` (un `Timer` par `Message`, 1 000 ms avant `setDying`).
- Minuteries de l'**affichage** : `SimpleTimer` (`PageEndView`, message toutes les 800 ms, `Thread.sleep` dans un `TimerTask`), `WaitAndDoActionAfterTimer` dans `CentralView` (couleur du labyrinthe après vie gagnée, relancé à chaque dessin tant que `isNewLife`), `TimerView` (`javax.swing.Timer`, sur l'EDT, animations). Sans effet sur les règles.
- Modèle à suivre déjà présent : `LadybugDying` compte en battements (`bip * PACE >= agonyDuration`).
- Hasard : `Utils.secureRandom`, `LitLampMode` (`Math.random`), `LevelRendering` (`new Random`), `AbstratGameModel` et `GameModelGameIsPlaying` (`new SecureRandom`).
- Constat : la boucle (`javax.swing.Timer`) et le clavier tournent sur l'EDT ; seules les minuteries `java.util.Timer` modifient l'état depuis un autre fil, d'où D22. Les passer en battements supprime cette concurrence.
- Écarts possibles à signaler : arrondi ms → battements (< 40 ms) ; durées qui suivent la boucle si elle ralentit ; comportement en pause (aujourd'hui, les minuteries continuent pendant la pause).
- Plan proposé à Frédéric (en attente) : pattern « Game Loop / Update Method », 5 commits (hasard à graine, planificateur en battements, super-pouvoir, messages, boucle appelable), minuteries de l'affichage laissées à l'étape 4.
