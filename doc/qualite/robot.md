# Le robot de vérification visuelle : le jeu joué au clavier, captures à l'appui

> Programme écrit pour Ladybug à l'étape 0 (18/11/2026), relancé à la fin de chaque étape. Fichier : [`doc/reference-avant/robot/LadybugRobot.java`](../reference-avant/robot/LadybugRobot.java). Résultats : `doc/reference-apres/<étape>/`.

**🟢 L'idée en une image (junior)**

Avant de rendre une voiture après réparation, le garagiste fait un tour du pâté de maisons : il démarre, tourne, freine, allume les phares. Il ne démonte rien, il **utilise** la voiture comme le client.

Le robot fait pareil avec Ladybug : il lance le vrai jeu, appuie sur les touches comme un joueur, prend des photos de l'écran et note ce que le jeu écrit dans ses logs. À la fin, il produit un rapport qu'on compare à celui de l'étape précédente.

**🔵 Dans Ladybug (confirmé)**

Le robot utilise `java.awt.Robot`, la classe Java qui sait envoyer des touches et capturer l'écran. Il se lance sans compilation, grâce au « source launcher » de Java 21 :

```bash
java doc/reference-avant/robot/LadybugRobot.java target/ladybug-0.9.0-SNAPSHOT.jar doc/reference-apres/etape-5bis-qualite
```

Son scénario, toujours le même :

| Section | Ce qu'il vérifie |
|---|---|
| 1. Démarrage | écran de présentation, aide F1 enfoncée puis relâchée, choix 1 ou 2 joueurs |
| 2.1 Déplacement | les quatre flèches font bouger Ladybug |
| 5. Touches de fonction | F2 à F7 et Entrée : chaque touche change bien l'image (pourcentage de pixels modifiés) |
| 5.6 Pause | deux captures en pause identiques, puis une différente après reprise |
| 5.7 Échap | la question « quitter ? » apparaît, N ramène au jeu |
| 6. Rendus | 12 parties pour voir les rendus de labyrinthe, tirés au hasard |
| 4.1 Mode 2 joueurs | le fantôme humain répond |
| Sortie | Échap en présentation ferme le jeu |

Ce qu'il produit : 47 fichiers (captures numérotées, `rapport.md`, `ladybug.log`). Le chiffre clé est le nombre de **changements de statut** lus dans les logs (`Passage du status en IN_GAME`…) : **174** à chaque étape depuis l'étape 3. Le même nombre, c'est le même parcours du jeu.

Un extrait de `rapport.md` :

```
## 5. Touches de fonction
   F6 : 13,88 % des pixels ont changé
## 5.7 Échap
   N : retour en partie ✅
```

**🟣 Le recul (senior / vétéran)**

- *Ce qu'il voit et que les tests ne voient pas* : le dessin réel dans une vraie fenêtre, le démarrage complet par Spring Boot, la manette et le clavier tels que macOS les transmet. Les tests sans écran (golden master) vérifient les règles ; le robot vérifie que le jeu **s'affiche et répond**.
- *Il est sensible au hasard* : le jeu, lancé normalement, n'a pas de graine fixe. Le 21/11, deux passages se sont arrêtés à « 5.7 Échap » : pendant les tests des touches F2 à F7, Ladybug reste immobile, et les fantômes l'ont mangée trois fois. Fin de partie, retour à la présentation, et Échap y ferme le jeu. Le journal l'a prouvé (« Ladybug is eaten by Inky / Pinky / Clyde »). Ce n'était pas un bug, et le 3e passage était complet. Piste : déplacer Ladybug entre les touches, ou lancer le jeu avec une graine.
- *Les écarts de pixels se lisent avec du recul* : environ 1 % de différence est du bruit (animations, clignotements). F6 (le mode lampe) varie de 3 à 14 % selon que la lampe s'allume ou s'éteint, tirée au hasard.
- *Il prend la main sur l'ordinateur* : il faut autoriser le terminal dans Réglages système (Accessibilité et Enregistrement de l'écran), et ne pas toucher au clavier pendant ses 5 minutes. Par sécurité, il s'arrête si la fenêtre du jeu n'est plus au premier plan, plutôt que de taper ailleurs.
- *Pourquoi pas en CI* : les machines de GitHub n'ont pas d'écran. On pourrait en simuler un (Xvfb sous Linux), mais sans macOS ni manette réelle. Le robot reste un outil local, relancé à la fin de chaque étape.
- *Ce qu'il ne remplace pas* : le son, le plaisir de jeu. D'où les parties de contrôle de Frédéric.

**🎮 Voir le bot jouer : `BotDemo`**

Le robot appuie sur des touches à l'aveugle. Pour voir un vrai joueur automatique, il y a la démo [`BotDemo`](../../src/test/java/com/kycox/game/testutil/BotDemo.java) : le vrai jeu, avec sa fenêtre, où Ladybug est jouée par le bot des tests (`AimingBot` : fuir, chasser, manger). Elle démarre seule une partie après 4 secondes de présentation, et recommence à chaque fin de partie.

```bash
mvn -q test-compile exec:java -Dexec.mainClass=com.kycox.game.testutil.BotDemo -Dexec.classpathScope=test
```

Elle reste dans les tests : le jar livré n'a pas de mode « bot ». Elle a déjà servi : regardée par Frédéric, elle a montré un bot qui dansait sur place (il décidait entre deux cases ; corrigé : il ne décide plus qu'au centre d'une case), puis, laissée toute une nuit, un vrai bug du jeu : un demi-tour pile sur un téléporteur faisait sortir Ladybug du labyrinthe (dette D32).

**✏️ Exercice**

Relancez le robot sur le jar actuel vers un dossier d'essai, puis comparez son `rapport.md` à celui de `doc/reference-apres/etape-5bis-qualite/`. Les nombres de statuts sont-ils égaux ? Quelles touches ont le plus grand écart de pixels, et pourquoi ?
