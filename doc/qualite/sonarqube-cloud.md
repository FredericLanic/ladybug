# SonarQube Cloud : une relecture automatique de chaque PR

> Service en ligne de SonarSource ([sonarcloud.io](https://sonarcloud.io)), gratuit pour un dépôt public sous licence libre (Ladybug : GPL-3.0). Branché sur Ladybug à l'étape 5 bis (21/11/2026). Tableau de bord public : [FredericLanic_ladybug](https://sonarcloud.io/dashboard?id=FredericLanic_ladybug).

**🟢 L'idée en une image (junior)**

Un correcteur orthographique ne juge pas l'histoire, mais il souligne les fautes, les répétitions et les phrases bancales. SonarQube Cloud fait la même chose avec le code : il connaît des milliers de règles (« cette variable peut être `null` ici », « ce bloc est copié-collé »…) et souligne ce qui les enfreint, à chaque PR.

**🔵 Dans Ladybug (confirmé)**

L'analyse est lancée par la CI, après le build et les tests, sur Linux seulement. Dans [`build.yml`](../../.github/workflows/build.yml) :

```yaml
- name: Analyze with SonarQube Cloud
  if: matrix.os == 'ubuntu-latest' && env.SONAR_TOKEN != ''
  run: mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar
```

- le scanner relit le code, les classes compilées et le rapport [JaCoCo](jacoco.md) ;
- il envoie le tout à sonarcloud.io, qui affiche le résultat et le résume dans la PR ;
- sans le secret `SONAR_TOKEN`, l'étape est sautée : rien ne casse.

Les réglages sont dans le [pom](../../pom.xml) : `sonar.organization`, `sonar.projectKey`, `sonar.host.url`, et la version du scanner dans `pluginManagement`.

*Les notes, de A (très bien) à E* :

| Mesure | Ce qu'elle regarde | Ladybug, 1re analyse |
|---|---|---|
| Fiabilité | les bugs probables | D (6 bugs) |
| Sécurité | les failles possibles | C (1 alerte) |
| Maintenabilité | la dette : le temps estimé pour tout nettoyer | A (≈ 1 h 45) |
| Duplication | le code copié-collé | 1,4 % |
| Couverture | les tests (via JaCoCo) | 60,1 % |

*La porte qualité* (« Quality Gate ») est le feu vert ou rouge d'une PR. Elle ne juge que le **code nouveau ou modifié** : on ne demande pas de tout réparer d'un coup, seulement de ne pas empirer. PR #32 : porte qualité OK.

*Trier les alertes, deux exemples réels* :
- **une vraie** : trois accès possibles à `null` dans `ScreenBlockView`. `maze.cellAt(…)` renvoie `null` hors du labyrinthe, et le code ne le vérifie pas. Aujourd'hui, la vue ne demande jamais de case hors labyrinthe, mais rien ne le garantit (un labyrinthe dynamique, étape 7 bis, pourrait le faire). À corriger ;
- **une fausse** : « générateur aléatoire non sûr » dans `GameRandom`. La règle vaut pour la cryptographie (mots de passe, jetons), pas pour les déplacements de fantômes. Elle se marque « sans risque » dans Sonar, avec la raison.

Où le trouver :
- le tableau de bord : https://sonarcloud.io/dashboard?id=FredericLanic_ladybug ;
- une PR : le lien apparaît dans le journal de l'étape « Analyze with SonarQube Cloud » (`ANALYSIS SUCCESSFUL, you can find the results at…`).

**🟣 Le recul (senior / vétéran)**

- *Analyse automatique ou par la CI* : SonarQube Cloud sait analyser seul un dépôt GitHub, sans rien configurer. C'est ce qui a tourné en premier sur `develop`. Mais il ne voit alors ni les classes compilées ni la couverture. L'analyse par la CI voit tout. Les deux ne peuvent pas coexister : il faut couper l'automatique (Administration → Analysis Method), sinon la CI échoue.
- *Le jeton est un mot de passe* : il se crée sur sonarcloud.io (My Account → Security) et se colle **uniquement** dans GitHub (Settings → Secrets and variables → Actions → **Secrets**, jamais « Variables »), sous le nom `SONAR_TOKEN`. GitHub le chiffre et le masque dans les logs. Un jeton collé ailleurs (un message, un fichier) se révoque et se remplace : c'est arrivé le 21/11, sans conséquence.
- *Gratuit, mais pas garanti à vie* : la gratuité pour l'open source est un choix commercial de SonarSource, qui a déjà renommé et changé ses offres. Ladybug n'en dépend pas : l'étape se saute sans jeton, et [ArchUnit](archunit.md) et [JaCoCo](jacoco.md), libres, restent acquis. Repli possible : SonarQube Community Build (installé chez soi, sans analyse des PR), ou CodeQL (GitHub, gratuit pour un dépôt public).
- *Les règles ne sont pas des ordres* : 13 remarques identiques (règle S6813, le style d'injection Spring) ne valent pas 13 corrections urgentes. On trie : corriger, marquer « sans risque » avec une raison, ou désactiver une règle qui ne convient pas au projet.

**✏️ Exercice**

Ouvrez le tableau de bord, onglet « Issues », filtre « Bug ». Choisissez celui de `SimpleTimer` (« re-interrupt this method… »), lisez l'explication de la règle (« Why is this an issue? ») et dites si c'est une vraie alerte pour Ladybug. Si oui, écrivez la correction dans une petite PR et regardez Sonar la valider.
