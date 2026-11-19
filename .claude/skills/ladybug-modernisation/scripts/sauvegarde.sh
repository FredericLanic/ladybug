#!/bin/sh
# Sauvegarde tout le travail Ladybug avec Claude dans ~/Documents/Claude/ladybug.
# Copie seulement (rien n'est déplacé ni supprimé dans les originaux).
# Usage : sh .claude/skills/ladybug-modernisation/scripts/sauvegarde.sh [dossier_export]   (depuis la racine du dépôt)
# Les données du dashboard s'exportent à part, par Claude (read_db avec out_dir) dans un dossier temporaire
# vide (l'export refuse d'écraser un fichier existant) ; ce dossier est passé en argument et fusionné dans
# $T/dashboard/donnees (ajout et mise à jour, jamais de suppression). Puis le dashboard local, la carte
# Ladybug et la page d'accueil ~/Documents/Claude/accueil sont régénérés (skill dashboard-local).
set -e
P="$(cd "$(dirname "$0")/../../../.." && pwd)"          # racine du dépôt ladybug
SKILL="$P/.claude/skills/ladybug-modernisation"
CC="$HOME/.claude/projects/-Users-fredericlanic-Documents-perso-ladybug"
T="$HOME/Documents/Claude/ladybug"

mkdir -p "$T/plan" "$T/skill" "$T/memoire" "$T/dashboard" "$T/conversation"
cp "$HOME/.claude/plans/linked-cooking-valley.md" "$T/plan/plan-modernisation.md"
cp "$P/doc/plan-modernisation-ladybug.pdf" "$T/plan/"
rsync -a --delete "$SKILL/" "$T/skill/"
cp "$CC"/memory/*.md "$T/memoire/"

# Conversations : copie brute (.jsonl) et version lisible (.md), une par session
for J in "$CC"/*.jsonl; do
  [ -f "$J" ] || continue
  D=$(head -c 2000000 "$J" | grep -o '"timestamp":"[0-9-]*' | head -1 | cut -d'"' -f4)
  ID=$(basename "$J" .jsonl | cut -c1-8)
  cp "$J" "$T/conversation/session-$D-$ID.jsonl"
  python3 -I "$SKILL/scripts/transcript.py" "$J" "$T/conversation/conversation-$D-$ID.md" >/dev/null
done
find "$T/dashboard" -name '.*,by-others' -delete 2>/dev/null || true

# Dashboard local (miroir du dashboard claude.ai) : fusion de l'export, image de la carte, génération
if [ -n "$1" ] && [ -d "$1" ]; then
  rsync -a --exclude '.*' "$1/" "$T/dashboard/donnees/"
fi
IMG=$(ls "$P"/doc/reference-apres/*/06-en-jeu-depart.png 2>/dev/null | tail -1)
[ -n "$IMG" ] && cp "$IMG" "$T/dashboard/carte.png"
GEN="$HOME/.claude/skills/dashboard-local/scripts/generer.py"
[ -f "$GEN" ] && [ -f "$T/dashboard/projet.json" ] && python3 -I "$GEN" projet "$T/dashboard"
echo "Sauvegarde à jour : $T ($(find "$T" -type f | wc -l | tr -d ' ') fichiers)"
