# Checklist de jeu : état de référence

Une checklist manuelle à dérouler avant et après chaque grande étape (1, 4, 7…) pour vérifier que le jeu se comporte toujours de la même façon. Le projet n'a pas encore de tests automatisés : c'est notre filet de sécurité en attendant l'étape 3.

Pour chaque point, notez `OK`, `KO` (avec ce qui se passe) ou `–` si le point ne s'applique pas.

## Conditions du passage « avant » (18/11/2026)

| | |
|---|---|
| Branche | `modernisation` (base `develop`, commit `f456c22`) |
| JDK | Amazon Corretto 21.0.8 (arm64) |
| Build | `mvn clean package -DskipTests` via Maven Central (refait le 18/11 au soir : contenu identique, test T7), **Lombok passé temporairement de 1.18.22 à 1.18.36** (1.18.22 ne compile pas en JDK 21), pom restauré ensuite |
| Lancement | `java -jar target/ladybug-0.6.0-SNAPSHOT.jar` |
| Machine | Mac Apple Silicon, sans manette |
| Démarrage | ✅ sans erreur dans les logs, pas de plantage lié à la manette (constaté par Claude) |

## 1. Démarrage

| # | Vérification | Avant (18/11) | Après étape 1 |
|---|---|---|---|
| 1.1 | La fenêtre s'ouvre et s'affiche correctement (pas de zone coupée ni décalée) | OK (robot 18/11, fenêtre 1470×923 sous la barre de menus) | OK (robot, `doc/reference-apres/etape-1/`) |
| 1.2 | L'écran de présentation s'affiche | OK (robot 18/11, `01`) | OK (robot, `doc/reference-apres/etape-1/`) |
| 1.3 | La touche F1 affiche l'aide | OK (robot 18/11, `02`, visible tant que F1 est enfoncée ; l'aide ne cite ni F6, ni F7, ni Pause, ni Entrée) | OK (robot, `doc/reference-apres/etape-1/`) |
| 1.4 | Les touches 1 et 2 choisissent le nombre de joueurs | OK (robot 18/11, `04`, `05`, et partie à 2 joueurs `41`) | OK (robot, `doc/reference-apres/etape-1/`) |

## 2. Partie en solo

| # | Vérification | Avant (18/11) | Après étape 1 |
|---|---|---|---|
| 2.1 | Ladybug se déplace avec les flèches | OK (robot 18/11, `06` à `10`) | OK (robot, `doc/reference-apres/etape-1/`) |
| 2.2 | Manger une gomme : le score augmente, avec le son | OK (Frédéric 18/11) | OK (Frédéric 18/11) |
| 2.3 | Manger une méga-gomme : les fantômes deviennent « stressés » (couleur, ralentis), puis clignotent | OK (Frédéric 18/11 ; 4 « Super power is up » dans les logs) | OK (Frédéric 18/11 ; 4 « Super power is up ») |
| 2.4 | Manger un fantôme stressé : il retourne à son point de régénération | OK (Frédéric 18/11) | OK (Frédéric 18/11) |
| 2.5 | Toucher un fantôme normal : Ladybug meurt (animation, son), puis repart de son point de départ | OK (Frédéric 18/11 ; « Ladybug is dead » dans les logs) | OK (Frédéric 18/11 ; 7 « Ladybug is dead ») |
| 2.6 | Une vie supplémentaire est gagnée avec l'expérience | OK (Frédéric 18/11 ; 4 « New life for Ladybug » sans aucune mort) | OK (Frédéric 18/11 ; 4 « New life for Ladybug ») |
| 2.7 | Les fruits apparaissent et peuvent être mangés | OK (Frédéric 18/11) | OK (Frédéric 18/11) |
| 2.8 | Les téléporteurs fonctionnent (pour Ladybug et pour les fantômes) | OK (Frédéric 18/11) | OK (Frédéric 18/11) |
| 2.9 | Le niveau se termine quand toutes les gommes sont mangées, puis le suivant démarre | OK (partie du 18/11 : niveau 1 → niveau 2) | OK (partie du 18/11 : niveau 1 → niveau 2) |
| 2.10 | Fin de partie (plus de vies) : message avec le niveau atteint | OK (Frédéric 18/11 ; « Ladybug lost the game » à 19 h 12) | OK (Frédéric 18/11 ; « Ladybug lost the game ») |

## 3. Les fantômes

