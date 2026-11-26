# Historique et outillage

À lire seulement si la question porte sur ces sujets.

## Ordre des étapes


0. Outillage et état de référence :
   - 0.0 : création de la branche `modernisation` **depuis `origin/develop`**, premier commit (skill et PDF) ;
   - 0.A : Graphify (graphe « avant » lancé sur la branche, puis `--update` après les étapes 4 à 8) ;
   - 0.B : Context7 (documentation à jour des bibliothèques) ;
   - 0.C : photo « avant » (compilation de `develop` tel quel, lancement sur Mac, `doc/checklist-jeu.md`, captures dans `doc/reference-avant/`), puis mise à jour du plan, du PDF, du skill et du dashboard pour `develop`
1. Socle (build, versions, manette optionnelle, CI)
2. Lanceur et XML externe
3. Tests. Le point 3.5, « tests autonomes » (golden master, scénarios, bot qui vise, son, PIT ≥ 60 %), vient du doc de Frédéric https://claude.ai/artifact/4mXUqpsGiwLczZzegYUyUP (C23 à C26)

**Ordre de réalisation (décidé le 18/11/2026, complété le 19/11/2026)** : 1 → 3.1, 3.2 et 3.4 (tests unitaires, sondes de log ; faits) → 3.5.1 (horloge unique en battements) → 2 (avec Spring Boot) → 3.3 (fait avec 2.7) → 3.5.2 à 3.5.5 ; **l'étape 3 est terminée le 20/11/2026**. Prochaine : étape 4. `develop` est la branche par défaut du dépôt depuis le 20/11 (PIT nocturne : `mutation.yml`) (test du contexte Spring, possible seulement une fois le lanceur en place, car le `component-scan` crée aujourd'hui les vues Swing). La numérotation est conservée.

4. Découplage du modèle
5. `PlayerInput`
6. Fantômes configurables et perception
7. Niveaux en fichiers et rendu du labyrinthe
7 bis. Labyrinthe dynamique : socle commun, puis A (portes), B (murs mobiles), D (labyrinthe généré). C (murs déclenchés par les joueurs) est traité en 6.7 et 9.6
8. Langage moderne, ménage et documentation (test « Jeff »)
9. Réseau
10. Optionnel

Le détail de chaque étape, les constats vérifiés et les critères de vérification sont dans `references/plan/` (un fichier par étape).

## Base de travail : `develop`, pas `master`


Le plan a d'abord été écrit en lisant `master`. Or `origin/develop` a **34 commits d'avance** (vérifié le 18/11/2026) :
- Observer y est remplacé par des événements dont la source est `GameModel` ;
- SLF4J, internationalisation, Java 19, sans `spring-boot-starter-web` ;
- rendu des niveaux par images et par Strategy ;
- packages réorganisés (`engine/` regroupe 70 classes) ;
- Jamepad n'est chargé que sous Windows.

Il garde en revanche les sous-classes de fantômes et le XML, n'a toujours aucun test, reste en Spring Boot 3.0.2 avec Lombok 1.18.22, et est en version `0.6.0-SNAPSHOT` (contre `0.8.0-SNAPSHOT` sur `master`).

**Une branche par étape**, créée depuis `develop` à jour et nommée `etape-N-sujet` (par exemple `etape-1-socle` ; jamais `modernisation/…`, en conflit avec la branche `modernisation`). Chaque étape est fusionnée par une PR vers `develop`, en merge commit, après le robot, la checklist et Graphify (résultats résumés dans la PR). L'étape 0 a été faite sur la branche `modernisation`, créée depuis `origin/develop`, poussée le 18/11/2026 ; sa PR vers `develop` est la première. Les constats et la dette ont été revus sur `develop` le 18/11/2026 (étape 0.C.5). **L'étape 0 est terminée** (commits `feb4a99`, `1ac73d4`, `bdd0842`) ; la référence « avant » est dans `doc/checklist-jeu.md` et `doc/reference-avant/2026-11-18/`, et le robot `doc/reference-avant/robot/LadybugRobot.java` se relance après chaque grande étape (Terminal.app doit avoir les autorisations Accessibilité et Enregistrement de l'écran).

**`develop` est protégée** depuis le 20/11/2026 (ruleset « Protection de develop ») : aucun push direct, tout passe par une PR, fusionnable seulement quand Build est vert sur Linux, Windows et macOS. Les branches fusionnées sont supprimées automatiquement ; seul le merge commit est autorisé. Les anciennes branches `SCS` et `macos` sont archivées en tags `archive/…`.

Réseau et accès GitHub : la clé `id_ed25519` est enregistrée sur GitHub depuis le 18/11/2026, donc on pousse en SSH. Sur certains réseaux (bureau), le port 22 est bloqué : il faut alors passer par HTTPS avec `gh`, qui n'est pas encore installé (`brew install gh`, puis `gh auth login` fait par Frédéric). Les clés n'ont pas de passphrase : ne lisez jamais les clés privées.

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

## Pédagogie : la raison d'être du chantier


À chaque refactoring :
- nommez le design pattern utilisé ;
- expliquez pourquoi vous l'avez choisi ;
- montrez un **avant / après** avec le vrai code de Ladybug.

Chaque pattern introduit a sa fiche dans `doc/patterns/<pattern>.md`, lisible à trois niveaux (junior, confirmé, vétéran) et terminée par un exercice. Le format a été validé par Frédéric ; le modèle et un exemple complet (Strategy) sont dans `references/pattern-sheet.md`. Ajoutez la fiche au sommaire `doc/patterns/README.md`, et faites pointer la Javadoc des classes concernées vers la fiche.

Les **outils de qualité** (ArchUnit, JaCoCo, SonarQube Cloud, CI, robot, Dependabot, Graphify) ont leurs fiches au même format dans `doc/qualite/` (demandé par Frédéric le 21/11/2026), avec le sommaire `doc/qualite/README.md` et le tableau « qui vérifie quoi ». Tout nouvel outil y reçoit sa fiche, et les chiffres cités (tests, couverture, statuts du robot) se tiennent à jour.

Points forts existants à reconnaître, sans les dénigrer :
- un pattern State dans `model/strategy` ;
- des interfaces de contrat par couche (`contract/`) ;
- Dijkstra pour les déplacements ;
- l'injection par constructeur.

