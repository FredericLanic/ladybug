## Questions ouvertes (à reprendre plus tard, sans blocage)
- **Pouvoirs** : fixes par type de fantôme (XML), ou évolutifs en cours de partie (bonus, niveau) ? *L'architecture permet les deux (capacités = objets remplaçables).*
- **Attribution des fantômes en réseau** : plusieurs joueurs sur le même type, ou un fantôme différent par joueur ? *L'association joueur ↔ fantôme précis permet les deux.*
- **Réseau local seulement, ou Internet aussi** (découverte des parties, NAT, sécurité) ?
- **Classement des options** : lesquelles relèvent de l'état partagé et lesquelles des préférences locales ?
- **Rayons de lumière** : l'écart entre 3,5 et 4 cases dans `CentralView` est-il voulu ?
- **Graphify et documents** : inclure plus tard README, backlog et `.txt` dans le graphe (analyse par le modèle), ou rester en mode code seul ?
- **Q11, labyrinthe dynamique** : prévenir le joueur avant un changement (clignotement du mur) ? Labyrinthe généré symétrique comme les niveaux actuels, ou libre ? Règle quand un mur se ferme sur un personnage (attendre, pousser) ?
- **Graphify en mode « toujours actif »** (`graphify claude install`, avec des hooks qui consultent le graphe automatiquement) : à activer ou non ?
