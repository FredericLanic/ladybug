# Graphify : la carte du code, avant et après chaque étape

> Outil libre ([Graphify-Labs/graphify](https://github.com/Graphify-Labs/graphify), paquet `graphifyy`), installé à l'étape 0 (18/11/2026). Résultats : `graphify-out/` (non versionné), une copie datée par grande étape.

**🟢 L'idée en une image (junior)**

Pour réorganiser une ville, on commence par une vue aérienne : les quartiers, les grandes routes, les carrefours où tout le monde passe. Après les travaux, on reprend une photo du même endroit et on compare.

Graphify fait la vue aérienne du code. Chaque classe est un point, chaque utilisation d'une classe par une autre est un trait. Il repère les « quartiers » (groupes de classes très liées) et les « carrefours » (les classes dont tout dépend).

**🔵 Dans Ladybug (confirmé)**

Une seule commande, toujours avec la même précaution :

```bash
GRAPHIFY_BACKEND=none graphify update .
```

Il produit dans `graphify-out/` :
- `graph.html` : la carte, à ouvrir dans un navigateur ;
- `GRAPH_REPORT.md` : le rapport en texte (carrefours, quartiers, cycles d'imports) ;
- `graph.json` : les données, pour comparer deux versions.

Avant une grande étape, on copie `graphify-out/` sous un nom daté. Après, on relance et on compare. Les mesures qu'il a fournies :

| Mesure | Avant | Après |
|---|---|---|
| Liens de `ScreenData` (étape 4.4) | 95 liens, la classe-carrefour du jeu | classe supprimée, coupée en `Maze` et `MazeOutline` |
| Liens des vues vers le modèle (étape 4.6) | 23 | 1 |
| `GameModelForController` (étape 5) | 32 liens | 30 |
| `GhostsGroup` (étape 5) | 44 liens | 55, à surveiller à l'étape 6 |
| Taille du graphe (étape 5 bis) | 2 165 nœuds | 2 187 nœuds |

Avant de modifier une classe, on peut aussi mesurer l'impact : `/graphify path "A" "B"` (comment A atteint B), `/graphify explain "X"` (le rôle de X).

**🟣 Le recul (senior / vétéran)**

- *`GRAPHIFY_BACKEND=none`, toujours* : sans cette variable, et faute de clé d'API, Graphify appelle Claude pour donner un nom aux quartiers, et envoie donc des noms de classes à l'extérieur. Testé le 18/11 : sans IA, le graphe est identique ; seuls les noms des quartiers sont moins parlants.
- *Il ne voit pas tout* :
  - le XML Spring n'est pas analysé : le câblage des fantômes n'apparaît pas ;
  - il regarde les imports **entre classes**. Il annonçait « aucun cycle d'import », alors qu'[ArchUnit](archunit.md) a trouvé 53 boucles entre **packages**. Deux échelles, deux vérités.
- *Une carte, pas un juge* : un carrefour n'est pas forcément un défaut (un `record Position` utilisé partout est sain). Ce qui compte, c'est l'évolution : un carrefour qui grossit d'étape en étape (`GhostsGroup` : 44 puis 55) signale une classe qui prend trop de rôles.
- *Pourquoi il n'est pas en CI* : c'est un outil de lecture et de comparaison entre deux étapes, pas une règle à faire respecter. La règle, c'est ArchUnit.

**✏️ Exercice**

Ouvrez `graphify-out/graph.html` et cherchez `GhostsGroup`. Comptez ses voisins, puis essayez de les classer en deux ou trois rôles (déplacement, statuts, joueurs humains…). Ce découpage est exactement la question de l'étape 6.
