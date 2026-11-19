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

### 19/11/2026 : 3.5.1a commité, 3.5.1b (planificateur en battements)
- 3.5.1a commité (`6b5f963`, « oui, commite et enchaîne sur 3.5.1b »). Incident corrigé avant commit : `Utils.java` est en CRLF et le premier passage d'édition l'avait converti en LF (diff de 134 lignes) ; fins de ligne rétablies. Règle pour la suite : éditer en conservant les fins de ligne de chaque fichier.
- 3.5.1b : `TickCountdown` (compte à rebours en battements, arrondi au battement supérieur : 2 500 ms → 63) et `TickScheduler` (bean ; `schedule(ms, Runnable)`, `tick()` appelé au début de `GameModel.actionsByTimerBip`). Les 5 états (`ProgramStarting`, `GameIsStarting`, `LevelIsStarting`, `LevelIsEnded`, `GameIsEnding`) planifient une référence de méthode (`currentGameStatus::setLevelStart`) au lieu de `WaitAndDoActionAfterTimer` + code numérique ; les constantes `TO_*` et `doActionAfterTimer` de `CurrentProgramStatus` disparaissent. `WaitAndDoActionAfterTimer` reste utilisé par `CentralView` (affichage, étape 4). 13 tests, 99 au total.
- Robot (1er essai interrompu : IntelliJ passé au premier plan pendant le daily de Frédéric ; 2e essai complet, code 0) : **174 changements de statut, séquence strictement identique à la référence « avant » du 18/11**, alors que celle de l'étape 3 avait l'inversion D22. Les 173 changements après le démarrage se font sur le fil Swing (`AWT-EventQueue-0`), aucun sur un `Timer-N`. **D22 est corrigée.**
- **Régression entendue par Frédéric** : « on entend deux fois la musique du début de la partie ». Cause mesurée : la boucle Swing bat en réalité toutes les ~43 ms (et non 40) ; comptées en battements, les attentes duraient 7 % de trop (début de niveau 4,52–4,60 s au lieu de 4,22 s) et la musique (4,217 s), redemandée à chaque battement pendant `LEVEL_STARTING`, repartait. Trois options présentées (boucle qui rattrape, échéances en temps réel, correction du son seul) ; **choix de Frédéric : la boucle à pas fixe** (« la 1, vas-y »).
- `BeatPacer` (5 tests) : le timer Swing se réveille toutes les 20 ms, le pacer dit combien de battements de 40 ms jouer (0, 1, parfois 2) ; au-delà de 5 battements de retard (mise en veille), il repart de l'instant présent. Le jeu tourne désormais à 25 battements/s réels, soit ~7 % plus vite qu'avant sur ce Mac.
- Robot avec le pacer : séquence des 174 statuts identique à la référence ; durées revenues à la normale (début de niveau 4,21–4,25 s, début de partie 2,50–2,54 s, fin de partie 1,50–1,54 s). Écran d'accueil au lancement : 3,7 s au lieu de 2,5 s (gros blocage de démarrage de Swing, rattrapage limité à 5 battements), accepté. Un 2e passage a été interrompu par IntelliJ au premier plan (42 statuts identiques avant l'arrêt).
- Risque résiduel noté : la musique est redemandée à chaque battement pendant `LEVEL_STARTING` ; un battement très en retard pile à la fin de la musique pourrait encore la relancer. À corriger en 3.5.3 (faux lecteur de sons, test).
- Frédéric, après écoute et partie : « ça me paraît très bien » (musique une seule fois, vitesse correcte). Commit 3.5.1b.

