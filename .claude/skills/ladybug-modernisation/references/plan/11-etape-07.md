## Étape 7 : niveaux en fichiers
7.1 Un fichier texte par niveau dans `resources/levels/`, avec la grille et un en-tête : départ, régénération, méga-gommes, téléportations.
7.2 `LevelLoader`, avec la même priorité dossier du jar > classpath.
7.3 Conversion de `Level1NG` à `Level8NG`, puis suppression de ces classes et des anciennes `Level1`, `Level3` et `Level`.

7.4 **Rendu du labyrinthe** (`view/map/ScreenBlockView`, `level/LevelNG`, `level/CheckScreenBlockBorders`).

Principe : **le style actuel est conservé à l'identique** (contours fins, coins arrondis, pointillés de gommes) ; c'est l'implémentation qui change.
- **Bordures typées** : le masque de bits `int` (`LEFT=1`, `UP=2`…) devient un `EnumSet<Border>`.
- **Autotiling** :
  - les vingt-cinq conditions de `displayBorders()` sont remplacées par un calcul de configuration des voisins et une **table** qui associe chaque configuration aux segments et arcs à dessiner ;
  - chaque cas est testable unitairement.
- **Fusion du calcul des bordures** : `screenBlockValue()` et `CheckScreenBlockBorders` deviennent une seule classe dédiée, par exemple `MazeBorderCalculator`. Une seule source de vérité au lieu de la double liste `screenBlocks`/`viewBlocks`.
- **Image pré-rendue** :
  - les murs sont dessinés une seule fois par niveau dans une `BufferedImage` ;
  - à chaque tick, on ne dessine plus que les gommes, les fruits, les téléporteurs et les personnages ;
  - le mode lampe devient un masque circulaire (`clip`) appliqué à cette image.
- **Thème** : couleurs (murs, gommes, méga-gommes, régénération, téléportation) et épaisseur du trait sont sorties dans un objet `MazeTheme` configurable en XML. C'est la base des skins (CTEC27). Plusieurs styles de labyrinthe deviennent possibles plus tard.
- **Vérification** : capture du rendu avant/après sur chaque niveau. Les images doivent être identiques.
