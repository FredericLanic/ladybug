#!/bin/sh
# Sauvegarde tout le travail Ladybug avec Claude dans ~/Documents/Claude/ladybug.
# Copie seulement (rien n'est déplacé ni supprimé dans les originaux).
# Usage : sh .claude/skills/ladybug-modernisation/scripts/sauvegarde.sh   (depuis la racine du dépôt ladybug)
# Les données du dashboard s'exportent à part, par Claude (read_db avec out_dir), dans $T/dashboard/donnees.
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
echo "Sauvegarde à jour : $T ($(find "$T" -type f | wc -l | tr -d ' ') fichiers)"
