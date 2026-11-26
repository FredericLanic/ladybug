## Règles de travail
- **Aucune action (branche, build, modification) sans feu vert explicite.** On discute d'abord.
- **Une branche par étape**, créée depuis `develop` à jour et nommée `etape-N-sujet` (par exemple `etape-1-socle`). On évite `modernisation/…`, que git refuse tant que la branche `modernisation` existe. L'étape 0 a été faite sur `modernisation`, créée depuis **`origin/develop`** (et non `master`). **Un commit par sous-étape**, le diff montré avant chaque commit, et la convention de message `[Tech] …`.
- **Fusion par pull request vers `develop`**, une PR par étape, en *merge commit* (pas de squash, pour garder les commits de sous-étape). Avant la fusion, on passe le robot, la checklist et Graphify, et on résume le résultat dans la description de la PR. Décidé le 18/11/2026.
- Les constats ci-dessus ont été faits sur `master`, puis revus sur `develop` le 18/11/2026 (étape 0.C.5).
- Le jeu reste jouable après chaque étape.
- `~/.m2/settings.xml` n'est pas modifié. Décidé le 18/11/2026 : build via **Maven Central**, avec un réglage propre au projet ; le Nexus local n'est pas relancé.

