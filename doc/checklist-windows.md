# Checklist : essai de Ladybug sous Windows

> Rédigée le 19/11/2026, après l'étape 2 (démarrage par Spring Boot) et 3.5.3d (nouvelle bibliothèque de manette, Jamepad 2.30). Le jeu n'a été essayé que sur Mac (Apple Silicon) ; la CI tourne sous Linux. Cocher chaque ligne, noter ce qui ne va pas dans la colonne « Remarque ».

## 0. Préparation

| # | À faire | OK ? | Remarque |
|---|---|---|---|
| 0.1 | Java **21** installé : `java -version` affiche 21 | | |
| 0.2 | Récupérer le jar (`mvn clean package`, ou une copie de `target/ladybug-0.8.0-SNAPSHOT.jar`) | | |
| 0.3 | Ouvrir une invite de commandes **dans le dossier du jar** | | |
| 0.4 | Lancer : `java -jar ladybug-0.8.0-SNAPSHOT.jar` | | |

## 1. Démarrage (lire la console)

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 1.1 | La bannière affiche **« Ladybug by Kykox 0.8.0-SNAPSHOT »** | | |
| 1.2 | `Sounds preloaded: 16/16` (sinon : quels sons manquent ?) | | |
| 1.3 | `Gamepad support enabled (… controller(s) connected)` ; **aucune** ligne « Manette Xbox désactivée » | | |
| 1.4 | `Spring XML configuration: classpath:application-context.xml` et `Launch mode: LOCAL` | | |
| 1.5 | Aucune ligne `ERROR` ni `Exception` | | |

## 2. Affichage

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 2.1 | La fenêtre s'ouvre sur l'écran où est la souris (essayer avec deux écrans si possible) | | |
| 2.2 | Le labyrinthe est **entier**, rien n'est caché par la barre des tâches | | |
| 2.3 | La zone du score est collée sous le labyrinthe, le tout centré | | |
| 2.4 | Le dessin de Ladybug et des vies est lisible | | |
| 2.5 | F1 : l'aide affiche 12 lignes, toutes dans le labyrinthe | | |

## 3. Son

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 3.1 | La musique du début de niveau est jouée **une seule fois** | | |
| 3.2 | Sons en partie : manger, méga-gomme, fantôme mangé, fruit, mort, sans saccade | | |
| 3.3 | F2 coupe puis remet le son | | |

## 4. Clavier

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 4.1 | S lance la partie, N recommence au niveau 1, O reprend le niveau sauvegardé | | |
| 4.2 | Flèches : Ladybug se déplace dans le bon sens | | |
| 4.3 | **Pause** (la vraie touche) puis **P** : pause puis reprise | | |
| 4.4 | Échap en partie, puis N (retour au jeu) ; Échap puis O (fin de partie) | | |
| 4.5 | F3, F4, F5 (apparence), F6 (lampe), F7 (camouflage), Entrée (mode debug) | | |
| 4.6 | 2 sur l'écran de présentation, puis Z Q S D : le fantôme joueur se déplace | | |

## 5. Manette Xbox filaire (USB)

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 5.1 | Brancher la manette **avant** de lancer le jeu : la console compte 1 manette (1.3) | | |
| 5.2 | Présentation : **A** lance la partie, **Back** (bouton « vue ») affiche l'aide de la manette | | |
| 5.3 | Question « reprendre le niveau ? » : **X** = niveau 1, **Y** = niveau sauvegardé | | |
| 5.4 | **Stick gauche : haut, bas, gauche, droite dans le bon sens** (point à surveiller : le sens haut/bas a été corrigé pour la nouvelle bibliothèque, jamais vérifié sous Windows) | | |
| 5.5 | Stick droit : mêmes directions | | |
| 5.6 | **Start** : pause puis reprise | | |
| 5.7 | **X** en partie : « arrêter la partie ? », puis **B** (retour) ou **Y** (fin) | | |
| 5.8 | Croix : haut = son, bas = peau de Ladybug, gauche / droite = chapeau / bandeau | | |
| 5.9 | Vibrations : méga-gomme et téléportation | | |
| 5.10 | Débrancher puis rebrancher la manette en jeu : messages « déconnectée / connectée » en bas de l'écran | | |
| 5.11 | Mode 2 joueurs (**Y** sur la présentation) avec **deux manettes** si possible : la 2e dirige le fantôme | | |
| 5.12 | Présentation : **B** quitte le jeu | | |

## 6. Fichiers et configuration

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 6.1 | Niveau sauvegardé dans `%TEMP%\labybug\numLevel.txt` après une partie | | |
| 6.2 | Un `ladybug.properties` avec `ghosts.hat=0`, posé dans le dossier de lancement : chapeau retiré, ligne « Game properties: … (external, overrides the jar) » | | |
| 6.3 | Un `application-context.xml` invalide dans ce dossier : message d'erreur clair, puis arrêt | | |

## 7. Sortie

| # | À vérifier | OK ? | Remarque |
|---|---|---|---|
| 7.1 | Échap sur l'écran de présentation ferme le jeu, sans erreur dans la console | | |

---

**En cas de problème** : copier la console dans un fichier (`java -jar ladybug-0.8.0-SNAPSHOT.jar > ladybug.log 2>&1`) et le donner à Claude.
