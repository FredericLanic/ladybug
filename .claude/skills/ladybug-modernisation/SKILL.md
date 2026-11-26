---
name: ladybug-modernisation
description: État, décisions et plan de la modernisation du jeu Ladybug (Java/Swing/Spring, clone de Pacman). À utiliser quand on travaille sur ce projet : refactoring, patterns, fantômes, niveaux, labyrinthe, tests, réseau, documentation.
---

# Ladybug — modernisation (version courte)

Les règles de travail sont dans `CLAUDE.md`. Ce fichier ne donne que l'état et les pointeurs : **ne lisez une référence que si la tâche en a besoin**, et seulement la partie utile.

## Le projet
≈140 classes, `com.kycox.game`. Écrit par Frédéric pour se perfectionner en Java ; niveau qu'il estime « moyen ». Objectif : un code qu'un développeur extérieur (Jeff) comprend vite, avec des patterns que Frédéric comprend lui-même.

## État (à tenir à jour en une ligne)
- Étapes 0 à 5 bis terminées (dernière PR : #38, 22/11/2026). **Prochaine : étape 6** (fantômes configurables et perception) → `references/plan/10-etape-06.md`.
- SonarQube Cloud prêt, inactif tant que le secret `SONAR_TOKEN` n'existe pas.

## Pédagogie (à chaque refactoring)
Nommer le pattern, dire pourquoi, montrer un avant / après avec le vrai code. Chaque nouveau pattern a sa fiche `doc/patterns/<pattern>.md` (modèle : `references/pattern-sheet.md`), chaque outil de qualité sa fiche dans `doc/qualite/`.

## Pièges connus
- Builds en Java 21 (voir `CLAUDE.md`).
- Les vues ne lisent que le `GameSnapshot` : pour afficher une info, l'ajouter dans `GameSnapshotFactory`.
- Les vues doivent savoir se dessiner avant le premier battement (fond noir).
- Registre ArchUnit `src/test/resources/archunit-store` : ne jamais le regénérer pour faire passer une nouvelle boucle.
- Le robot peut s'interrompre au scénario Échap si Ladybug perd ses 3 vies : relancer.
- Garder la configuration XML Spring (ne pas proposer `@Configuration`).

## Où trouver le reste
| Besoin | Fichier |
|---|---|
| Détail d'une étape | `references/plan/NN-etape-XX.md` (sommaire : `references/plan/00-vue-d-ensemble.md`) |
| Questions ouvertes | `references/plan/16-questions-ouvertes.md` (ne pas les trancher seul) |
| Décisions prises | `references/decisions.md` |
| Pourquoi une décision | `grep -rn "mot" references/journal/` (sommaire : `references/journal/README.md`) |
| Branches, Graphify, Context7, ordre des étapes | `references/historique.md` |
| Dashboard, pages, PDF, sauvegarde | skill `/ladybug-cloture`, **seulement à la demande de Frédéric** |
| Backlog de l'auteur | `doc/backlog.txt` |
