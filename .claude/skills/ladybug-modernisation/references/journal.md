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

### Divers
- Le message « Update installed · Restart to update » vient de l'application Claude : une mise à jour à appliquer en redémarrant, sans effet sur le projet.
- Le message « API Error: Connection lost mid-response » était une coupure réseau : la réponse en cours a été tronquée et le travail a repris ensuite.
