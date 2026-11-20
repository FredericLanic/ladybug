# Value Object : une position qu'on ne peut pas modifier par erreur

> Source : Eric Evans, *Domain-Driven Design* (« Value Object »), et les `record` de Java 16+. Introduit dans Ladybug à l'étape 4.3 (novembre 2026), sur décision de Frédéric (C44).

**🟢 L'idée en une image (junior)**

Une adresse postale, c'est une valeur : « 12 rue des Lilas ». Si vous déménagez, vous ne grattez pas le numéro sur la porte du voisin, vous prenez une nouvelle adresse. Et deux enveloppes portant la même adresse vont au même endroit, même si ce ne sont pas les mêmes enveloppes.

Dans Ladybug, une position (ou une direction) est une valeur du même genre : deux nombres, `x` et `y`. Avant l'étape 4.3, on pouvait les « gratter » : déplacer un personnage modifiait sa position sur place. Désormais, se déplacer, c'est **prendre une nouvelle position**.

**🔵 Dans Ladybug (confirmé)**

*Avant* : le modèle utilisait `java.awt.Point`, une classe de la bibliothèque graphique, **modifiable**.

```java
// GameMainConstants : une seule instance, partagée par tout le jeu
public static final Point POINT_ZERO = new Point(0, 0);

// Body : la position par défaut d'un corps EST cette constante…
private Point position = GameMainConstants.POINT_ZERO;

// UserBody : … et se déplacer modifie la position sur place
getPosition().translate(getDirection().x * getSpeed(), getDirection().y * getSpeed());
```

Si un corps bougeait avant qu'on lui ait donné sa vraie position, `translate` déplaçait **la constante elle-même** : `POINT_ZERO` ne valait plus (0, 0), pour tout le jeu (dette D21). Pour s'en protéger, le code faisait des copies à la main : `(Point) getPositionBlock().clone()`.

*Après* : un `record` de deux entiers, sans AWT.

```java
public record Position(int x, int y) {
    public Position translate(int dx, int dy) {
        return new Position(x + dx, y + dy);      // une nouvelle position ; celle-ci ne change pas
    }
    public double distance(Position other) { … } // même calcul que java.awt.Point
}

// UserBody : se déplacer, c'est remplacer sa position
setPosition(getPosition().translate(getDirection().x() * getSpeed(), getDirection().y() * getSpeed()));
```

Et la case « devant » un personnage se calcule sans copie ni modification :

```java
// avant                                         // après
var positionBlock = (Point) getPositionBlock().clone();   var positionBlock = getPositionBlock();
if (direction.equals(POINT_UP) && …) {                    if (direction.equals(POINT_UP) && …) {
    positionBlock.y--;                                         return positionBlock.translate(0, -1);
}                                                          }
```

Le `record` écrit pour nous le constructeur, `x()`, `y()`, `equals`, `hashCode` et `toString`. Deux positions de mêmes coordonnées sont égales, comme deux enveloppes à la même adresse.

Où le trouver :
- [`Position`](../../src/main/java/com/kycox/game/tools/Position.java) et [`PositionTest`](../../src/test/java/com/kycox/game/tools/PositionTest.java)
- [`Body.getFrontPositionBlock()`](../../src/main/java/com/kycox/game/body/Body.java) et [`UserBody.teleport()`](../../src/main/java/com/kycox/game/body/UserBody.java)

**🟣 Le recul (senior / vétéran)**

- *Principe* : un objet défini par sa **valeur**, pas par son identité. On ne le modifie pas, on en crée un autre. Une seule règle supprime toute une famille de bogues : l'alias (deux variables qui pointent sur le même objet modifiable) et la constante qui change.
- *Pourquoi pas `Point`* : c'est une classe d'AWT, la bibliothèque graphique. Un modèle qui l'utilise dépend de l'affichage, au moins sur le papier. Depuis 4.3, plus aucune classe du modèle n'importe `java.awt` ni `javax.swing` : un hôte réseau sans fenêtre (étape 9) pourra faire tourner le jeu.
- *Le changement était-il sûr ?* Oui, et c'est vérifiable : le golden master est **identique à la ligne près**. Trois précautions l'expliquent :
  - `distance` reprend exactement le calcul de `Point` (testé sur plusieurs cas), car les fantômes comparent des distances ;
  - aucune collection ne dépend de l'ordre de hachage (un `record` ne calcule pas son `hashCode` comme `Point`) ;
  - personne ne partageait une même instance de position : chaque corps recevait un `Point` neuf. D21 ne s'était donc jamais produite en jeu, mais elle était possible.
- *Le coût* : 60 fichiers touchés, mécaniquement (`.x` devient `.x()`, `new Point` devient `new Position`). C'est le prix d'un type qu'on a laissé se répandre partout ; d'où l'intérêt d'avoir ses propres types de base tôt dans un projet.
- *Pièges* :
  - **oublier de récupérer le résultat** : `position.translate(1, 0);` seul ne fait plus rien. Il faut écrire `position = position.translate(1, 0);` ;
  - **les noms** : les constantes s'appellent encore `POINT_UP`, `POINT_ZERO`… alors que ce sont des `Position`. Les renommer (par exemple en directions) est prévu à l'étape 8 ;
  - **la frontière** : les vues dessinent toujours avec des entiers (`g.drawImage(image, position.x(), position.y(), …)`). Seules les classes d'essai de `engine/view/tests`, qui placent des composants Swing, gardent `java.awt.Point`.

**✏️ Exercice**

Les constantes `POINT_UP`, `POINT_DOWN`, `POINT_LEFT` et `POINT_RIGHT` sont des directions. Écrivez une énumération `Direction` (UP, DOWN, LEFT, RIGHT, NONE) qui porte son déplacement (`dx`, `dy`) et une méthode `Position move(Position from)`. Quelles méthodes de `Ghost` deviendraient plus lisibles ? (Indice : `setDirection(new Position(getDirection().x(), -getDirection().y()))`.)
