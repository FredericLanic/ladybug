"""Convertit un fichier de session Claude Code (.jsonl) en Markdown lisible.

Usage : python3 -I transcript.py <session.jsonl> <sortie.md>
Garde les messages de l'utilisateur et les réponses texte de Claude ; résume les appels d'outils en une ligne.
"""
import json
import sys

src, dst = sys.argv[1], sys.argv[2]
out = ["# Conversation Claude Code : projet Ladybug", ""]
for line in open(src, encoding="utf-8"):
    try:
        e = json.loads(line)
    except ValueError:
        continue
    if e.get("type") not in ("user", "assistant") or e.get("isMeta"):
        continue
    msg = e.get("message") or {}
    content = msg.get("content")
    ts = (e.get("timestamp") or "")[:16].replace("T", " ")
    parts = []
    if isinstance(content, str):
        parts.append(content)
    elif isinstance(content, list):
        for c in content:
            if c.get("type") == "text" and c.get("text", "").strip():
                parts.append(c["text"])
            elif c.get("type") == "tool_use":
                inp = c.get("input") or {}
                hint = inp.get("description") or inp.get("file_path") or inp.get("action") or inp.get("query") or ""
                parts.append(f"> _outil : {c.get('name')} — {str(hint)[:120]}_")
    text = "\n\n".join(p for p in parts if p.strip())
    if not text or "<command-name>" in text or text.startswith("<system-reminder>"):
        continue
    who = "**Frédéric**" if e["type"] == "user" else "**Claude**"
    out.append(f"### {who} · {ts}")
    out.append("")
    out.append(text)
    out.append("")
open(dst, "w", encoding="utf-8").write("\n".join(out))
print(f"{len(out)} lignes écrites dans {dst}")
