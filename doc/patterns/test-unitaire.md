# Test unitaire : vérifier une pièce du jeu sans lancer le jeu

> Ce n'est pas un design pattern au sens strict, mais c'est la technique qui protège tous les refactorings suivants. Elle a donc sa fiche.

**🟢 L'idée en une image (junior)**

Un garagiste ne fait pas le tour du pâté de maisons pour vérifier une ampoule de phare : il la branche sur un banc d'essai. Un test unitaire, c'est un banc d'essai pour une pièce du jeu. On vérifie `Score` sans ouvrir de fenêtre, sans jouer et sans attendre qu'un fantôme passe au bon endroit. Le test dure quelques millisecondes et se relance après chaque modification.

**🔵 Dans Ladybug (confirmé)**

*Avant* : pour savoir combien rapporte un fantôme mangé, il fallait lancer le jeu, avaler une méga-gomme, attraper un fantôme et regarder le score. C'est long, et on ne voit pas tout : personne n'avait remarqué que le score montait de 30 alors que le message affiche 15.

*Après* : `src/test/java/com/kycox/game/engine/score/ScoreTest.java`. Chaque test suit le même plan en trois temps, **Arrange, Act, Assert** (préparer, agir, vérifier) :

```java
@Test
@DisplayName("Un fantôme mangé rapporte 15 points, comme l'affiche le message")
void eatenGhost() {
    // Arrange : un groupe de fantômes dont un vient d'être mangé
    when(ghostsGroup.getNbrEatenGhosts()).thenReturn(1);
    // Act : le jeu calcule le score de ce tour
    score.setScoreAndMessages(ghostsGroup, ladybug, groupMessages);
    // Assert : 15 points, pas un de plus
    assertThat(score.getScore()).isEqualTo(GameMainConstants.SCORE_EATEN_GHOST);
}
```

Résultat au premier lancement :

```
ScoreTest.eatenGhost
expected: 15
 but was: 30
```

Le test a trouvé un bug : `Score.setScore` **et** `Score.setMessages` ajoutent tous les deux les points du fantôme.

**Les « mocks »** (faux objets). `Score` a besoin d'une `Ladybug` et d'un `GhostsGroup`. Construire une vraie Ladybug demande Spring, des images et un labyrinthe. On demande plutôt à Mockito une doublure :

```java
ladybug = mock(Ladybug.class);                     // une fausse Ladybug
when(ladybug.isEatenAPoint()).thenReturn(true);    // « fais comme si tu avais mangé une gomme »
```

Par défaut, une doublure répond `false`, `0` ou une liste vide. On ne règle donc que ce qui compte pour le test.

**Les tests paramétrés.** Pour vérifier une formule sur plusieurs valeurs, on écrit le test une fois et on lui donne un tableau (`SpeedFunctionTest`) :

```java
@ParameterizedTest(name = "niveau {0} -> index {1}")
@CsvSource({ "1, 2", "7, 3", "13, 4", "19, 5", "25, 6" })
void indexGrowsWithLevel(int numLevel, int expectedIndex) {
    assertThat(speedFunction.getRealIndexSpeed(numLevel)).isEqualTo(expectedIndex);
}
```

Où les trouver :
- `src/test/java/com/kycox/game/engine/score/ScoreTest.java` ;
- `src/test/java/com/kycox/game/tools/maths/` : `SpeedFunctionTest`, `BlinkySpeedIndexTest`, `GhostSensitiveBehaviourTest`.

On les lance avec `mvn test`, ou avec le bouton ▶ d'IntelliJ à côté de la classe. La CI GitHub les lance sur chaque PR.

**🟣 Le recul (senior / vétéran)**

- *Ce que le test révèle sur la conception* : une classe difficile à tester est souvent une classe qui fait trop de choses. `Score` calcule le score **et** fabrique les messages affichés (le `FIXME` de l'auteur le disait déjà) ; le bug est né de ce mélange. Les tests de `Dijkstra` (étape 3.2b) montrent l'inverse : comme `ScreenData` se construit sans Spring, le test est simple.
- *Quand ne pas mocker* : on mocke ce qui est lourd ou hors sujet (Ladybug, Swing, réseau). On garde les vrais objets simples : `ScoreTest` utilise un vrai `Fruits`, parce que la table des scores des fruits fait partie de ce qu'on veut vérifier. Trop de mocks, et le test ne vérifie plus que ses propres réglages.
- *L'aléatoire* : `GhostSensitiveBehaviour` tire au sort avec une méthode statique (`Utils.generateRandomInt`), qu'on ne peut pas remplacer dans un test. On le vérifie donc sur 20 000 tirages, avec une marge de plus de dix écarts-types. La vraie solution, plus tard : injecter une source d'aléa (`RandomGenerator` de Java 17), pour qu'un test puisse fixer le tirage.
- *Documenter l'existant* : un test peut aussi décrire un comportement curieux sans le juger. `SpeedFunctionTest.lastLevelIndexIsOutOfValidSpeeds` montre qu'au niveau 30 l'index sort de la liste des vitesses, et que `Body.getSpeed()` le rattrape. Si quelqu'un change ce comportement, le test le signale.
- *Pièges* : tester l'implémentation plutôt que le résultat (« la méthode privée X a été appelée ») ; écrire un test qui passe toujours ; laisser un test `@Disabled` sans raison ni date.

**✏️ Exercice**

Dans `ScoreTest`, écrivez un test « Ladybug mange une gomme ET se téléporte dans le même tour » : quel score attendez-vous ? Lancez-le. Puis écrivez un test pour un fruit inconnu (`getEatenAIdRefFruit()` renvoie 99) : que rapporte-t-il, et pourquoi ? (Indice : lisez `Fruits.getFruitById` et le « bouchon ».)
