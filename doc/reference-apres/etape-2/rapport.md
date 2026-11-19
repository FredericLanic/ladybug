# Passage du robot du 19/11/2026 16:27

Jar : `target/ladybug-0.6.0-SNAPSHOT.jar`. Logs complets : `ladybug.log`.

## Lancement
- Jeu lancé (pid 87829), fenêtre : java.awt.Rectangle[x=0,y=33,width=1470,height=923]

## 1. Démarrage
- 📷 `01-presentation.png` (statut : PROGRAM_PRESENTATION)
- 📷 `02-aide-f1-enfoncee.png` (statut : PROGRAM_PRESENTATION)
- 📷 `03-aide-f1-relachee.png` (statut : PROGRAM_PRESENTATION)
- 📷 `04-choix-2-joueurs.png` (statut : PROGRAM_PRESENTATION)
- 📷 `05-choix-1-joueur.png` (statut : PROGRAM_PRESENTATION)

## Lancement d'une partie solo
- 📷 `06-en-jeu-depart.png` (statut : IN_GAME)

## 2.1 Déplacement de Ladybug
- 📷 `07-deplacement-←.png` (statut : IN_GAME)
- 📷 `08-deplacement-↑.png` (statut : IN_GAME)
- 📷 `09-deplacement-→.png` (statut : IN_GAME)
- 📷 `10-deplacement-↓.png` (statut : IN_GAME)

## 5. Touches de fonction
- 📷 `11-f2-son-avant.png` (statut : IN_GAME)
- 📷 `12-f2-son-apres.png` (statut : IN_GAME)
- F2 : 0,01 % des pixels ont changé
- 📷 `13-f3-logo-avant.png` (statut : IN_GAME)
- 📷 `14-f3-logo-apres.png` (statut : IN_GAME)
- F3 : 0,06 % des pixels ont changé
- 📷 `15-f4-bandeau-avant.png` (statut : IN_GAME)
- 📷 `16-f4-bandeau-apres.png` (statut : IN_GAME)
- F4 : 0,12 % des pixels ont changé
- 📷 `17-f5-chapeau-avant.png` (statut : IN_GAME)
- 📷 `18-f5-chapeau-apres.png` (statut : IN_GAME)
- F5 : 0,27 % des pixels ont changé
- 📷 `19-f6-lampe-avant.png` (statut : IN_GAME)
- 📷 `20-f6-lampe-apres.png` (statut : IN_GAME)
- F6 : 12,70 % des pixels ont changé
- 📷 `21-f7-camouflage-avant.png` (statut : IN_GAME)
- 📷 `22-f7-camouflage-apres.png` (statut : IN_GAME)
- F7 : 0,41 % des pixels ont changé
- 📷 `23-entree-debug-avant.png` (statut : IN_GAME)
- 📷 `24-entree-debug-apres.png` (statut : IN_GAME)
- ⏎ : 0,10 % des pixels ont changé

## 5.6 Pause
- Attention : les claviers Mac n'ont pas de touche Pause. Le robot envoie VK_PAUSE, mais macOS ne la transmet peut-être pas : comparer avec le bruit des autres captures (≈ 0,9 %).
- 📷 `25-pause-1.png` (statut : IN_GAME)
- 📷 `26-pause-2.png` (statut : IN_GAME)
- Différence entre les deux captures en pause : 0,00 % des pixels (≈ 0 attendu)
- 📷 `27-pause-reprise.png` (statut : IN_GAME)
- Différence après reprise : 0,13 % des pixels (> 0 attendu)

## 5.7 Échap
- 📷 `28-echap-confirmation.png` (statut : ASK_FORCE_END_GAME)
- N : retour en partie ✅

## 6. Rendus des niveaux (12 parties)
- Le rendu n'est pas écrit dans les logs : il est identifié en regardant les captures.
- 📷 `29-rendu-partie-1.png` (statut : IN_GAME)
- 📷 `30-rendu-partie-2.png` (statut : IN_GAME)
- 📷 `31-rendu-partie-3.png` (statut : IN_GAME)
- 📷 `32-rendu-partie-4.png` (statut : IN_GAME)
- 📷 `33-rendu-partie-5.png` (statut : IN_GAME)
- 📷 `34-rendu-partie-6.png` (statut : IN_GAME)
- 📷 `35-rendu-partie-7.png` (statut : IN_GAME)
- 📷 `36-rendu-partie-8.png` (statut : IN_GAME)
- 📷 `37-rendu-partie-9.png` (statut : IN_GAME)
- 📷 `38-rendu-partie-10.png` (statut : IN_GAME)
- 📷 `39-rendu-partie-11.png` (statut : IN_GAME)
- 📷 `40-rendu-partie-12.png` (statut : IN_GAME)

## 4.1 Mode 2 joueurs
- 📷 `41-2-joueurs-depart.png` (statut : IN_GAME)
- 📷 `42-2-joueurs-fantome-q.png` (statut : IN_GAME)
- 📷 `43-2-joueurs-fantome-z.png` (statut : IN_GAME)
- 📷 `44-2-joueurs-fantome-d.png` (statut : IN_GAME)
- 📷 `45-2-joueurs-fantome-s.png` (statut : IN_GAME)

## Sortie du jeu
- Échap en présentation : le jeu se ferme ✅ (code 0)

## Statuts vus dans les logs, dans l'ordre

PROGRAM_START → PROGRAM_STARTING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION → PROGRAM_ASK_KEEP_PREVIOUS_GAME_LEVEL → GAME_START → GAME_STARTING → LEVEL_START → GAME_START → LEVEL_STARTING → IN_GAME → ASK_FORCE_END_GAME → GAME_END → GAME_ENDING → PROGRAM_PRESENTATION_START → PROGRAM_PRESENTATION
