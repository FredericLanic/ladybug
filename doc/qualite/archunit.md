# ArchUnit : l'architecture vérifiée comme un test

> Bibliothèque Java libre ([archunit.org](https://www.archunit.org)), version 1.5.1. Introduite dans Ladybug à l'étape 5 bis (21/11/2026). Fichier : [`ArchitectureTest`](../../src/test/java/com/kycox/game/ArchitectureTest.java).

**🟢 L'idée en une image (junior)**

Dans un immeuble, le plan dit que la cuisine ne donne pas sur la chambre. Si un ouvrier perce une porte entre les deux, personne ne s'en aperçoit avant longtemps. ArchUnit est un inspecteur qui repasse à chaque build et vérifie que les portes sont là où le plan les prévoit.

Dans Ladybug, le « plan », ce sont les décisions des étapes 4 et 5 : les vues ne lisent que la photo de la partie, le modèle ne connaît pas l'écran, etc. Avant ArchUnit, ces règles ne tenaient que par la discipline. Maintenant, les contourner fait échouer le build.

**🔵 Dans Ladybug (confirmé)**

ArchUnit lit les classes compilées (le bytecode) et sait, pour chaque classe, quelles autres classes elle utilise. Une règle s'écrit presque comme une phrase :

```java
@AnalyzeClasses(packages = "com.kycox.game", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

	@ArchTest
	static final ArchRule gameCoreDoesNotKnowTheScreen = noClasses().that()
	        .resideInAnyPackage(GAME_CORE)                       // modèle, personnages, labyrinthe…
	        .should().dependOnClassesThat().resideInAnyPackage("java.awt..", "javax.swing..")
	        .because("le modèle doit tourner sans écran (tests, futur serveur réseau)");
}
```

Les quatre règles de Ladybug :

| Règle | Décision qu'elle protège |
|---|---|
| Le cœur du jeu (`engine.model`, `maze`, `body`, `input`, `snapshot`…) n'utilise ni AWT ni Swing | Étape 4.1 : le jeu tourne sans écran, dans les tests comme demain sur un serveur |
| Les vues ne dépendent pas du modèle vivant (sauf `EventGameModel` et trois énumérations partagées) | Étape 4.6 : une vue dessine le `GameSnapshot`, rien d'autre |
| Seuls `engine.controller` et `input` appellent `PlayerInput.request(…)` | Étape 5 : un ordre de joueur vient d'un clavier ou d'une manette (demain du réseau) |
| Aucune **nouvelle** boucle entre les grands packages | Un package qui dépend d'un autre qui dépend de lui ne se comprend pas seul |

Quand une règle est enfreinte, le message dit qui, où et pourquoi :

```
Rule 'no classes that reside in a package 'com.kycox.game.engine.view..' should depend on
classes that appartiennent au modèle vivant, because une vue dessine la photo de la partie…'
Method <CentralView.drawScoresIncrement(Graphics2D)> calls method <MessageType.getEndMessage()> in (CentralView.java:332)
```

Ce message-là est réel : au premier passage, il a montré que `MessageType` voyage dans le snapshot. Il a rejoint la liste des exceptions assumées.

*Dette gelée.* La règle des boucles en a trouvé **53**, surtout à cause du package `constant`, qui mélange constantes, contrats, images et données de niveaux (dette D29). Tout réorganiser tout de suite aurait été une étape à part entière. La règle est donc **gelée** (`FreezingArchRule`) :

```java
static final ArchRule noNewCyclesBetweenTopLevelPackages = FreezingArchRule
        .freeze(slices().matching("com.kycox.game.(*)..").should().beFreeOfCycles())
        .associateViolationLinesVia(ArchitectureTest::sameCycle);
```

- les boucles d'aujourd'hui sont enregistrées dans `src/test/resources/archunit-store/` ;
- une boucle **nouvelle** fait échouer le build ;
- une boucle supprimée sort du registre et ne peut plus revenir.

Où le trouver :
- les règles : [`ArchitectureTest.java`](../../src/test/java/com/kycox/game/ArchitectureTest.java) ;
- les réglages du gel : [`archunit.properties`](../../src/test/resources/archunit.properties) ;
- le lancement : avec les autres tests (`mvn test`), moins d'une seconde.

**🟣 Le recul (senior / vétéran)**

- *Pourquoi Graphify disait « aucun cycle »* : Graphify regarde les imports d'une classe à l'autre ; ArchUnit regarde les dépendances entre **packages**. `body.Ghost` utilise `constant.GhostStatus`, et `constant.GhostsBodyImages` utilise `body.GhostColor` : aucune classe ne tourne en rond, mais les deux packages, si. Les deux constats sont vrais, à deux échelles différentes.
- *Un registre trop bavard est un piège* : le premier registre (600 Ko) listait chaque dépendance de chaque boucle. Un simple appel ajouté sur un lien déjà en boucle aurait été vu comme une « nouvelle » violation, et le build aurait cassé pour rien. Réglage retenu : une boucle est reconnue par son seul chemin (`body -> constant -> body`), en ignorant le nombre de dépendances (`cycles.maxNumberOfDependenciesPerEdge=0` et `sameCycle`). Registre : 26 Ko.
- *Ne jamais régénérer le registre pour faire passer un build* : ce serait effacer la dette sans la payer. Le registre ne doit que maigrir.
- *Vérifier la règle elle-même* : chaque règle a été testée par sabotage. On introduit exprès une violation (une classe de `body` qui utilise `java.awt.Point`, une vue qui lit `GhostsGroup`…), le build doit échouer, puis on retire le sabotage. Une règle qui ne peut pas échouer ne protège rien.
- *Alternatives* : jMolecules et Spring Modulith (modules plus formels), ou des modules Maven séparés (idée I18), qui rendent une dépendance interdite impossible à compiler. ArchUnit est le plus léger : une dépendance de test, aucun changement du code.

**✏️ Exercice**

Dans une vue (par exemple `PageLeftView`), ajoutez un champ `private com.kycox.game.body.GhostsGroup ghosts;`. Lancez `mvn test -Dtest=ArchitectureTest` et lisez le message. Retirez le champ. Bonus : déplacez `GhostColor` hors de `body` (dans `snapshot`, par exemple) et regardez combien de boucles disparaissent du registre.