### 19/11/2026 : 3.5.1c (super-pouvoir en battements)
- `SuperPowerTimer` réécrit (117 → ~70 lignes) : un seul `TickCountdown` de 375 battements au lieu de deux `java.util.Timer` chaînés (plus de `numTimer`, de second objet imbriqué, de fils cachés). Statut lu dans les battements restants : RUN au-dessus de 125, STOPPING de 125 à 1, STOP à 0. Même interface publique ; `GameModel` appelle `superPowerTimer.tick()` après le planificateur. 4 tests (108 au total).
- Incident corrigé avant de montrer le diff : fins de ligne mélangées (CRLF dans l'en-tête, LF ailleurs) à cause d'un échappement doublé dans le script d'édition ; fichier remis entièrement en CRLF.
- Partie de Frédéric (code 0, aucune erreur) : 2 méga-gommes à 12,5 s d'écart, la seconde pendant le clignotement relance bien les 15 s ; début de niveau 4,23 s. Frédéric : « oui c'est bon ». Les logs ne permettaient pas de vérifier 10 s + 5 s : à sa demande, deux sondes ajoutées dans `SuperPowerTimer.tick()` : « Super power is flashing » et « Super power is over ».

### 19/11/2026 : 3.5.1d (messages en battements)
- Partie de Frédéric avec 3.5.1c + sondes : « Super power is up » 15:19:38,002 → « flashing » 15:19:48,002 (10,000 s) → « over » 15:19:53,001 (5,0 s). 3 fantômes mangés, fruit 2, 4 téléportations, aucune erreur.
- 3.5.1d : `Message` décompte lui-même ses 25 battements (`TickCountdown`), `GroupMessages.tick()` appelé par `GameModel` à chaque battement ; `IncrementScoreTimer` supprimé, ainsi que le setter public de `dying`. 3 tests (111 au total). Le modèle n'utilise plus aucun `java.util.Timer` ; restent `SimpleTimer` et `WaitAndDoActionAfterTimer` côté affichage (étape 4). Frédéric : messages « +… » toujours affichés ~1 s, « oui c'est bon ».

### 19/11/2026 : 3.5.1e, fin de 3.5.1
- `GameModel.actionsByTimerBip()` devient `beat()` (public), Javadoc de l'ordre d'un battement. Propriété `-Dladybug.loop.autostart=false` : le timer Swing ne démarre pas, un test pilote les battements. `GameModelBeatTest` (2 tests, Mockito) : ordre planificateur → super-pouvoir → messages → état du jeu → événement ; N battements à la demande. 113 tests.
- Fiche `doc/patterns/game-loop.md` (Game Loop / Update Method) au format validé, avec le piège de la boucle à 43 ms ; sommaire `doc/patterns/README.md` ; Javadoc de `TickCountdown`, `TickScheduler`, `BeatPacer`, `SuperPowerTimer` vers la fiche.
- Robot de fin de 3.5.1 (`doc/reference-apres/etape-3-horloge/`, 47 captures, code 0, aucune erreur) : 174 statuts identiques à la référence « avant », 173 sur le fil Swing ; début de niveau 4,22–4,25 s, début de partie 2,50–2,54 s, fin de partie 1,50–1,53 s.
- Le test « partie complète rejouée deux fois » reste impossible tant que Spring crée les fenêtres (component-scan) : reporté à 3.5.2 (décision du 19/11).
- Fin de 3.5.1 (feu vert « oui, commite et enchaîne sur les trois points ») : plan (3.5.1 détaillé et marqué fait ; partie rejouée à l'identique ajoutée à 3.5.2 ; durées des sons et son redemandé ajoutés à 3.5.3), copie `~/.claude/plans`, SKILL.md, PDF ; dashboard (p07, s03, santé : 113 tests, 152 classes, 9 734 lignes ; j37).
- Graphify : 1 597 → 1 671 nœuds, 4 069 → 4 258 liens ; 89 ajoutés (`GameRandom`, `TickCountdown`, `TickScheduler`, `BeatPacer`, tests), 15 supprimés (`IncrementScoreTimer`, ancien `SuperPowerTimer`, `RandomEnum` de `LevelRendering`, `doActionAfterTimer`). Piège : la sauvegarde datée de Graphify (`graphify-out/2026-11-19/`) écrase celle du même jour ; le graphe de fin d'étape 1 a été remis dans `graphify-out/2026-11-19-fin-etape1/`.
- PR #17 (https://github.com/FredericLanic/ladybug/pull/17) : CI verte (113 tests, 0 échec), fusionnée en merge commit `4be7cdb` sur `develop`. Branche `etape-3-horloge` conservée (suppression à demander). **3.5.1 terminé.** Prochaine étape selon l'ordre C24 : **étape 2** (lanceur et XML externe).

### 19/11/2026 : analyse de l'étape 2 (lecture seule)
- Branche `etape-3-horloge` supprimée (local et GitHub), feu vert « oui pour les deux ».
- Démarrage actuel : `Application.main` (annotée `@SpringBootApplication` mais sans `SpringApplication.run`) crée le contexte dans un **champ statique** (`new ClassPathXmlApplicationContext("application-context.xml")`), donc pendant le chargement de la classe, sur le fil `main` ; le `component-scan` de `com.kycox` construit alors toutes les vues Swing hors du fil Swing. `MainLadybug.launchTheGame()` affiche « v0.6.0-SNAPSHOT » et « Java 19+ » en dur, teste `>= 19` (D03), puis `mainFrame.setVisible(true)` sur `main`.
- `GameProperties` : `@PropertySource("classpath:ladybug.properties")` seulement. `application.properties` et `banner.txt` ne servent pas (Spring Boot n'est pas lancé).
- Le manifest du jar contient déjà `Implementation-Version: 0.6.0-SNAPSHOT` (pour 2.2).
- Classes liées à Swing ou au son : `engine/view/*`, `engine/controller/KeyboardController`, `engine/sound/*`, `Engine` (lit les durées des sons), `MainLadybug`, les enums d'images et `Sounds`. Piste pour 3.3 : profil Spring « gui » dans le XML (scan du modèle sans ces paquets, puis `<beans profile="gui">`), plutôt qu'un contexte parent/enfant, qui casserait les événements `EventGameModel` (ils remontent de l'enfant au parent, pas l'inverse).
- Context7 interrogé sur `ApplicationHome` (dossier du jar) : réponse trop maigre, à vérifier à la compilation.
- Plan proposé à Frédéric (en attente) : pattern Factory pour la création du contexte, 6 commits, 3 questions (XML externe invalide, modes dans le lanceur, dossier des fichiers externes).

### 19/11/2026 : étape 2 avec Spring Boot, 2.1
- Frédéric, en réponse au plan : « JE VEUX QUE CE SOIT SPRINGBOOT ». Plan révisé : `SpringApplicationBuilder(...).headless(false)` (Spring Boot passe sinon la JVM en `java.awt.headless=true`, cause probable de l'échec de l'essai commenté dans `Application.java`), `@ImportResource` pour garder le XML (C01), configuration externe native de Spring Boot (dossier courant et `config/`), bannière et version, `@SpringBootTest` avec profil « gui » pour 3.3. Pattern pédagogique : configuration externalisée (au lieu de Factory). Vérifié dans Context7 (Spring Boot 3.5.16 : emplacements externes, `spring.config.import` optionnel, `@ImportResource`).
- Réponses de Frédéric (« oui pour les trois ») : **C27** XML externe invalide → message clair (log + boîte de dialogue) puis arrêt ; **C28** le choix 1 ou 2 joueurs reste dans le jeu, le lanceur n'a qu'un `LaunchMode.LOCAL` (HOST et JOIN à l'étape 9) ; **C29** fichiers externes cherchés selon la convention Spring Boot (dossier courant et `config/`), et non plus « à côté du jar ».
- Branche `etape-2-lanceur` créée depuis `develop` (`4be7cdb`).
- 2.1 : `Application` démarre par `SpringApplicationBuilder` + `headless(false)` + `@ImportResource("classpath:application-context.xml")` ; `MainLadybug` devient `ApplicationRunner` et ouvre la fenêtre par `SwingUtilities.invokeLater` ; `logging.pattern.console` garde l'ancien format de log. Essai de 12 s puis robot complet : contexte prêt en 1,6 s, 174 statuts identiques à la référence, sortie par Échap en code 0, aucune erreur. Restent : bannière `${project.version}` (2.2) ; vues encore construites sur le fil `main` (profil « gui », 2.7).
- 2.2 : version lue dans le manifest (`Implementation-Version`) par la bannière Spring Boot et `MainLadybug.gameVersion()` (« development » hors jar) ; la ligne `application.version=${project.version}` (non filtrée par Maven) masquait la version et est retirée. Le contrôle « Java 19+ » est supprimé : code mort, car une JVM plus ancienne refuse de charger les classes compilées pour Java 21 avant d'arriver dans `MainLadybug`. **D03 corrigée.** 1 test (114 au total). Feu vert « oui, commite et enchaîne sur 2.3 et 2.4 ».
- 2.3 à 2.5 : paquet `launcher` (`ConfigLocations` : `./config/` puis `./` puis le jar, 4 tests ; `StartupReport` : mode, XML et propriétés utilisés dans les logs ; `StartupFailure` : log + boîte de dialogue + code 1, C27 ; `LaunchMode.LOCAL`, C28). `@ImportResource("${ladybug.config:classpath:application-context.xml}")`, valeur par défaut posée par `SpringApplicationBuilder.properties` (la ligne de commande l'emporte) ; `spring.config.import` optionnel pour `ladybug.properties` externe. Essais réels depuis un dossier de test : sans fichier, avec `ladybug.properties` externe (chapeau et peau à 0 pris en compte), avec XML externe valide, avec XML invalide (`Clydee` → message explicite et code 1). 118 tests. Constat : `-Djava.awt.headless=true` fait échouer le démarrage tant que les vues sont créées avec le contexte (objet de 2.7).
- 2.7 et 3.3 : méta-annotation `@GuiComponent` (`@Component` + `@Profile("gui")`) sur les 21 classes d'interface (vues, rendus, clavier, manette, `GameSounds`, `Engine`, `MainLadybug`) ; aucune classe du modèle n'en dépend (`NewSounds` reste dans le modèle). Le lanceur active le profil. Tests `SpringContextTest` (vrai contexte sans écran : 4 fantômes dans l'ordre, modèle présent, aucune fenêtre) et `ExternalSpringXmlTest` (XML imposé sans Clyde : Blinky, Inky, Pinky), 0,9 s. 121 tests. Robot : 174 statuts identiques, profil « gui » actif, code 0. Feu vert « oui, commite et enchaîne sur 2.8 ».
- 2.8 : `Screen` (désormais `@GuiComponent`) prend l'écran de la souris (`MouseInfo`) et la place réellement disponible (`getScreenInsets`) ; `MainFrame` place la fenêtre par `setBounds` avant de l'agrandir. Essais de Frédéric sur ses deux écrans (HP 27w 1920×1080 au-dessus du Mac, Mac 1470×923 disponibles) : le jeu s'ouvre sur l'écran de la souris, le labyrinthe est entier. **D18 corrigée.**
- Remarques de Frédéric pendant les essais, toutes traitées dans 2.8 : (1) « le jeu au centre de l'écran et la zone du bas collée à la zone de jeu » → la zone du bas (score + bande d'animation) est collée sous le labyrinthe, l'écart passe tout en bas ; (2) « sur le petit écran, la zone en bas est vraiment haute » → zone de hauteur bornée ; (3) « quand il y a de la place, le dessin de Ladybug avec les vies est vraiment petit » → constat : le dessin ne prenait que la moitié de sa bande (`(h − 12) / 2`, 18 px) et la moitié basse de la zone (`jPanelLadybugKinematique`) est toujours vide ; correction (« oui pour les deux, avec une taille maximale de ladybug ») : la zone prend la moitié de la place libre, entre 2 et 3 cases (Mac 101 px, HP 144 px), le reste est partagé au-dessus et en dessous ; le dessin occupe toute la bande, plafonné à une case (Mac 46 px, HP 48 px). 7 tests (`ScreenTest`, `StatusGameViewTest`), 127 au total.
- Idée notée (à discuter) : la bande « animation de Ladybug », moitié basse de la zone du bas, n'a jamais été remplie.
- 2.6 : README (build : Java 21, Spring Boot 3.5, Maven 3.9, nom du jar ; nouvelle section « Personnaliser le jeu » : emplacements, extraction du XML par `unzip -p`, exemple trois fantômes, `--ladybug.config=file:…`, tableau de `ladybug.properties`, options de lancement), commandes vérifiées. Fiche `doc/patterns/configuration-externalisee.md` (avec profil et méta-annotation), sommaire, Javadoc. Défaut trouvé en vérifiant : le message d'erreur citait le XML du jar quand le XML fautif venait de `--ladybug.config` → `ConfigLocations.effectiveSpringXml()` + test. 128 tests. Feu vert « oui, commite et enchaîne sur les trois points » (robot, plan/PDF/dashboard, Graphify + PR).
- Robot de fin d'étape 2 : un 1er passage interrompu en 5.7 (Ladybug avait perdu ses 3 vies pendant le jeu à l'aveugle du robot, l'Échap est tombé sur la présentation et a fermé le jeu, comme prévu) ; 2e passage complet (`doc/reference-apres/etape-2/`, 47 captures) : 174 statuts identiques à la référence, 173 sur le fil Swing, profil « gui » actif, code 0.
- Plan (étape 2 marquée faite ; ordre : tout le bloc 1 → 3.1/3.2/3.4 → 3.5.1 → 2 → 3.3 est fait ; à décider : 3.5.2 à 3.5.5 avant l'étape 4 ou en parallèle), SKILL.md (décisions Spring Boot et XML externe à jour), PDF, dashboard (s02 « Terminée », D03 et D18 résolues, C27 à C29, idée I13 « bande d'animation vide », santé : 128 tests, 157 classes, 10 106 lignes, paquet `launcher`, j39).
- Graphify : 1 671 → 1 760 nœuds, 4 258 → 4 415 liens ; 92 ajoutés (paquet `launcher`, `Screen`, tests), 3 supprimés. Graphe de fin de 3.5.1 sauvegardé dans `graphify-out/2026-11-19-fin-3.5.1/`.
- PR #18 (https://github.com/FredericLanic/ladybug/pull/18) : CI verte (128 tests ; `SpringContextTest` et `ExternalSpringXmlTest` passent aussi sur Ubuntu, sans écran), fusionnée en merge commit sur `develop`. Branche `etape-2-lanceur` conservée (suppression à demander). **L'étape 2 est terminée.**

### 19/11/2026 : 3.5.2 (golden master), analyse et décisions
- Branche `etape-2-lanceur` supprimée (local et GitHub), branche `etape-3-golden-master` créée depuis `develop` (`2905e8d`). Recommandation de Claude acceptée : 3.5.2 avant l'étape 4.
- Analyse : toutes les touches passent par `GameModelForController` (un test peut les simuler) ; aucune autre source de hasard ou de temps dans le modèle (`HashMap<Point,Point>` des téléporteurs : ordre stable). **Piège** : `LevelRepository` écrit le dernier niveau dans `java.io.tmpdir/labybug/numLevel.txt` (faute de frappe dans le nom) : un test écraserait la sauvegarde réelle → dossier réglable `ladybug.save.dir`.
- Plan en 5 commits : 3.5.2a banc d'essai `GameDriver` ; 3.5.2b journal de partie ; 3.5.2c preuve du déterminisme ; 3.5.2d golden master solo puis 2 joueurs ; 3.5.2e bot qui vise, partie de référence jusqu'au niveau 2 (C26).
- Décisions de Frédéric (« oui pour les trois ») : **C30** images clés reportées après un journal stable ; **C31** journal = événements + une ligne d'état toutes les 25 battements ; **C32** chaque référence est montrée à Frédéric (extrait) et validée avant d'être figée.
- 3.5.2a : `LevelRepository` lit `ladybug.save.dir` (par défaut, inchangé : `tmpdir/labybug/numLevel.txt`). `GameDriver` (src/test/testutil) : vrai jeu sans écran, contexte neuf par partie, graine fixée, boucle pilotée, « touches » via `GameModelForController`, durées des sons lues dans les `.wav` (`AudioSystem.getAudioFileFormat`, sans carte son). 4 tests ; durées réelles retrouvées (63 battements d'accueil, ~169 de début de partie + musique). Sauvegarde réelle de Frédéric non touchée (vérifié). Fragilité trouvée : **D23** les actions des fantômes ne sont créées qu'au premier battement de présentation ; « S » pressé avant → `NullPointerException` (impossible pour un humain ; à corriger à l'étape 4). 132 tests.
- 3.5.2b : `GameJournal` (src/test/testutil) observe le modèle après chaque battement, sans toucher au jeu : lignes `STATUS`, `LEVEL`, `SCORE (+n)`, `LIVES`, `LADYBUG`, `GHOST <nom> <état>`, et `STATE` (positions de tous) toutes les 25 battements ; première ligne `SEED`. Extrait montré à Frédéric (graine 42). Limite connue : état de fin de battement, les statuts éphémères (`GAME_START`, question « reprendre ») n'apparaissent pas. 133 tests. Feu vert « oui, commite et enchaîne sur 3.5.2c ».
- 3.5.2c : `GameScript` (touches datées, `soloLevelOne()`), `DeterminismTest` : même graine + mêmes touches → journaux identiques sur 60 s de jeu ; autre graine → autre partie. Essai de sabotage (hasard sans graine remis dans `Utils`) : le test échoue, puis fichier restauré. Précaution notée pour 3.5.2d : `GhostsBodyImages.isComputed` (CTEC1) est un état partagé qui survit d'une partie à l'autre. 135 tests. Feu vert « oui, commite et enchaîne sur 3.5.2d ».
- 3.5.2d : `GoldenMaster` (référence absente → candidat dans `target/golden/` et échec ; écart → première ligne différente avec contexte, journal complet dans `target/golden/<nom>.actual.txt` ; `-Dgolden.update=true` réécrit les références), `GameScript.twoPlayersLevelOne()` et touches ZQSD, marque « (human) » dans le journal, remise à zéro de `GhostsBodyImages.isComputed` à chaque partie. `GoldenMasterGameTest` : parties scénarisées solo et 2 joueurs (graine 42, 3 000 battements). Candidats montrés à Frédéric (solo : 29 gommes, 3 morts, fin de partie à b1788 ; 2 joueurs : Pinky humain, fin à b1038 ; ni méga-gomme ni fruit ni niveau 2, rôle du bot de 3.5.2e) ; **validés** (« oui ») et figés dans `src/test/resources/golden/`. 139 tests.
- 3.5.2e : `AimingBot` (sans hasard : fuir un fantôme dangereux à ≤ 3 cases de chemin, chasser un fantôme effrayé à ≤ 8, sinon la gomme la plus proche ; parcours en largeur), `GameRules` (score jamais en baisse en jeu, jamais dans un mur, jamais 60 s sans progrès ; message avec graine et battement), `BotGameTest`. Partie du bot (graine 42) : 2 méga-gommes, 7 fantômes mangés, 7 fruits apparus / 2 mangés, 6 téléportations, 1 vie gagnée, 1 perdue, niveau 1 vidé à b3917, **niveau 2 atteint** à b4049 (C26) ; bond de +51 en fin de niveau, figé tel quel. Journal montré à Frédéric et **validé** (« oui ») → `src/test/resources/golden/bot-seed42.txt`. 4 300 battements en 3,3 s. 140 tests. Feu vert « oui, commite et enchaîne sur les points de fin ».
- Fin de 3.5.2 : robot (`doc/reference-apres/etape-3-golden-master/`, 47 captures, code 0) : 174 statuts identiques à la référence. Fiche `doc/patterns/golden-master.md` + sommaire. Plan (3.5.2 marqué fait), copie `~/.claude/plans`, PDF ; dashboard (s03, D23, C30 à C32, I12 « Réalisée », 140 tests, p04, p07, j41).
- PR #19 (https://github.com/FredericLanic/ladybug/pull/19) : **1re CI en échec**, 9 erreurs, toutes la même cause : `Sounds` chargeait tous les sons dans son constructeur ; sur Ubuntu sans carte son, `AudioSystem.getClip()` lève `IllegalArgumentException` (non rattrapée) → `ExceptionInInitializerError`, et le modèle (qui n'utilise `Sounds` que comme des noms via `NewSounds`) plante. Défaut réel : un joueur sans sortie audio aurait aussi vu le jeu planter. Correction : chargement de chaque son à sa première écoute (`getClip()`), échec rattrapé avec un avertissement (jeu muet) ; `GameSounds` ignore les sons absents (durée 0). Le déterminisme entre systèmes reste à confirmer par la CI suivante.
- 2e CI verte sur Ubuntu : **les trois références du golden master, produites sur le Mac, sont identiques sur Linux** (déterminisme confirmé d'un système à l'autre ; bot 6,2 s sur la CI).
- Remarque de Frédéric : « l'idée de charger tous les sons au début était de ne pas avoir de freeze durant le jeu ». Réponse : préchargement rétabli dans `GameSounds.preloadSounds()` (`@PostConstruct`, profil « gui » seulement), log « Sounds preloaded: 16/16 » ; `Sounds` reste paresseux et tolérant pour le modèle, les tests et les postes sans carte son.
- Pause demandée par Frédéric (réunion), puis « termines les actions » : PR #19 fusionnée en merge commit sur `develop` (CI verte, 140 tests). **3.5.2 terminé.** Branche `etape-3-golden-master` conservée (suppression à demander). Reste à décider : 3.5.3 à 3.5.5 ou étape 4. À confirmer par Frédéric : absence de saccade avec le préchargement des sons.

### 19/11/2026 : préparation de 3.5.3 (touches, manette, son)
- Frédéric, après une partie : préchargement des sons « parfait », pas de saccade. Choix de la suite : « on finit l'étape 3.5.x ».
- Analyse de 3.5.3 (lecture seule) : `NewSounds` (liste des sons demandés) suffit à observer le son, sans faux lecteur ; la liste n'est vidée que par certains états (pendant `LEVEL_STARTING`, la musique reste demandée à chaque battement : cause du risque résiduel) → la vider après chaque battement ; `KeyboardController` testable avec de vraies `KeyEvent` sans écran (sauf Échap en présentation : `System.exit`) ; manette : `XboxOneController` hérite de `XBoxOneControllerManager` (D19), logique non testable ; durées des sons dupliquées entre `Engine` et `GameDriver`. Plan proposé : 3.5.3a sons dans le journal (références à revalider, C32), 3.5.3b `SoundDurations` dans le modèle, 3.5.3c touches, 3.5.3d manette par composition (`PadState`, `PadMapper`).
- **C33** : touche **P** = pause, sur tous les systèmes (la touche Pause n'existe pas sur clavier Mac ; dernière ligne « KO » de la checklist).
- Manette Xbox sur Mac : Jamepad 1.3.2 (2019) ne fournit sa bibliothèque macOS qu'en x86_64 (vérifié : `libjamepad64.dylib` Mach-O x86_64, Mac et JVM en arm64) et Ladybug n'active la manette que sous Windows (`@Conditional(WindowsHost)`). Pour 3.5.3d : rechercher une bibliothèque maintenue avec natives arm64 (fork libGDX de Jamepad, libsdl4j… à vérifier), retirer la condition Windows, essai avec la manette.
- Manette du fils de Frédéric connectée en Bluetooth au Mac (macOS 27.2.1) : Xbox Wireless Controller `045E:0B20`, micrologiciel 5.24, **modèle 1708 (Xbox One S)** confirmé par Frédéric.
- 3.5.3a (feu vert « oui, vas-y pour 3.5.3a », branche `etape-3-touches-son` depuis `develop` `f321b92`) : `GameModel.beat()` vide la liste des sons demandés au début de chaque battement (un son n'est demandé qu'au battement de son événement ; la musique du début ne reste plus demandée pendant `LEVEL_STARTING`). `GameJournal` note `SOUND <nom>` quand un son commence. Vérifié : hors lignes `SOUND`, les trois journaux sont strictement identiques aux références validées (aucune règle changée). Sons de la partie du bot : 15 sur 16 ; `GHOST_SURVIVOR` déclaré mais jamais utilisé → **D24**. Références mises à jour après validation de Frédéric (« oui ») : +38, +31, +189 lignes `SOUND`. `BotGameTest` vérifie la couverture des 15 sons. Incident : 1re mise à jour lancée avec une mauvaise liste de tests (`+` au lieu de `,`), donc non faite, et ma vérification comparait l'ancienne référence à elle-même ; refaite correctement. 140 tests.
- 3.5.3b : `SoundDurations` (modèle) lit la durée des `.wav` dans l'en-tête, sans carte son ; `GameModel.init()` règle agonie, début de niveau, intermède et fin de partie (constante 1 500 ms) ; `Engine` ne garde que la durée de la couleur « vie gagnée » ; méthodes de durée de `GameSounds` et copie de `GameDriver` supprimées. `SoundDurationsTest` (2). Golden master inchangé (mêmes durées). Partie de Frédéric : 74 gommes, méga-gomme, 3 fantômes, 3 tunnels ; début de niveau 4,219 s ; « oui, commite et enchaîne sur 3.5.3c ». 141 tests.
- 3.5.3c : touche **P** = pause (C33), en plus de Pause ; `KeyboardControllerTest` (12 tests, vraies `KeyEvent` envoyées au vrai `KeyboardController` sur le jeu du banc d'essai : 1/2, S puis N, flèches, P/Pause, Échap puis N ou O, F1 appui/relâchement, F2, F3 à F5, F6, F7, Entrée ; Échap en présentation non testable : `System.exit`). Le test « Échap puis O » a révélé que `forceStopGame()` ne faisait rien si le timer Swing ne tournait pas : condition retirée (« oui pour forceStopGame », sans effet en jeu). Aide F1 : question de Frédéric « est-ce que tout tiendra dans l'écran ? » → `drawEightCenterTextLines` (8 lignes placées à la main) remplacée par `drawCenterTextLines(String...)` (lignes réparties sur 13 cases) ; aide du clavier à 12 lignes (lampe, camouflage, P, Échap ajoutés, choix « P, F6, F7 et Échap ») ; textes FR et EN. README : P, F7 et S ajoutés au tableau des touches. Essais de Frédéric : aide lisible, pause et Échap + O OK (log « Force Stop Game »). 153 tests.
- 3.5.3d, recherche de bibliothèque (Context7 n'a pas Jamepad ; vérifié sur Maven Central et dans les jars) : **`com.badlogicgames.jamepad:jamepad:2.30.0.0`** (fork libGDX, mars 2024, Maven Central, SDL 2.30, même paquet `com.studiohartman.jamepad`), contient `libjamepadarm64.dylib` **arm64** (vérifié avec `file`) ; dépendance `gdx-jnigen-loader` 2.2.0. Autre candidat écarté : `io.github.libsdl4j:libsdl4j` 2.28.4-1.6 (API SDL brute, plus de réécriture).
- Essai isolé (programme `PadProbe` hors du projet) avec la manette Xbox One S du fils de Frédéric en Bluetooth : bibliothèque chargée sans `UnsatisfiedLinkError`, « Xbox One S Controller » reconnue ; 2e essai de 15 s : stick gauche (−1 à +1 sur les deux axes) et boutons A, B, X, Y lus correctement, **même en arrière-plan** (pas de limite « premier plan » de macOS). Avertissement bénin : `gamecontrollerdb.txt` absent, correspondances intégrées de SDL utilisées.
- 3.5.3d : `com.badlogicgames.jamepad:jamepad:2.30.0.0` remplace Jamepad 1.3.2 (dépôt jitpack retiré du pom) ; `PadState` (record : état d'une manette, fabricable dans les tests, Jamepad 2.x ayant des champs `final`) ; `XBoxOneControllerManager` (unique, `update()` à chaque battement, `state(i)`, `vibrate(...)`, fichier de correspondances vide `jamepad/gamecontrollerdb.txt` pour éviter l'avertissement) ; `XboxOneController` l'**utilise** au lieu d'en hériter (**D19 corrigée**) et expose `handleRequests` testable ; `@Conditional(WindowsHost)` retiré partout et `WindowsHost` supprimé. `XboxOneControllerTest` (6 tests, fausse manette).
- Essais de Frédéric avec la manette Xbox One S : `Gamepad support enabled (1 controller(s) connected)`, A / X / Y / stick / fin de partie OK ; **remarque « haut bas sont inversés »** : l'axe vertical du stick a changé de sens entre Jamepad 1.x et 2.x (SDL : positif = bas) → `PadState.from()` retourne les axes Y (convention Ladybug : positif = haut) + test ; « ça marche pour le mode solo ». Mode duo : manette n° 1 = Ladybug, n° 2 = fantôme ; avec une seule manette, le fantôme se joue au clavier (ZQSD).
- Question de Frédéric : la manette filaire marchera-t-elle sous Windows ? Réponse : normalement oui (Windows reconnaît les manettes Xbox filaires, `jamepad64.dll` présent, Ladybug marchait déjà ainsi), mais non vérifié ; le sens haut/bas est à contrôler. Pas de natif Windows ARM. À la demande de Frédéric : **`doc/checklist-windows.md`** (démarrage, affichage, son, clavier, manette filaire, fichiers, sortie), essai prévu « plus tard ».
- Robot de fin de 3.5.3 : 1er passage interrompu en 5.7 (Ladybug avait perdu ses 3 vies ; manette allumée) ; 2e passage manette éteinte : complet, 174 statuts identiques, code 0 (`doc/reference-apres/etape-3-touches-son/`). Zone morte du stick = 0,45, manette au repos ≈ ±0,05 : la manette ne pouvait pas diriger Ladybug ; échec le plus probablement dû au hasard (comme à la fin de l'étape 2). Idée **I14** : lancer le robot avec une graine fixe (`-Dladybug.seed`). Pause du robot : 0,3 % de différence pendant la pause (avant ≈ 0,9 %, bruit) : elle fonctionne désormais.
- Plan (3.5.3 marqué fait ; choix : 3.5.4 et 3.5.5 avant l'étape 4), copie `~/.claude/plans`, PDF.
- Graphify après 3.5.3 : voir PR #20.
- PR #20 (https://github.com/FredericLanic/ladybug/pull/20) : 1re CI en échec avant compilation (« Non-resolvable parent POM … spring-boot-starter-parent:3.5.16 … in central » : incident passager de Maven Central, rien changé à cette ligne) ; relancée : verte (159 tests). Fusionnée en merge commit sur `develop`. Graphify : 1 856 → 1 909 nœuds, 4 680 → 4 855 liens. **3.5.3 terminé.** Restent 3.5.4 (CI sur trois systèmes) et 3.5.5 (PIT). Branche `etape-3-touches-son` conservée (suppression à demander).

### 19/11/2026 : 3.5.4 (CI sur trois systèmes)
- Branches `etape-3-touches-son` et `etape-3-golden-master` supprimées (« oui pour les deux »). Branche `etape-3-ci-multi` créée depuis `develop` (`f0d4fd1`).
- `build.yml` : matrice `ubuntu-latest`, `windows-latest`, `macos-latest`, `fail-fast: false`. Piège anticipé : Git récupère les fichiers texte en CRLF sous Windows → références du golden master protégées par `.gitattributes` (`eol=lf`) et `GoldenMaster` qui ignore `\r\n` (+ 1 test). 160 tests en local.
