# Snapshot : les vues regardent une photo, pas le jeu

> Source : la notion d'« état immuable » publié par un modèle (Martin Fowler, *Event-Carried State Transfer* ; le Memento du Gang of Four en est un cousin), et l'architecture « hôte qui fait autorité » des jeux en réseau. Introduit dans Ladybug à l'étape 4.6 (novembre 2026), décision C45 de Frédéric.

**🟢 L'idée en une image (junior)**

Un match de foot à la télévision. Le réalisateur ne laisse pas chaque téléspectateur entrer sur le terrain pour regarder : il envoie des **images**. Le téléspectateur peut les regarder autant qu'il veut, il ne peut pas déplacer un joueur.

Dans Ladybug, à la fin de chaque battement, le jeu prend une **photo** de la partie (`GameSnapshot`) et l'envoie. Les vues, les sons, la manette et le journal des tests ne regardent que cette photo, jamais le jeu lui-même.

**🔵 Dans Ladybug (confirmé)**

*Avant* : le modèle s'envoyait **lui-même** dans l'événement, et chaque vue gardait une référence vers le jeu vivant.

```java
// GameModel, à la fin de chaque battement
applicationEventPublisher.publishEvent(new EventGameModel(this));

// CentralView
public void onApplicationEvent(EventGameModel event) {
    if (event.getSource() instanceof GameModelForViews model) {
        gameModelForViews = model;           // le jeu vivant…
        repaint();                           // …relu plus tard, au moment du dessin
    }
}
// et getGhosts() renvoyait la vraie classe Ghost : une vue pouvait la modifier
```

*Après* : le modèle envoie une photo, faite de `record` et de listes non modifiables.

```java
// GameModel
applicationEventPublisher.publishEvent(new EventGameModel(this, GameSnapshotFactory.of(this)));

// CentralView
public void onApplicationEvent(EventGameModel event) {
    snapshot = event.getSnapshot();
    repaint();
}

// le dessin ne lit que la photo
if (snapshot.is(GameStatus.LEVEL_STARTING)) { … }
g2d.drawImage(ghostView.getImage(ghost), ghost.position().x() + 1, ghost.position().y() + 1, this);
```

La photo contient : statut, niveau, score, joueurs, Ladybug (`LadybugSnapshot`), les fantômes (`GhostSnapshot`), le labyrinthe case par case (`MazeSnapshot`, `CellSnapshot`), la lampe et le style des murs, les messages de score et les sons du battement.

Où le trouver :
- le package [`snapshot`](../../src/main/java/com/kycox/game/snapshot/) : les `record` ;
- [`GameSnapshotFactory`](../../src/main/java/com/kycox/game/engine/model/GameSnapshotFactory.java) : le seul endroit qui traduit le jeu vivant en photo ;
- [`GameSnapshotFactoryTest`](../../src/test/java/com/kycox/game/engine/model/GameSnapshotFactoryTest.java) et [`Snapshots`](../../src/test/java/com/kycox/game/testutil/Snapshots.java) (petites photos faites à la main pour les tests).

**🟣 Le recul (senior / vétéran)**

- *Ce que ça apporte* :
  - **lecture seule garantie** par le compilateur, et non plus par la bonne volonté des contrats : il n'y a rien à modifier dans un `record` ;
  - **une seule frontière** entre le jeu et l'affichage : 9 contrats ont disparu (`GameModelForViews`, `LadybugForGameView`, `GhostForView`, `GameModelForSounds`…) ; une vue ne connaît plus que la photo ;
  - **le réseau (étape 9)** : l'hôte enverra exactement cette photo aux autres postes ; un client n'aura jamais besoin du modèle ;
  - **une autre interface** (libGDX, Android, idées I17 et I18) n'aura qu'à lire la même photo.
- *Comment on a vérifié que rien ne manquait* : en 4.6a, le journal des tests (golden master) a été reconstruit à partir de la photo **avant** de toucher aux vues. Journal identique ⇒ la photo contient tout ce qu'il regardait. Puis les vues sont passées à la photo par morceaux (4.6b, 4.6c), avec une partie de contrôle à chaque fois.
- *Le coût* : une photo par battement, soit 25 par seconde, dont environ 300 cases de labyrinthe. C'est négligeable ici. Si un jour ça comptait (réseau lent), on n'enverrait le labyrinthe que lorsqu'il change (`layoutVersion`) ou seulement les différences.
- *Ce qui reste hors de la photo* :
  - les **commandes** : les contrôleurs agissent encore sur le modèle (`GameModelForController`), jusqu'à `PlayerInput` (étape 5) ;
  - les **messages du bandeau** (`GameMessaging`) : ils mélangent messages du jeu et messages du poste ; le partage sera décidé à l'étape 9 ;
  - les **préférences locales** (`LocalPreferences`) : par nature, elles ne font pas partie de la partie.
- *Piège* : une photo ne montre qu'un instant. Un signal qui ne dure qu'un battement (« vie gagnée ») doit être remarqué **à la réception** de la photo, pas au moment du dessin : deux battements peuvent passer entre deux dessins (dette D25).

**✏️ Exercice**

Le panneau de debug (`PageLeftView`) affiche la position de chaque fantôme. Ajoutez sa **case** (`ghost.block()`) à l'affichage. Quelle classe devez-vous modifier ? Et laquelle ne devez-vous **pas** toucher ? (Indice : la photo contient déjà l'information.)
