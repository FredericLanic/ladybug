# Fiche de design pattern : modèle validé

Chaque pattern introduit dans Ladybug a sa fiche dans `doc/patterns/<nom-du-pattern>.md`. Rédigez-la **au moment où le pattern entre dans le code**, avec le vrai code de Ladybug, jamais un exemple abstrait.

## Structure

```markdown
### <Pattern> : <ce qu'il apporte, en une phrase>

**🟢 L'idée en une image (junior)**
Une analogie du quotidien en 2 ou 3 phrases, sans jargon.

**🔵 Dans Ladybug (confirmé)**
- Le problème concret dans notre code (fichier, méthode).
- *Avant* : l'extrait de code d'origine.
- *Après* : le code refactorisé.
- Un schéma Mermaid simple si c'est utile.
- Où le trouver dans le code : liens `fichier:ligne`.

**🟣 Le recul (senior / vétéran)**
- Le principe SOLID concerné.
- Quand **ne pas** l'utiliser.
- Les alternatives en Java moderne (lambda, `sealed`, records…).
- Les pièges classiques.

**✏️ Exercice**
Un petit exercice facultatif, réalisable dans Ladybug.
```

## Exemple validé par Frédéric : Strategy

### Strategy : changer de comportement sans changer de classe

**🟢 L'idée en une image (junior)**
Un fantôme, c'est un corps et une manette. La manette décide où aller : une manette « poursuite », une manette « embuscade », une manette « balade ». Pour changer la personnalité d'un fantôme, on change de manette, pas de fantôme.

**🔵 Dans Ladybug (confirmé)**
*Avant* : `Ghost.moveByBehaviour()` choisit le comportement avec un `switch`. Pour en ajouter un, il faut modifier `Ghost`.
```java
switch (behaviour) {
    case SMART -> smartMoving(ladybug, screenData);
    case AGGRESSIVE -> moveTo(ladybug.getPositionBlock(), screenData);
    default -> defaultMoving(screenData);
}
```
*Après* : le fantôme délègue à l'objet qu'on lui a donné, sans savoir lequel c'est.
```java
public interface GhostMoveStrategy {
    /** Calcule la prochaine direction du fantôme. */
    Point nextDirection(Ghost ghost, Ladybug ladybug, ScreenData screen);
}
// dans Ghost :
setDirection(moveStrategy.nextDirection(this, ladybug, screenData));
```
Le choix se fait dans le XML (`<property name="moveStrategy" ref="ChaseStrategy"/>`), sans recompiler.

**🟣 Le recul (senior / vétéran)**
- *Principe* : ouvert/fermé (le « O » de SOLID). On ajoute un comportement sans modifier `Ghost`.
- *Quand ne pas l'utiliser* : avec deux comportements figés pour toujours, un `switch` sur un `enum` scellé reste plus lisible. Le pattern se justifie ici parce qu'on veut configurer les fantômes en XML et brancher l'IA, le clavier ou le réseau.
- *En Java moderne* : une interface fonctionnelle et une lambda suffisent pour une stratégie sans état. On garde une classe dès qu'elle porte des paramètres (rayon, probabilités).
- *Piège* : une stratégie qui modifie directement le fantôme. Elle doit **renvoyer une décision** (une direction) et laisser le fantôme l'appliquer, sinon les tests et le réseau se compliquent.

**✏️ Exercice** : écrire une `PatrolStrategy` qui fait tourner un fantôme autour de sa zone de départ, puis l'activer dans le XML.
