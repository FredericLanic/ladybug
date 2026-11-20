# Extract Class : une classe, un métier

> Source : Martin Fowler, *Refactoring* (« Extract Class »), et le principe de responsabilité unique (le « S » de SOLID). Introduit dans Ladybug à l'étape 4.4 (novembre 2026) ; Frédéric a choisi l'option B (une seule liste de cases à tenir à jour).

**🟢 L'idée en une image (junior)**

Dans un restaurant, le cuisinier prépare les plats et le serveur les présente. Si le cuisinier devait aussi tenir un second carnet des commandes « pour la salle », recopié à la main à chaque plat servi, un jour les deux carnets ne diraient plus la même chose.

`ScreenData` était ce cuisinier-serveur : il portait le labyrinthe **pour les règles du jeu** et une **copie** du labyrinthe **pour le dessin**, qu'il recopiait à la main à chaque gomme ou fruit mangé. On a séparé les deux métiers : `Maze` connaît le labyrinthe ; `MazeOutline`, côté vue, sait seulement tracer ses murs.

**🔵 Dans Ladybug (confirmé)**

*Avant* : `ScreenData` tenait deux listes de cases, et chaque changement devait être fait deux fois.

```java
// ScreenData
private List<ScreenBlock> dataScreenBlocks = …;              // pour le jeu
private final List<ScreenBlock> viewScreenBlocks = …;        // pour le dessin (copie)

public void updateScreenBlock(Ladybug ladybug) {
    currentScreenBlock.removePoint();
    currentScreenBlock.addEatenPoint();
    // … et la même chose dans la copie, retrouvée par ses coordonnées
    viewScreenBlocks.stream().filter(sb -> sb.getCoordinate().equals(currentScreenBlock.getCoordinate()))
            .findFirst().orElseThrow().removePoint();
}

public int getScreenWidth() { return … * GameMainConstants.BLOCK_SIZE; }   // des pixels dans le modèle
```

*Après* : deux classes, chacune son métier.

```java
// Maze (modèle) : une seule liste de cases, plus aucun pixel
public void updateScreenBlock(Ladybug ladybug) {
    currentScreenBlock.removePoint();
    currentScreenBlock.addEatenPoint();
}

// MazeOutline (vue) : une copie qui ne sert qu'à la FORME des murs,
// reconstruite seulement quand le labyrinthe change de forme (nouveau niveau)
public MazeOutline of(Maze maze) {
    if (maze.getLayoutVersion() != builtFromLayoutVersion) {
        rebuild(maze);   // copie des cases + nettoyage des bordures pour le tracé
    }
    return this;
}
```

Le dessin lit chaque information à la bonne source :
- les **murs** (`LinesRendering`, `TilesRendering…`) lisent le contour : `outline.getBlock(position)` ;
- les **gommes, fruits et téléportations** (`ScreenBlockView`) lisent le jeu : `maze.getScreenBlock(position)`. C'était déjà le cas pour les gommes, pas pour les fruits ni les téléportations ;
- la **taille en pixels** est calculée par `CentralView` (nombre de cases × `BLOCK_SIZE`).

Où le trouver :
- [`Maze`](../../src/main/java/com/kycox/game/engine/maze/Maze.java), anciennement `engine/screendata/ScreenData`
- [`MazeOutline`](../../src/main/java/com/kycox/game/engine/view/map/MazeOutline.java) et [`MazeOutlineTest`](../../src/test/java/com/kycox/game/engine/view/map/MazeOutlineTest.java)
- [`DisplayRendering`](../../src/main/java/com/kycox/game/engine/view/map/rendering/DisplayRendering.java), qui passe le contour aux dessins des murs

**🟣 Le recul (senior / vétéran)**

- *Le signal* : une classe dont on décrit le rôle avec un « et » (« la carte du jeu **et** sa copie de dessin **et** les pixels »), et qui a beaucoup de liens. Graphify mesurait 95 liens pour `ScreenData`, le plus gros nœud après `Position`.
- *Pourquoi l'option B plutôt que de simplement déplacer la copie ?* Déplacer la copie (option A) aurait gardé deux listes à synchroniser. Ne garder dans la copie que ce qui en a besoin, la forme, supprime le problème au lieu de le déplacer : il n'y a plus qu'une seule vérité pour le contenu des cases. C'est aussi ce que demandera le `GameSnapshot` (4.6) : la vue lira le contenu dans l'état du jeu, et calculera elle-même ce qui ne concerne que le dessin.
- *Comment la vue sait-elle qu'il faut reconstruire ?* Par un numéro de version (`layoutVersion`) que `Maze` augmente à chaque niveau préparé. C'est moins élégant qu'un événement, mais c'est simple, et il n'y a pas d'ordre de réception à gérer. Le labyrinthe dynamique (étape 7 bis) pourra augmenter ce numéro à chaque mur qui bouge.
- *Le changement était-il sûr ?* Le golden master est identique (le modèle se comporte pareil). Il ne voit pas le dessin : les murs, gommes et fruits sont vérifiés à l'œil en partie, et `MazeOutlineTest` vérifie le contour sur les huit niveaux (copie de chaque case, bords extérieurs, reconstruction seulement au changement de niveau).
- *Ce qui reste* : `Maze` porte encore le style de rendu tiré au hasard et le mode lampe. Ce sont des décisions du jeu, mais pas des propriétés du labyrinthe : ils partent en 4.5, sans changer l'ordre des tirages au hasard.

**✏️ Exercice**

`CentralView` calcule maintenant `widthInPixels()` et `heightInPixels()`. Cherchez dans `CentralView` les cinq méthodes `draw…CenterTextLine(s)` : elles répètent toutes les mêmes quatre lignes (taille, police, couleur, centrage). Quelle petite classe pourriez-vous extraire pour écrire du texte centré dans le labyrinthe ? Quel serait son unique métier ?
