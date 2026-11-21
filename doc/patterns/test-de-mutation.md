# Test de mutation : tester les tests

> Technique outillée par [PIT](https://pitest.org) (pitest). Introduite dans Ladybug à l'étape 3.5.5 (novembre 2026), sur l'objectif du doc « Stratégie de tests autonomes » de Frédéric : un score de mutation d'au moins 60 %.

**🟢 L'idée en une image (junior)**

Pour vérifier qu'une alarme fonctionne, on ne se contente pas de constater qu'elle est installée : on ouvre une fenêtre et on écoute si elle sonne. PIT fait la même chose avec les tests. Il glisse volontairement une petite erreur dans le code (un « mutant ») : un `<` devient `<=`, un `+` devient `-`, un `return true` devient `return false`. Puis il relance les tests. S'ils échouent, l'alarme a sonné : le mutant est **tué**. S'ils passent quand même, le mutant a **survécu** : cette partie du code n'est pas vraiment protégée.

**🔵 Dans Ladybug (confirmé)**

*Avant* : on savait **combien** de tests il y avait (159), pas **ce qu'ils protégeaient vraiment**. Une ligne exécutée par un test n'est pas forcément vérifiée.

*Exemple réel* : la zone morte du stick de la manette, dans `XboxRequest` :
```java
private boolean isXLeftStick() {
    return (xLeftStick >= XboxOneConstants.SENSITIVE || xLeftStick <= -XboxOneConstants.SENSITIVE);
}
```
PIT a remplacé `>=` par `>`, et aucun test n'échouait : le seuil exact (0,45) n'était vérifié nulle part. Le test ajouté en 3.5.5 le borde des deux côtés :
```java
request.readCurrentState(Pad.connected().leftStick(0.44f, -0.44f).build());
assertThat(request.isRightLeftStick()).isFalse();   // sous le seuil : rien
request.readCurrentState(Pad.connected().leftStick(0.46f, 0).build());
assertThat(request.isRightLeftStick()).isTrue();    // au-dessus : à droite
```

*Les chiffres de Ladybug* :

| | Score de mutation | Couverture des lignes |
|---|---|---|
| Premier passage (tout le code) | 38 % | 56 % |
| Sans le dessin Swing ni le démarrage (C34) | 60 % | — |
| Après les tests de la manette et des images | **68 %** | **81 %** |

Ces 81 % sont mesurés par PIT sur son seul périmètre (sans le dessin Swing ni le démarrage). [JaCoCo](../qualite/jacoco.md), qui compte tout le jeu, vues comprises, donne 65,7 % des lignes.

Où le trouver :
- la configuration : plugin `pitest-maven` dans le [pom](../../pom.xml) ;
- le lancement : `mvn test-compile org.pitest:pitest-maven:mutationCoverage` (une dizaine de minutes), rapport dans `target/pit-reports/index.html` ;
- la CI : workflow [`mutation.yml`](../../.github/workflows/mutation.yml), chaque nuit sur `develop` et à la demande (≈ 36 min sur GitHub, trop long pour chaque PR), échec sous 60 % ; voir aussi la fiche [CI GitHub Actions](../qualite/ci-github-actions.md).

**🟣 Le recul (senior / vétéran)**

- *Couverture contre mutation* : la couverture dit « cette ligne a été exécutée », la mutation dit « si cette ligne était fausse, un test le verrait ». La seconde est bien plus exigeante. PIT appelle « force des tests » le score calculé sur le seul code exécuté (82 % dans Ladybug).
- *Le périmètre* : mesurer du code qu'aucun test sans écran ne peut atteindre (le dessin Swing) fait baisser le score sans rien dire de la qualité des tests. Ladybug l'exclut jusqu'aux images clés (C30, C34), et le robot comme les parties de Frédéric le couvrent.
- *Le seuil* : 60 % dans la CI, avec la marge d'un score à 68 %. Un seuil trop proche du score casse la CI au moindre ajout ; trop bas, il ne protège plus rien.
- *Le coût* : chaque mutant relance les tests qui le concernent, soit ~14 000 exécutions, ~11 minutes en local et ≈ 36 minutes sur GitHub. D'où un seul passage, sur un seul système, la nuit plutôt qu'à chaque PR.
- *Pièges* :
  - **les mutants équivalents** : certains changements ne modifient pas le comportement (par exemple un `>=` sur une valeur qui n'est jamais égale au seuil). Ils survivent toujours ; viser 100 % n'a pas de sens ;
  - **les boucles sans fin** : un mutant qui inverse une condition de sortie fait tourner un test indéfiniment ; PIT l'arrête au bout d'un délai et le compte comme détecté (`TIMED_OUT`) ;
  - **tester pour le score** : un test qui ne vérifie rien d'utile mais tue un mutant améliore le chiffre, pas le jeu. Un mutant survivant est une question (« ce comportement compte-t-il ? »), pas un ordre.

**✏️ Exercice**

Ouvrez `target/pit-reports/index.html` après un passage de PIT, et cherchez `engine.message.GameMessaging` (0 %). Choisissez un mutant survivant, écrivez le test qui le tue, et relancez PIT pour le voir passer au rouge (tué).
