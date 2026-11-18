---
name: ladybug-modernisation
description: Contexte, décisions et plan de modernisation du jeu Ladybug (Java/Swing/Spring, clone de Pacman). À utiliser dès qu'on travaille sur ce projet — refactoring, factorisation, design patterns, configuration XML des fantômes, niveaux, rendu du labyrinthe, mode lampe, tests, mise à jour du pom, lanceur MainLadybug, mode réseau (Ladybug contre fantômes sur plusieurs postes), documentation ou fiches de patterns — même si l'utilisateur ne parle pas explicitement de « plan » ou de « modernisation ».
---

# Ladybug — modernisation

Ce skill résume les échanges de cadrage entre Frédéric (l'auteur) et Claude, en novembre 2026. Il sert à reprendre le chantier dans n'importe quelle session sans perdre les décisions déjà prises.

Le plan détaillé est dans `references/plan.md`. Lisez-le avant de proposer ou d'entamer une étape.

## Le projet et son auteur

- Ladybug est un jeu de type Pacman en Java, avec Swing et Spring. Il compte ≈140 classes (environ 9 100 lignes) dans le package `com.kycox.game`.
- Frédéric l'a écrit pour **se perfectionner en Java** (MVC, Spring, design patterns, threads), quand il travaillait chez OXiane BZH. C'est pour cela que le skin, le T-shirt et les fantômes portent le thème « OXiane ».
- Il estime son niveau Java **moyen**, à l'image du code. Il dira quand il ne comprend pas.
- Ce qui l'a motivé : un développeur Java vétéran (Jeff) n'a pas osé se plonger dans le code, faute de temps. L'objectif est un code qu'un développeur extérieur comprend vite, et des patterns que **Frédéric comprend lui-même**.

## Règles de travail (importantes)

1. **Discuter avant d'agir.** Ne lancez aucune action (branche, build, modification de code, commit) sans un feu vert explicite du type « vas-y » ou « on commence ». La validation d'un plan ne vaut pas feu vert. Frédéric a déjà interrompu une exécution lancée trop tôt. L'exploration en lecture seule reste permise.
2. **Avancer à petits pas.** Une branche par étape (`etape-N-sujet`, créée depuis `develop` à jour), un commit par sous-étape, le diff montré avant chaque commit, puis une PR vers `develop` fusionnée en merge commit (pas de squash). Le jeu doit rester jouable après chaque étape.
3. **Ne pas toucher à `~/.m2/settings.xml`.** Il pointe vers un Nexus local (`localhost:8081`) éteint. Le choix entre relancer le Nexus et passer par Maven Central est encore ouvert : demandez-le.
4. **Langue** : le **code en anglais** (classes, méthodes, variables), les **commentaires et la Javadoc en français**. Appliquez la règle progressivement, dans les fichiers que vous touchez.
5. **Répondre en français**, avec un vocabulaire accessible. Si vous employez un terme comme « couplage » ou « responsabilité unique », traduisez-le en une phrase concrète appliquée à Ladybug.

## Pédagogie : la raison d'être du chantier

À chaque refactoring :
- nommez le design pattern utilisé ;
- expliquez pourquoi vous l'avez choisi ;
- montrez un **avant / après** avec le vrai code de Ladybug.

Chaque pattern introduit a sa fiche dans `doc/patterns/<pattern>.md`, lisible à trois niveaux (junior, confirmé, vétéran) et terminée par un exercice. Le format a été validé par Frédéric ; le modèle et un exemple complet (Strategy) sont dans `references/pattern-sheet.md`. Ajoutez la fiche au sommaire `doc/patterns/README.md`, et faites pointer la Javadoc des classes concernées vers la fiche.

Points forts existants à reconnaître, sans les dénigrer :
- un pattern State dans `model/strategy` ;
- des interfaces de contrat par couche (`contract/`) ;
- Dijkstra pour les déplacements ;
- l'injection par constructeur.

## Décisions prises

- **Garder la configuration XML Spring** (`application-context.xml`). Elle permet d'ajouter, retirer ou régler des fantômes sans recompiler, et c'est un choix voulu par Frédéric. Ne proposez pas de la remplacer par `@Configuration` Java.
- **Rendre le XML externe prioritaire.** Un `application-context.xml` posé à côté du jar doit l'emporter sur celui du jar. Ce n'est **pas** le cas aujourd'hui : c'est vérifié, `JarLauncher` + `ClassPathXmlApplicationContext` ne lisent que l'intérieur du jar.
- **Revoir le `main`.** `MainLadybug` doit devenir un lanceur : lecture de la configuration, choix du mode (solo, 2 joueurs local, puis héberger et rejoindre), construction du contexte, puis la fenêtre affichée via `invokeLater`.
- **Fantômes = assemblage de capacités** (composition) plutôt que des sous-classes : stratégie de déplacement, politique de vitesse, `Perception`. `Clyde`, `Inky` et `Pinky` ne diffèrent aujourd'hui que par leur vitesse initiale.
- **La visibilité dépend du personnage.** Elle concerne le mode lampe, c'est-à-dire le cercle de lumière autour d'un joueur (touche F6, ou tirage aléatoire par niveau). Ce qu'un joueur voit dans le noir dépend « du type de fantôme et de ses pouvoirs ». Le même paramètre de perception sert à l'IA et à l'humain. La vue gagne une notion d'**observateur**.
- **Rendu du labyrinthe : garder le style à l'identique** (contours bleus fins, coins arrondis, pointillés de gommes) et refaire l'implémentation : autotiling par table, image pré-rendue, `MazeTheme`.
- **Labyrinthe dynamique** (idée de Hammed, 18/11/2026) : les **quatre variantes** sont retenues (A. portes, B. murs mobiles, C. murs déclenchés par les joueurs, D. labyrinthe généré). Un socle commun (`MutableMaze`, `MazeChangeEvent`, `MazeChange`, `ReachabilityChecker`, `OccupancyRule`) est construit une fois, et les variantes ne diffèrent que par **qui décide** du changement. Prérequis : l'étape 7. La variante C devient un pouvoir configurable (6.7), puis passe par l'hôte en réseau (9.6).
- **Le mode réseau vient en dernier** (étape 9). Il suit une architecture d'**hôte qui fait autorité** : les clients n'envoient que leurs entrées, l'hôte renvoie un `GameSnapshot`. Les étapes précédentes le préparent : listener typé, snapshot, `PlayerInput` par personnage.

## Ordre des étapes

0. Outillage et état de référence :
   - 0.0 : création de la branche `modernisation` **depuis `origin/develop`**, premier commit (skill et PDF) ;
   - 0.A : Graphify (graphe « avant » lancé sur la branche, puis `--update` après les étapes 4 à 8) ;
   - 0.B : Context7 (documentation à jour des bibliothèques) ;
   - 0.C : photo « avant » (compilation de `develop` tel quel, lancement sur Mac, `doc/checklist-jeu.md`, captures dans `doc/reference-avant/`), puis mise à jour du plan, du PDF, du skill et du dashboard pour `develop`
1. Socle (build, versions, manette optionnelle, CI)
2. Lanceur et XML externe
3. Tests
4. Découplage du modèle
5. `PlayerInput`
6. Fantômes configurables et perception
7. Niveaux en fichiers et rendu du labyrinthe
7 bis. Labyrinthe dynamique : socle commun, puis A (portes), B (murs mobiles), D (labyrinthe généré). C (murs déclenchés par les joueurs) est traité en 6.7 et 9.6
8. Langage moderne, ménage et documentation (test « Jeff »)
9. Réseau
10. Optionnel

Le détail de chaque étape, les constats vérifiés et les critères de vérification sont dans `references/plan.md`.

## Graphify (cartographie du code)

Graphify ([Graphify-Labs/graphify](https://github.com/Graphify-Labs/graphify)) est prévu à l'étape 0. Le paquet s'appelle `graphifyy`, avec deux « y ». Il s'installe via `uv`, car la machine a Python 3.9 et Graphify demande 3.11 ou plus.

Installé le 18/11/2026 (étape 0.A).

**Règle absolue : toujours préfixer par `GRAPHIFY_BACKEND=none`** (par exemple `GRAPHIFY_BACKEND=none graphify update .`). Sinon, faute de clé d'API, Graphify appelle le CLI Claude Code pour nommer les communautés et envoie des noms de classes à Anthropic. Validé par le test T3 du 18/11 : sans IA, le graphe est identique, seuls les noms des communautés sont moins parlants.

- **Construire le graphe** : `GRAPHIFY_BACKEND=none graphify update .` (ou `/graphify . --code-only` avec la même variable).
- **Avant une modification**, mesurer l'impact : `/graphify path "A" "B"`, `/graphify explain "X"`, `/graphify query "…"`.
- **Après une grande étape** : `GRAPHIFY_BACKEND=none graphify update .`, puis montrer la différence avec le graphe précédent (copier d'abord `graphify-out/` comme référence).

Limite : le XML Spring n'est pas analysé. Le câblage des fantômes ne figure donc pas dans le graphe.

## Context7 (documentation à jour des bibliothèques)

Context7 ([upstash/context7](https://github.com/upstash/context7)) a été suggéré par Halim. Il est installé depuis le 18/11/2026 (étape 0.B) : mode MCP, portée projet (`.mcp.json`), connexion OAuth faite par Frédéric avec `/mcp`. Test T4 réussi sur `/spring-projects/spring-boot/v3.5.16`.

Une fois installé, **consultez-le avant toute montée de version ou toute utilisation d'une API de bibliothèque** : Spring Boot, Lombok, JUnit, AssertJ, Mockito, SLF4J, Jamepad, Java 21… Précisez la version visée. Il fournit deux outils : `resolve-library-id`, puis `query-docs`. La raison : la mémoire de Claude peut dater, et une migration ratée fait perdre du temps et la confiance de Frédéric.

Confidentialité : les questions partent chez Upstash. N'y mettez ni code du projet ni information personnelle. Ne posez que des questions générales sur la bibliothèque.

## Dashboard « Ladybug Modernisation »

Un dashboard privé sur claude.ai, demandé par Frédéric sur le modèle de celui de Halim : https://claude.ai/artifact/SpChAsmu1H63WxzCEYsp3d (modèle « Data »).

Il affiche, dans l'ordre :
- l'avancement des étapes ;
- **le plan en détail** (une section dépliable par titre « ## » de `references/plan.md`) ;
- la santé du code (tuiles et graphique par package) ;
- la dette technique ;
- les décisions et questions ouvertes ;
- **les idées** (qui les a proposées, statut, où elles atterrissent dans le plan) ;
- l'outillage ;
- le journal.

Les noms (Jeff, Halim, OXiane) y figurent, à la demande de Frédéric.

**C'est Claude qui le tient à jour**, à la fin de chaque session qui change quelque chose. Les données sont des documents de la base du dashboard. Écrivez-les avec `write_db` (en `update` avec `if_version` sur les documents existants) :
- `data/ladybug/steps/sNN` : `status` vaut « À faire », « En discussion », « En cours » ou « Terminée », avec `updated` ;
- `data/ladybug/packages/<package>` et `data/ladybug/health/<key>` : relancez `python3 .claude/skills/ladybug-modernisation/scripts/metrics.py .` et recopiez les valeurs. Mettez aussi à jour la date et le commit dans la `description` des datasets `packages` et `health` ;
- `data/ladybug/debt/Dnn` : `status` passe à « Résolu » ;
- `data/ladybug/decisions/Cnn|Qnn` : une question tranchée devient une décision ;
- `data/ladybug/tooling/<id>` et `data/ladybug/journal/jNN` (nouvelle entrée, `order` croissant) ;
- `data/ladybug/ideas/Inn` : **chaque nouvelle idée** (de Frédéric, d'un collègue ou de Claude), avec `title`, `author`, `status` (« À discuter », « Retenue », « Réalisée », « Écartée »), `step` et `text` ;
- `data/ladybug/plan/pNN` : **à chaque modification du plan**, lancez `python3 .claude/skills/ladybug-modernisation/scripts/plan_dashboard.py .claude/skills/ladybug-modernisation/references/plan.md <dossier>` puis envoyez les fichiers produits en `batch` (`set`). Si le nombre de sections diminue, supprimez les documents en trop.

Une étape « bis » garde un `num` décimal pour le tri (7 bis → 7.5) et un `label` affiché (« 7 bis »).

Ne touchez au code de la page (`files/index.html`) que si Frédéric demande une modification de la mise en page.

## Questions ouvertes

Ces questions sont à reprendre plus tard. Ne les tranchez pas seul, et ne les laissez pas bloquer l'avancement :
- Pouvoirs des fantômes : fixes (XML) ou évolutifs en cours de partie ?
- Attribution des fantômes en réseau : plusieurs joueurs sur un même type, ou un fantôme chacun ?
- Réseau local seulement, ou Internet aussi ?
- Quelles options relèvent de l'état partagé, et lesquelles des préférences locales ?
- L'écart de rayon de lumière (3,5 cases contre 4) dans `CentralView` est-il voulu ?
- Nexus local ou Maven Central ?
- Q11, labyrinthe dynamique : prévenir le joueur avant un changement ? Labyrinthe généré symétrique ou libre ? Mur qui se ferme sur un personnage : attendre ou pousser ?
- Graphify : inclure plus tard les documents (README, backlog) ? Activer le mode « toujours actif » ?

Concevez de façon à laisser les deux options possibles : les capacités sont des objets remplaçables, et un joueur est associé à un fantôme précis, pas à un type.

## Base de travail : `develop`, pas `master`

Le plan a d'abord été écrit en lisant `master`. Or `origin/develop` a **34 commits d'avance** (vérifié le 18/11/2026) :
- Observer y est remplacé par des événements dont la source est `GameModel` ;
- SLF4J, internationalisation, Java 19, sans `spring-boot-starter-web` ;
- rendu des niveaux par images et par Strategy ;
- packages réorganisés (`engine/` regroupe 70 classes) ;
- Jamepad n'est chargé que sous Windows.

Il garde en revanche les sous-classes de fantômes et le XML, n'a toujours aucun test, reste en Spring Boot 3.0.2 avec Lombok 1.18.22, et est en version `0.6.0-SNAPSHOT` (contre `0.8.0-SNAPSHOT` sur `master`).

**Une branche par étape**, créée depuis `develop` à jour et nommée `etape-N-sujet` (par exemple `etape-1-socle` ; jamais `modernisation/…`, en conflit avec la branche `modernisation`). Chaque étape est fusionnée par une PR vers `develop`, en merge commit, après le robot, la checklist et Graphify (résultats résumés dans la PR). L'étape 0 a été faite sur la branche `modernisation`, créée depuis `origin/develop`, poussée le 18/11/2026 ; sa PR vers `develop` est la première. Les constats et la dette ont été revus sur `develop` le 18/11/2026 (étape 0.C.5). **L'étape 0 est terminée** (commits `feb4a99`, `1ac73d4`, `bdd0842`) ; la référence « avant » est dans `doc/checklist-jeu.md` et `doc/reference-avant/2026-11-18/`, et le robot `doc/reference-avant/robot/LadybugRobot.java` se relance après chaque grande étape (Terminal.app doit avoir les autorisations Accessibilité et Enregistrement de l'écran).

Réseau et accès GitHub : la clé `id_ed25519` est enregistrée sur GitHub depuis le 18/11/2026, donc on pousse en SSH. Sur certains réseaux (bureau), le port 22 est bloqué : il faut alors passer par HTTPS avec `gh`, qui n'est pas encore installé (`brew install gh`, puis `gh auth login` fait par Frédéric). Les clés n'ont pas de passphrase : ne lisez jamais les clés privées.

## Pièges connus

- Jamepad (manette Xbox) plante avec `UnsatisfiedLinkError` sur Mac Apple Silicon et fait tomber tout le contexte Spring. Il faut le rendre optionnel (étape 1).
- Les versions sont incohérentes : pom en Java 16, `MainLadybug` exige 19+, bannière en v0.6.0, CI en JDK 17.
- Le flag « fantôme joué par un humain » est stocké dans l'enum d'images `GhostsBodyImages` (état global, backlog CTEC1).
- `GameModel` dépend de Swing (`javax.swing.Timer`) et de `java.util.Observable`, qui est déprécié.
- Le backlog de l'auteur se trouve dans `doc/backlog.txt` (CTEC pour la technique, CFCT pour les fonctionnalités), et ses idées pour le mode 2 joueurs dans `doc/idéePourJeux2Joueurs.txt`.

## Mettre ce skill à jour

Frédéric a demandé que **tout ce qu'on se dit soit retenu**. Après chaque discussion importante :
1. Ajoutez une entrée datée dans `references/journal.md` : ce qui a été dit, le raisonnement, ce qui a été décidé ou reporté.
2. Si une décision change ou qu'une question ouverte est tranchée, mettez à jour ce fichier et `references/plan.md`. Gardez `~/.claude/plans/linked-cooking-valley.md` synchronisé.
3. Régénérez le PDF : modifiez `references/plan-pdf.html`, puis lancez Chrome headless avec `--print-to-pdf=doc/plan-modernisation-ladybug.pdf`.
4. Mettez à jour le dashboard (voir la section « Dashboard ») : statuts, mesures, idées, plan en détail et nouvelle entrée de journal. Frédéric veut que **toutes les idées et tout le plan** y soient lisibles.
5. **Sauvegardez tout dans `~/Documents/Claude/ladybug`**, à la demande de Frédéric :
   - lancez `sh .claude/skills/ladybug-modernisation/scripts/sauvegarde.sh` depuis la racine du dépôt (copie du plan, du PDF, du skill, de la mémoire et des conversations en `.jsonl` et `.md`) ;
   - exportez les données du dashboard avec `read_db` (`list`, `out_dir` = `~/Documents/Claude/ladybug/dashboard/donnees`) pour `datasets` et chaque collection `data/ladybug/*` ;
   - si la page a changé, copiez aussi `files/index.html` dans `dashboard/page-index.html`.

Lisez `references/journal.md` quand vous avez besoin du contexte d'une décision : pourquoi le XML est conservé, comment on a vérifié que le XML externe n'était pas lu, d'où vient l'idée de la perception, etc.