Astuce : **Entrée** (mode debug) affiche à gauche, pour chaque fantôme, son statut, son indice de vitesse (en rouge s'il est au moins aussi rapide que Ladybug) et son mode de déplacement (`MOVE_TO` en rouge = il chasse une cible).

| # | Vérification | Avant (18/11) | Après étape 1 |
|---|---|---|---|
| 3.1 | Blinky (rouge) poursuit Ladybug et accélère quand il reste peu de gommes | OK (Frédéric 18/11, panneau debug) | OK (Frédéric 18/11) |
| 3.2 | Pinky (rose) cherche à passer devant Ladybug | OK (Frédéric 18/11, panneau debug) | OK (Frédéric 18/11) |
| 3.3 | Inky (cyan) et Clyde (orange) errent, puis réagissent quand Ladybug approche | OK (Frédéric 18/11, panneau debug) | OK (Frédéric 18/11) |

## 4. Mode 2 joueurs

| # | Vérification | Avant (18/11) | Après étape 1 |
|---|---|---|---|
| 4.1 | Un fantôme se dirige avec Z, Q, S et D | OK (robot 18/11, `41` à `45` ; en mode lampe, la zone éclairée suit aussi le fantôme joueur) | OK (robot, `doc/reference-apres/etape-1/`) |
| 4.2 | Le fantôme joueur peut manger Ladybug | OK (Frédéric 18/11) | OK (Frédéric 18/11) |

## 5. Touches de fonction

| # | Vérification | Avant (18/11) | Après étape 1 |
|---|---|---|---|
| 5.1 | F2 : son activé ou coupé | OK (robot 18/11, message « son éteint / son allumé » en bas à droite) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.2 | F3 : logo du T-shirt affiché ou retiré | OK (robot 18/11, `13`/`14`, la « peau » de Ladybug change) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.3 | F4 : bandeau des fantômes | OK (robot 18/11, `15`/`16`, le cache-œil des fantômes change) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.4 | F5 : chapeau des fantômes | OK (robot 18/11, `17`/`18`, le bandana disparaît) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.5 | F6 : mode « lampe allumée » | OK (robot 18/11, `19`/`20`) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.6 | Pause : met le niveau en pause et le reprend | **KO sur Mac** (robot 18/11, `25`/`26` : les fantômes bougent). Il n'y a pas de touche Pause sur un clavier Mac, et `VK_PAUSE` envoyé par le robot n'arrive pas au jeu : la pause est inaccessible | KO sur Mac, comme avant (robot) |
| 5.7 | Échap : fin du niveau ou sortie du jeu | OK (partie de Frédéric, confirmé par le robot : Échap → N reprend, Échap → O arrête, Échap sur la présentation ferme le jeu, code 0) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.9 | F7 : camouflage des fantômes | OK (robot 18/11, `21`/`22`, les fantômes deviennent sombres) | OK (robot, `doc/reference-apres/etape-1/`) |
| 5.8 | Entrée : mode debug | OK (robot 18/11, `23`/`24`, cercles de debug autour des fantômes) | OK (robot, `doc/reference-apres/etape-1/`) |

## 6. Rendus des niveaux

Sur `develop`, le rendu est tiré au hasard parmi quatre (`LevelRendering`). Notez lesquels vous avez vus, et faites une capture de chacun dans `doc/reference-avant/`.

| # | Rendu | Vu ? | Capture | Après étape 1 |
|---|---|---|---|---|
| 6.1 | Lignes (`LINES_RENDERING`) | Oui (parties 5 et 9) | `33`, `37` (en mode lampe) | Pas sorti au tirage (0 partie sur 13) |
| 6.2 | Tuiles 492 (`TILES_RENDERING_492`) | Oui (8 parties sur 12) | `29`, `39` (sans lampe) ; `30`, `32`, `34`, `35`, `36`, `40` (lampe) | Oui (`32`, `34`, `38`) |
| 6.3 | Tuiles 584 (`TILES_RENDERING_584`) | Oui (partie à 2 joueurs) | `41` à `45` (lampe) | Oui (`29`, `35`, `37`, partie solo et 2 joueurs) |
| 6.4 | Lignes rouges (`TILES_RENDERING_REDLINES`) | Oui (parties 3 et 10, et la partie solo) | `06` à `28`, `31`, `38` | Oui (`30`, `31`, `33`, `36`, `39`, `40`) |

## Remarques

_Tout comportement étrange, même s'il existait déjà avant : il vaut mieux le noter maintenant que le prendre ensuite pour une régression._

- **Passage du robot du 18/11/2026** : captures et `rapport.md` dans `doc/reference-avant/2026-11-18/` (45 captures, environ 3 Mo). Script : `doc/reference-avant/robot/LadybugRobot.java`.
- **Pause inaccessible sur Mac** : la seule touche prévue est `VK_PAUSE`, absente des claviers Mac (voir 5.6).
- **Mode lampe tiré au hasard** à chaque niveau (`LitLampMode.isLitLampMode`), avec une probabilité qui augmente avec le niveau. Dès le niveau 1, il arrive qu'on ne voie que la zone autour des joueurs.
- **Aide (F1) incomplète** : elle ne présente ni F6, ni F7, ni Pause, ni Entrée.
- **Retour inexpliqué en jeu** : une fois, le statut est revenu de `ASK_FORCE_END_GAME` à `IN_GAME` 0,24 s après Échap, sans touche N envoyée (premier passage du robot, 17 h 42). Pas reproduit ; à surveiller.
- Les pourcentages de pixels modifiés du rapport sont peu parlants (bruit d'animation d'environ 1 %) : la vérification se fait en regardant les captures.
