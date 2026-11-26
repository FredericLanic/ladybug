## Vue d'ensemble
| # | Étape | Pourquoi | Patterns ou outils |
|---|---|---|---|
| 0 | Outillage et état de référence | Créer la branche depuis `develop`, voir le code avant d'y toucher, figer le comportement « avant », travailler avec la documentation à jour | Graphify, Context7, checklist et captures |
| 1 | Socle | Faire compiler et démarrer, aligner les versions | – |
| 2 | Lanceur et XML externe | Un démarrage clair, une configuration modifiable sans recompiler | Factory |
| 3 | Tests | Un filet de sécurité avant de refactoriser | JUnit 5 |
| 4 | Découplage du modèle | Un modèle indépendant de Swing, base du réseau | Game Loop, Value Object, Snapshot |
| 5 | `PlayerInput` | Un contrôleur par personnage, plusieurs fantômes joueurs | Strategy |
| 6 | Fantômes configurables | Un fantôme = assemblage de capacités, perception | Composition, Strategy |
| 7 | Niveaux et rendu du labyrinthe | Niveaux en fichiers, rendu lisible et thématisable | Builder, autotiling, Flyweight |
| 7 bis | **Labyrinthe dynamique** (idée de Hammed) | Portes, murs mobiles, niveaux générés ; murs déclenchés par les joueurs avec les pouvoirs (6.7, 9.6) | Observer, Command, Strategy, Builder |
| 8 | Langage moderne, ménage, documentation | Lisibilité, `ARCHITECTURE.md`, test « Jeff » | records, `switch` moderne |
| 9 | **Réseau** | Un poste Ladybug, un ou plusieurs postes fantômes | Strategy (`NetworkInput`) |
| 10 | Optionnel | `jpackage`, JavaFX ou libGDX | – |
