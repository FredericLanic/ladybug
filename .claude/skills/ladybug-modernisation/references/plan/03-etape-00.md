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

> **Ordre de réalisation (décidé le 18/11/2026, complété le 19/11/2026)** : étape 1 → **3.1, 3.2 et 3.4** (tests unitaires, sondes de log) → **3.5.1** (horloge unique en tics, C24) → étape 2 → **3.3** (test du contexte Spring, fait avec 2.7). **Tout cela est fait au 19/11/2026**, ainsi que 3.5.2 (golden master) et 3.5.3 (touches, manette, son). **L'étape 3 est terminée le 20/11/2026** (3.5.4 le 19/11, 3.5.5 le 20/11). Prochaine étape : 4. La numérotation est conservée. Raisons : les tests ont besoin d'un build sain et des versions de test fixées par Spring Boot 3.5 (étape 1) ; les tests unitaires de logique pure protègent ensuite le premier vrai changement de code (étape 2) ; le test du contexte Spring, lui, n'est faisable proprement qu'après l'étape 2, car le `component-scan` de `com.kycox` crée aujourd'hui les 14 vues Swing (`MainFrame`…), ce qui échouerait en mode headless.
