## Étape 7 bis : labyrinthe dynamique (idée de Hammed, 18/11/2026)
Idée proposée par Hammed après une démo : le labyrinthe change pendant la partie. Frédéric retient **les quatre variantes** : A. portes, B. murs mobiles, C. murs déclenchés par les joueurs, D. labyrinthe généré.

**Constats dans le code (`develop`)** :
- les fantômes s'adapteraient d'eux-mêmes : `Dijkstra.getShorterWay` reconstruit son graphe depuis `ScreenData` à chaque appel ;
- l'affichage est figé : les bordures sont calculées une seule fois par niveau (`checkViewBlockBorder`), avec la double liste `dataScreenBlocks` / `viewScreenBlocks`. L'étape 7 (`MazeBorderCalculator`, source de vérité unique, image pré-rendue) est donc un **prérequis**.

**7 bis.0 Socle commun** (construit une seule fois) :
- `MutableMaze` : seul point d'entrée pour changer un mur (`setWall(case, bool)`) ;
- `MazeChangeEvent` (**Observer**) : prévient la vue (redessiner seulement la zone touchée de l'image pré-rendue), les fantômes et, plus tard, le réseau ;
- `MazeChange` (**Command**) : un changement décrit comme un objet daté, par exemple « fermer (12,5) à t+30 s » ;
- `ReachabilityChecker` : refuse tout changement qui rendrait une gomme inaccessible (parcours en largeur, testé unitairement) ;
- `OccupancyRule` (**Strategy**) : que faire si un mur se ferme sur un personnage. Par défaut, attendre que la case soit libre (à confirmer).

Les variantes ne diffèrent que par **qui décide** du changement, sur le même principe que `PlayerInput` (étape 5).

7 bis.1 **A. Portes** : décrites dans le fichier du niveau (`door: 12,5 period=20s`). Premier palier : il installe et montre le socle.
7 bis.2 **B. Murs mobiles** : une `MazeMutationStrategy` (planifiée ou aléatoire), réglable en XML.
7 bis.3 **D. Labyrinthe généré** : un `MazeGenerator` (**Builder**) produit un niveau, validé par le même `ReachabilityChecker` et chargé comme les fichiers de l'étape 7.
**C. Murs déclenchés par les joueurs** : traitée en 6.7 (pouvoir configurable) puis en 9.6 (réseau), une fois les capacités et les entrées par personnage en place.

**Vérification** : une porte s'ouvre et se ferme, les fantômes changent de chemin, aucune gomme ne devient inaccessible ; capture avant/après.
