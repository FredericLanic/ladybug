## Étape 6 : fantômes 100 % configurables (assemblage de capacités)
6.1 `GhostMoveStrategy` : `ChaseStrategy` (ex-AGGRESSIVE), `AmbushStrategy` (ex-SMART), `WanderStrategy` (ex-CALM). Le code est repris de `Ghost`.
6.2 `GhostSpeedPolicy` : `StandardSpeed` (modes NORMAL et MINUS) et `RageSpeed` (logique de Blinky).
6.3 **`Perception`**, valable pour les fantômes **et** pour Ladybug :
- `lightRadius` : rayon éclairé ;
- `detectRadius` : ex-`maximumAttackDist`, utilisé à la fois par l'IA et pour la vue du joueur humain ;
- `seesOpponentAlways` : voir l'adversaire même dans le noir ;
- même paramètre pour l'IA et pour l'humain, ce qui garantit l'équité.

6.4 **Notion d'observateur dans la vue** :
- une classe `VisibilityRule` testable répond à « pour cet observateur, qu'est-ce qui est visible ? » ;
- `CentralView` ne contient plus aucune règle en dur ;
- en écran partagé, l'observateur regroupe tous les joueurs locaux (comportement actuel conservé).

6.5 `Ghost` devient une classe concrète. `Blinky`, `Pinky`, `Inky` et `Clyde` sont supprimées et remplacées par des `<bean>` XML. L'ancienne propriété `behaviour="AGGRESSIVE"` reste acceptée comme raccourci.
6.6 La vue gère un nombre quelconque de fantômes et les couleurs inconnues (image par défaut en repli).
6.7 **Pouvoir « agir sur les murs »** (variante C du labyrinthe dynamique, réalisée après l'étape 7 bis) : une capacité configurable en XML, par exemple « ce fantôme peut ouvrir un mur », « Ladybug peut poser un mur ». Elle passe par le socle de l'étape 7 bis (`MazeChange`, `ReachabilityChecker`). C'est une réponse possible à la question des pouvoirs.
