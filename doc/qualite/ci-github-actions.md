# CI GitHub Actions : chaque PR vérifiée sur trois systèmes

> Intégration continue (CI) par GitHub Actions, gratuite pour un dépôt public. Mise en place dans Ladybug aux étapes 1 et 3 (novembre 2026), complétée à l'étape 5 bis. Fichiers : [`.github/workflows/`](../../.github/workflows/).

**🟢 L'idée en une image (junior)**

Dans une boulangerie, chaque fournée passe par le même contrôle avant d'aller en vitrine : on goûte, on pèse, on vérifie la cuisson. Personne ne dit « ça ira, je l'ai fait cent fois ».

La CI, c'est ce contrôle pour le code : à chaque PR, des machines de GitHub récupèrent le code, le compilent et lancent tous les tests, sur Linux, Windows et macOS. Tant que ce n'est pas vert, la PR ne peut pas entrer dans `develop`.

**🔵 Dans Ladybug (confirmé)**

Trois workflows, trois rythmes :

| Workflow | Quand | Ce qu'il fait | Durée |
|---|---|---|---|
| [`build.yml`](../../.github/workflows/build.yml) | chaque PR et chaque push sur `develop` | compile, 251 tests (golden master, bot, [ArchUnit](archunit.md)), rapport [JaCoCo](jacoco.md), analyse [SonarQube Cloud](sonarqube-cloud.md) | 1 à 2 min par système |
| [`mutation.yml`](../../.github/workflows/mutation.yml) | chaque nuit sur `develop`, et à la demande | [test de mutation](../patterns/test-de-mutation.md) (PIT), échec sous 60 % | ≈ 36 min |
| [`maven-release.yml`](../../.github/workflows/maven-release.yml) | à la main, sur `master` | publie une version : tag `vX.Y.Z` et GitHub Release avec le jar | quelques minutes |

Le cœur de `build.yml` est une **matrice** : le même travail, lancé trois fois, une par système.

```yaml
jobs:
  build:
    strategy:
      fail-fast: false          # un système en échec n'arrête pas les autres
      matrix:
        os: [ ubuntu-latest, windows-latest, macos-latest ]
    runs-on: ${{ matrix.os }}
    steps:
    - uses: actions/checkout@v7
    - uses: actions/setup-java@v6
      with: { distribution: temurin, java-version: '21', cache: maven }
    - run: mvn -B clean verify
```

*La protection de `develop`* (ruleset « Protection de develop », depuis le 20/11/2026) :
- aucun push direct : tout passe par une PR ;
- fusion possible seulement si Build est vert sur les trois systèmes ;
- fusion en merge commit (l'historique de chaque étape reste lisible) ;
- branche supprimée automatiquement après la fusion.

Où le trouver : l'onglet « Actions » du dépôt, ou en ligne de commande `gh pr checks <numéro>`.

**🟣 Le recul (senior / vétéran)**

- *Pourquoi trois systèmes* : Ladybug est un jeu de bureau, joué sur Mac et sur Windows. Les écarts existent : polices, fins de ligne, chemins de fichiers, bibliothèque native de la manette (Jamepad). Un test qui ne passe que sur la machine du développeur ne prouve pas grand-chose.
- *Ce qui ne va pas dans une PR* : PIT prend ≈ 36 minutes sur GitHub. Le mettre à chaque PR aurait rendu chaque correction pénible. Il tourne la nuit, et un échec se voit le matin. Compromis : une PR peut faire baisser le score de mutation sans le savoir tout de suite.
- *Le piège du parallélisme* : avec 4 processus PIT sur une machine GitHub de 4 cœurs, les tests se gênaient, dépassaient leur délai, et PIT les comptait à tort comme « mutants détectés » : score gonflé. Réglage mesuré : 2 processus, délai x2.
- *Ce que la CI ne voit pas* : le dessin à l'écran, le son, le plaisir de jeu. D'où le [robot](robot.md), et les parties de contrôle de Frédéric.
- *Les secrets* : un workflow lit `secrets.SONAR_TOKEN` sans jamais pouvoir l'afficher (masqué `***` dans les logs). Une PR venant d'un fork n'y a pas accès : l'étape Sonar est alors sautée.

**✏️ Exercice**

Ouvrez l'onglet « Actions » du dépôt, choisissez un run de Build et comparez les durées des trois systèmes. Ensuite, sur une branche d'essai, faites échouer un test exprès, ouvrez une PR en brouillon et regardez le bouton « Merge » rester bloqué. Fermez la PR sans la fusionner.
