---
name: ladybug-cloture
description: Clôture d'une session ou d'une étape Ladybug (journal, état, dashboard, pages, sauvegarde). Seulement quand Frédéric tape /ladybug-cloture.
disable-model-invocation: true
---

# Clôture Ladybug

Lancé par Frédéric avec `/ladybug-cloture` (éventuellement suivi de `complete`). Les chemins `references/…` sont ceux du skill `ladybug-modernisation`.

## Clôture légère (par défaut, fin de session)
1. Ajoutez 3 à 8 lignes au journal **sans le relire** :
   `cat >> .claude/skills/ladybug-modernisation/references/journal/AAAA-MM-JJ.md <<'EOF'` (titre `## JJ/MM/AAAA — sujet`, puis ce qui a été dit, décidé ou reporté).
   Ajoutez la ligne de titre au sommaire `references/journal/README.md` de la même façon.
2. Si une décision change : modifiez la ligne concernée de `references/decisions.md` ou le fichier d'étape de `references/plan/` (édition ciblée, pas de réécriture).
3. Mettez à jour la ligne « État » du `SKILL.md` de `ladybug-modernisation` si l'étape a changé.

C'est tout. Pas de dashboard, pas de PDF, pas de sauvegarde.

## Clôture complète (`/ladybug-cloture complete`, en fin d'étape seulement)
Faites d'abord la clôture légère, puis :

1. Mettez à jour le dashboard (voir « Dashboard » plus bas) : statuts, mesures, idées, plan en détail et nouvelle entrée de journal. Frédéric veut que **toutes les idées et tout le plan** y soient lisibles.
2. **Sauvegardez tout dans `~/Documents/Claude/ladybug`**, à la demande de Frédéric :
   - lancez `sh .claude/skills/ladybug-modernisation/scripts/sauvegarde.sh <dossier_export>` depuis la racine du dépôt, après l'export ci-dessous (copie du plan, du PDF, du skill, de la mémoire et des conversations en `.jsonl` et `.md`) ;
   - exportez les données du dashboard avec `read_db` (`list`) pour `datasets` et chaque collection `data/ladybug/*`, avec un `out_dir` **vide dans le scratchpad** (l'export refuse d'écraser un fichier existant), puis passez ce dossier à `sauvegarde.sh` : il le fusionne dans `~/Documents/Claude/ladybug/dashboard/donnees` sans rien supprimer, et régénère le **dashboard local**, la carte Ladybug et la page d'accueil `~/Documents/Claude/accueil/index.html` (skill utilisateur `dashboard-local`) ;
   - si la page a changé, copiez aussi `files/index.html` dans `dashboard/page-index.html`.

3. Mettez à jour les **4 documents** (en fin d'étape, plus à chaque PR : choix de Frédéric du 26/11/2026, pour économiser les tokens) :
   - le dashboard « Ladybug Modernisation » (étapes, mesures de `metrics.py`, journal, plan, dette, décisions, idées) ;
   - la page « Ladybug, côté technique » : https://claude.ai/artifact/Fa36n8xrck4G4DjcHbGK1K (pour Jeff et Alex, privée) ;
   - la « Visite guidée de Ladybug » : https://claude.ai/artifact/EVZJcMjMnRfYiiqYR47wFF (pour Jeff, partagée) ;
   - le document Claude Docs « Ladybug — Stratégie de tests autonomes » : https://claude.ai/artifact/4mXUqpsGiwLczZzegYUyUP (section « Bilan ») ;

   puis les copies locales (`~/Documents/Claude/ladybug/pages/`) et la sauvegarde. Une PR fusionnée sans Claude (directement sur GitHub) ne déclenche rien : comparez en début de session le dernier commit de `develop` avec celui noté au dashboard (`data/ladybug/health/commit`).

Le PDF du plan (`references/plan-pdf.html` → `doc/plan-modernisation-ladybug.pdf`) n'est régénéré **que si Frédéric le demande** : il coûte cher (fichier HTML de 52 Ko à éditer).

Économie : pour chaque document, utilisez une édition ciblée (`edit_version`, `db_str_replace`, `write_db` en `update`) plutôt qu'une relecture et une republication complètes.

## Dashboard
Un dashboard privé sur claude.ai, demandé par Frédéric sur le modèle de celui de Halim : https://claude.ai/artifact/SpChAsmu1H63WxzCEYsp3d (modèle « Data »).

Il affiche, dans l'ordre :
- l'avancement des étapes ;
- **le plan en détail** (une section dépliable par titre « ## » de `references/plan/`) ;
- la santé du code (tuiles et graphique par package) ;
- la dette technique ;
- les décisions et questions ouvertes ;
- **les idées** (qui les a proposées, statut, où elles atterrissent dans le plan) ;
- l'outillage ;
- le journal.

Les noms (Jeff, Halim, OXiane) y figurent, à la demande de Frédéric.

**C'est Claude qui le tient à jour**, lors d'une clôture complète. Les données sont des documents de la base du dashboard. Écrivez-les avec `write_db` (en `update` avec `if_version` sur les documents existants) :
- `data/ladybug/steps/sNN` : `status` vaut « À faire », « En discussion », « En cours » ou « Terminée », avec `updated` ;
- `data/ladybug/packages/<package>` et `data/ladybug/health/<key>` : relancez `python3 .claude/skills/ladybug-modernisation/scripts/metrics.py .` et recopiez les valeurs. Mettez aussi à jour la date et le commit dans la `description` des datasets `packages` et `health` ;
- `data/ladybug/debt/Dnn` : `status` passe à « Résolu » ;
- `data/ladybug/decisions/Cnn|Qnn` : une question tranchée devient une décision ;
- `data/ladybug/tooling/<id>` et `data/ladybug/journal/jNN` (nouvelle entrée, `order` croissant) ;
- `data/ladybug/ideas/Inn` : **chaque nouvelle idée** (de Frédéric, d'un collègue ou de Claude), avec `title`, `author`, `status` (« À discuter », « Retenue », « Réalisée », « Écartée »), `step` et `text` ;
- `data/ladybug/plan/pNN` : **à chaque modification du plan**, lancez `python3 .claude/skills/ladybug-modernisation/scripts/plan_dashboard.py .claude/skills/ladybug-modernisation/references/plan <dossier>` puis envoyez les fichiers produits en `batch` (`set`). Si le nombre de sections diminue, supprimez les documents en trop.

Une étape « bis » garde un `num` décimal pour le tri (7 bis → 7.5) et un `label` affiché (« 7 bis »).

Ne touchez au code de la page (`files/index.html`) que si Frédéric demande une modification de la mise en page.
