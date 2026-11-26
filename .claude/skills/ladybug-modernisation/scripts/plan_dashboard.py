#!/usr/bin/env python3
"""Découpe le plan (dossier references/plan/, un fichier par section) en sections (une par titre « ## ») et écrit un fichier
JSON par section, prêt à être envoyé dans le tableau de bord (collection
data/ladybug/plan) avec l'outil Artifact, action write_db, db_op batch.

Usage : python3 plan_dashboard.py <dossier plan/ ou plan.md> <dossier de sortie>
"""
import json
import re
import sys
from pathlib import Path


def split_sections(text: str):
    sections, current = [], None
    for line in text.splitlines():
        if line.startswith("## "):
            current = {"heading": line[3:].strip(), "lines": []}
            sections.append(current)
        elif line.startswith("# "):
            continue  # titre du document
        elif current is not None:
            current["lines"].append(line)
        else:
            # texte avant le premier « ## » : rattaché à une section d'introduction
            if line.strip():
                sections.append(current := {"heading": "Introduction", "lines": [line]})
    return sections


def main():
    plan, out = Path(sys.argv[1]), Path(sys.argv[2])
    out.mkdir(parents=True, exist_ok=True)
    for old in out.glob("p*.json"):
        old.unlink()
    if plan.is_dir():
        text = "\n".join(f.read_text() for f in sorted(plan.glob("*.md")) if f.name != "README.md")
    else:
        text = plan.read_text()
    for order, section in enumerate(split_sections(text), start=1):
        heading = section["heading"]
        match = re.match(r"Étape (\d+(?: bis)?)\s*:\s*(.*)", heading)
        num, title = (match.group(1), match.group(2)) if match else ("", heading)
        body = "\n".join(section["lines"]).strip()
        body = re.sub(r"\n---\s*$", "", body).strip()
        doc = {"order": order, "num": num, "title": title[:1].upper() + title[1:], "md": body}
        (out / f"p{order:02d}.json").write_text(json.dumps(doc, ensure_ascii=False, indent=1))
        print(f"p{order:02d}  {num or '-':>6}  {title}  ({len(body)} car.)")


if __name__ == "__main__":
    main()
