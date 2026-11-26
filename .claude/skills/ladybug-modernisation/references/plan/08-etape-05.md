## Étape 5 : un contrôleur par personnage (`PlayerInput`)
> **Analyse du 20/11/2026** : la demande de Ladybug est portée par Ladybug ; celle du fantôme humain traverse 4 classes de stratégie, n'est jamais remise à zéro et vaut pour tous les fantômes humains ; IA dans `Ghost` ; le drapeau « humain » rangé dans les images a déjà disparu en 4.2. Décisions : IA laissée dans `Ghost` jusqu'à l'étape 6 ; un seul fantôme jouable en local pendant l'étape 5 (le modèle en accepte N) ; tirage au hasard gardé ; puis **I20** (choisir son fantôme, plusieurs fantômes en local). Critère : golden master identique.

5.1 **Fait le 20/11/2026** (commit `eff99dd`) : package `input` (`PlayerInput`, boîte aux lettres d'un joueur ; `PlayerInputs` ; `InputSource`, l'appareil, noté dans le journal du jeu à chaque changement) ; Ladybug lit sa boîte, clavier et manette 1 y écrivent, `setLadybugRequest` retiré du contrat. Énoncé d'origine : interface `PlayerInput` (« quelle direction veut prendre ce personnage »), avec trois implémentations :
- `KeyboardInput` (flèches ou ZQSD) ;
- `XboxInput` ;
- `AiInput` (les déplacements IA actuels).

5.2 **Fait le 20/11/2026** (commit `d4974c5`) : chaque fantôme lit sa propre `PlayerInput` ; la demande unique qui traversait 4 classes de stratégie est supprimée (`setGhostRequest` retiré du contrat) ; `GhostsGroup.move` / `moveWithoutPlayerRequests` ; ZQSD et manette 2 écrivent dans la boîte du fantôme humain ; un fantôme qui devient humain repart sans ordre. Questions ouvertes pour 5.3 : fantôme humain sans vie (redevient IA ?) ; bandeau à plusieurs fantômes. Énoncé d'origine : chaque `Ladybug`/`Ghost` porte son `PlayerInput`. Ce changement :
- **supprime** le flag `computed` stocké sur l'enum `GhostsBodyImages` (CTEC1) ;
- supprime la `ghostRequest` unique de `GhostsGroup`.

5.3 **Fait le 20/11/2026** : le modèle accepte plusieurs fantômes humains (`getNbrPlayers`, `PlayerInputs.ghost(nom)`) ; règle **C50** : un fantôme humain sans vie revient à l'ordinateur, la partie des fantômes s'arrête quand tous sont éliminés. Écran et touches inchangés jusqu'à I20. Énoncé d'origine : possibilité d'avoir **plusieurs fantômes joués par des humains** en local (préparation du réseau).
5.4 **Fait le 20/11/2026** : `setHumanPlayer(Ghost)` ; le tirage d'une couleur (inchangé, golden master identique) désigne le premier fantôme de cette couleur, ou un autre si le XML n'en a pas (avant : aucun fantôme humain). Énoncé d'origine : un joueur est associé à **un fantôme précis**, pas à un type.
