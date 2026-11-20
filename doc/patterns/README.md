# Fiches de patterns de Ladybug

Chaque fiche explique une technique introduite dans Ladybug, avec le vrai code du jeu, à trois niveaux de lecture :
- 🟢 junior : l'idée en une image ;
- 🔵 confirmé : le code de Ladybug, avant et après ;
- 🟣 vétéran : le recul, les limites et les alternatives.

Chaque fiche se termine par un petit exercice facultatif.

| Fiche | En une phrase | Étape |
|---|---|---|
| [Test unitaire](test-unitaire.md) | Vérifier automatiquement une petite partie du jeu, sans le lancer. | 3 |
| [Game Loop / Update Method](game-loop.md) | Une seule horloge, la boucle du jeu, fait avancer tout ce qui dépend du temps, battement par battement. | 3 (3.5.1) |
| [Configuration externalisée](configuration-externalisee.md) | Régler le jeu (fantômes, apparence) avec des fichiers posés à côté, sans recompiler ; profil « gui » pour charger le jeu sans écran. | 2 |
| [Golden master](golden-master.md) | Figer le journal d'une partie de référence pour voir tout changement de comportement, au battement près. | 3 (3.5.2) |
| [Test de mutation](test-de-mutation.md) | Glisser de petites erreurs dans le code pour vérifier que les tests les détectent : tester les tests. | 3 (3.5.5) |
| [Value Object](value-object.md) | Une position immuable (`record Position`) : se déplacer, c'est prendre une nouvelle position ; le modèle n'utilise plus rien d'AWT. | 4 (4.3) |
