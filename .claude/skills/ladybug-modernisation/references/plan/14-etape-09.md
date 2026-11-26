## Étape 9 : mode réseau (plus tard, progressivement)
Architecture : **un hôte qui fait autorité**.
- L'hôte fait tourner le seul `GameModel`.
- Les clients envoient uniquement leurs entrées.
- L'hôte renvoie un `GameSnapshot` à chaque tick.

9.1 Protocole simple : sockets TCP et records sérialisés, sans framework lourd.
9.2 `NetworkInput`, une nouvelle implémentation de `PlayerInput` (étape 5).
9.3 Lanceur : modes **héberger** (`--host`) et **rejoindre** (`--join host:port`). Le client n'assemble que la vue et l'envoi des entrées.
9.4 Chaque client a sa propre vue, avec son personnage comme observateur. L'hôte peut n'envoyer à chaque client que ce que son personnage perçoit (anti-triche).
9.5 Les éléments décidés par l'hôte (mode lampe, tirages aléatoires) sont transmis. Les préférences restent locales.
9.6 **Labyrinthe dynamique en réseau** : l'hôte seul décide des changements de murs (étape 7 bis), qui voyagent dans le `GameSnapshot`. Un client qui utilise le pouvoir « agir sur les murs » (6.7) envoie une demande, et l'hôte la valide.
